package repository

import (
	"context"
	"fmt"
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
	uc := getUserCollection()

	t, err := getToken(ctx)
	if err != nil {
		return nil, err
	}

	uid, err := ur.auth.VerifyIDToken(ctx, t)
	if err != nil {
		return nil, err
	}

	u := &user.User{}

	doc, err := ur.firestore.Get(ctx, uc, uid)
	if err == nil {
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

		if err = ur.firestore.Set(ctx, uc, u.ID, u); err != nil {
			return nil, err
		}
	}

	return u, nil
}

func (ur *userRepository) IndexByUsername(ctx context.Context, username string) ([]*user.User, error) {
	uc := getUserCollection()

	usernameLower := strings.ToLower(username)

	docs, err := ur.firestore.Search(ctx, uc, "username_lower", "asc", usernameLower, 50)
	if err != nil {
		return nil, err
	}

	us := make([]*user.User, len(docs))

	for i, doc := range docs {
		u := &user.User{}

		if err = doc.DataTo(u); err != nil {
			return nil, err
		}

		us[i] = u
	}

	return us, nil
}

func (ur *userRepository) IndexByUsernameFromStartAt(
	ctx context.Context, username string, startAt string,
) ([]*user.User, error) {
	uc := getUserCollection()

	usernameLower := strings.ToLower(username)

	docs, err := ur.firestore.SearchFromStartAt(ctx, uc, "username_lower", "asc", usernameLower, startAt, 50)
	if err != nil {
		return nil, err
	}

	us := make([]*user.User, len(docs))

	for i, doc := range docs {
		u := &user.User{}

		if err = doc.DataTo(u); err != nil {
			return nil, err
		}

		us[i] = u
	}

	return us, nil
}

func (ur *userRepository) ShowByUsername(ctx context.Context, username string) (*user.User, error) {
	uc := getUserCollection()

	query := &firestore.Query{
		Field:    "username",
		Operator: "==",
		Value:    username,
	}

	u := &user.User{}

	iter := ur.firestore.GetByQuery(ctx, uc, query)
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

func (ur *userRepository) ShowByUserID(ctx context.Context, userID string) (*user.User, error) {
	uc := getUserCollection()

	query := &firestore.Query{
		Field:    "id",
		Operator: "==",
		Value:    userID,
	}

	u := &user.User{}

	iter := ur.firestore.GetByQuery(ctx, uc, query)
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

func (ur *userRepository) Create(ctx context.Context, u *user.User) error {
	uc := getUserCollection()

	_, err := ur.auth.CreateUser(ctx, u.ID, u.Email, u.Password)
	if err != nil {
		return err
	}

	if err = ur.firestore.Set(ctx, uc, u.ID, u); err != nil {
		return err
	}

	return nil
}

func (ur *userRepository) Update(ctx context.Context, u *user.User) error {
	uc := getUserCollection()

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

	if err = ur.firestore.Set(ctx, uc, u.ID, u); err != nil {
		return err
	}

	return nil
}

func (ur *userRepository) UpdatePassword(ctx context.Context, uid string, password string) error {
	if err := ur.auth.UpdatePassword(ctx, uid, password); err != nil {
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

// OAuth認証による初回User登録時、UIDの先頭16文字を取得
// e.g.) 12345678-qwer-asdf-zxcv-uiophjklvbnm -> user12345678qwerasdf
func getName(uid string) string {
	str := strings.Replace(uid, "-", "", -1)
	return fmt.Sprintf("user%s", str[0:16])
}
