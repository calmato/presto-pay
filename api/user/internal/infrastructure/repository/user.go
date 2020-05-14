package repository

import (
	"context"
	"strings"
	"time"

	"github.com/calmato/presto-pay/api/user/internal/domain/user"
	"github.com/calmato/presto-pay/api/user/lib/firebase/authentication"
	"github.com/calmato/presto-pay/api/user/lib/firebase/firestore"
	"github.com/calmato/presto-pay/api/user/middleware"
	"golang.org/x/xerrors"
	"google.golang.org/api/iterator"
)

type userRepository struct {
	auth      *authentication.Auth
	firestore *firestore.Firestore
}

// NewUserRepository - UserRepositoryの生成
func NewUserRepository(fa *authentication.Auth, fs *firestore.Firestore) user.UserRepository {
	return &userRepository{
		auth:      fa,
		firestore: fs,
	}
}

func (ur *userRepository) Authentication(ctx context.Context) (*user.User, error) {
	userReference := getUserReference()

	t, err := getToken(ctx)
	if err != nil {
		return nil, err
	}

	uid, err := ur.auth.VerifyIDToken(ctx, t)
	if err != nil {
		return nil, err
	}

	u := &user.User{}

	doc, err := ur.firestore.Get(ctx, userReference, uid)
	if err != nil {
		if err = doc.DataTo(u); err != nil {
			return nil, err
		}
	} else {
		// err: Firebase Authentication にはデータがあるが、Firestore にはない
		// -> Authentication のデータを Firestore に登録
		au, err := ur.auth.GetUserByUID(ctx, uid)
		if err != nil {
			return nil, err
		}

		if au.UserInfo == nil {
			return nil, xerrors.New("UserInfo is not exists in Firebase Authentication")
		}

		current := time.Now()

		u.ID = au.UserInfo.UID
		u.Name = au.UserInfo.DisplayName
		u.Username = getName(au.UserInfo.UID)
		u.Email = au.UserInfo.Email
		u.ThumbnailURL = au.UserInfo.PhotoURL
		u.CreatedAt = current
		u.UpdatedAt = current

		if err = ur.firestore.Set(ctx, userReference, u.ID, u); err != nil {
			return nil, err
		}
	}

	return u, nil
}

func (ur *userRepository) Create(ctx context.Context, u *user.User) error {
	userReference := getUserReference()

	_, err := ur.auth.CreateUser(ctx, u.ID, u.Email, u.Password)
	if err != nil {
		return err
	}

	if err = ur.firestore.Set(ctx, userReference, u.ID, u); err != nil {
		return err
	}

	return nil
}

func (ur *userRepository) Update(ctx context.Context, u *user.User) error {
	userReference := getUserReference()

	au, err := ur.auth.GetUserByUID(ctx, u.ID)
	if err != nil {
		return err
	}

	if au.UserInfo == nil {
		return xerrors.New("UserInfo is not exists in Firebase Authentication")
	}

	// Emailに変更があればFirebase Authenticationも更新
	if u.Email != au.UserInfo.Email {
		if err = ur.auth.UpdateEmail(ctx, u.ID, u.Email); err != nil {
			return err
		}
	}

	if err = ur.firestore.Set(ctx, userReference, u.ID, u); err != nil {
		return err
	}

	return nil
}

func (ur *userRepository) GetUIDByEmail(ctx context.Context, email string) (string, error) {
	uid, err := ur.auth.GetUIDByEmail(ctx, email)
	if err != nil {
		return "", err
	}

	return uid, nil
}

func (ur *userRepository) GetUserByUsername(ctx context.Context, username string) (*user.User, error) {
	userReference := getUserReference()

	query := &firestore.Query{
		Field:    "username",
		Operator: "==",
		Value:    username,
	}

	u := &user.User{}

	iter := ur.firestore.GetByQuery(ctx, userReference, query)
	for {
		doc, err := iter.Next()
		if err == iterator.Done {
			break
		}

		if err != nil {
			return nil, err
		}

		err = doc.DataTo(u)
		if err != nil {
			return nil, err
		}
	}

	return u, nil
}

func getToken(ctx context.Context) (string, error) {
	gc, err := middleware.GinContextFromContext(ctx)
	if err != nil {
		return "", xerrors.New("Cannot convert to gin.Context")
	}

	a := gc.GetHeader("Authorization")
	if a == "" {
		return "", xerrors.New("Authorization Header is not contain.")
	}

	t := strings.Replace(a, "Bearer ", "", 1)
	return t, nil
}

// OAuth認証による初回User登録時、UIDの先頭8文字を取得
func getName(uid string) string {
	return uid[0:8]
}
