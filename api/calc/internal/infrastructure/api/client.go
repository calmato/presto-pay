package api

import (
	"bytes"
	"context"
	"encoding/json"
	"io/ioutil"
	"log"
	"net/http"

	"github.com/calmato/presto-pay/api/calc/internal/domain"
	"github.com/calmato/presto-pay/api/calc/internal/domain/user"
	"github.com/calmato/presto-pay/api/calc/middleware"
	"golang.org/x/xerrors"
)

// APIClient - 他のAPI操作用インターフェース
type APIClient interface {
	Authentication(ctx context.Context) (*user.User, error)
	ShowUser(ctx context.Context, userID string) (*user.User, error)
	UserExists(ctx context.Context, userID string) (bool, error)
	CreateUnauthorizedUser(ctx context.Context, name string, thumbnail string) (*user.User, error)
	AddGroup(ctx context.Context, userID string, groupID string) (*user.User, error)
	RemoveGroup(ctx context.Context, userID string, groupID string) (*user.User, error)
	AddHiddenGroup(ctx context.Context, groupID string) (*user.User, error)
	RemoveHiddenGroup(ctx context.Context, groupID string) (*user.User, error)
}

// Client - 他のAPI管理用の構造体
type Client struct {
	userAPIURL string
}

// NewAPIClient - API Clientの初期化
func NewAPIClient(userAPIURL string) APIClient {
	return &Client{
		userAPIURL: userAPIURL,
	}
}

/*
 * ###########################
 * Request
 * ###########################
 */
type createUnauthorizedUserRequest struct {
	Name      string `json:"name"`
	Thumbnail string `json:"thumbnail"`
}

/*
 * ###########################
 *  User API
 * ###########################
 */

// Authentication - ログインユーザー情報の取得
func (c *Client) Authentication(ctx context.Context) (*user.User, error) {
	url := c.userAPIURL + "/v1/auth"
	req, _ := http.NewRequest("GET", url, nil)

	if err := setHeader(ctx, req); err != nil {
		return nil, err
	}

	res, err := getResponse(req)
	if err != nil {
		return nil, err
	}

	if _, err := getStatus(res); err != nil {
		return nil, err
	}

	defer res.Body.Close()
	body, err := ioutil.ReadAll(res.Body)
	if err != nil {
		return nil, err
	}

	u := &user.User{}
	if err = json.Unmarshal(body, u); err != nil {
		return nil, err
	}

	return u, nil
}

// ShowUser - ユーザー情報取得
func (c *Client) ShowUser(ctx context.Context, userID string) (*user.User, error) {
	url := c.userAPIURL + "/internal/users/" + userID
	req, _ := http.NewRequest("GET", url, nil)

	if err := setHeader(ctx, req); err != nil {
		return nil, err
	}

	res, err := getResponse(req)
	if err != nil {
		return nil, err
	}

	if _, err := getStatus(res); err != nil {
		return nil, err
	}

	defer res.Body.Close()
	body, err := ioutil.ReadAll(res.Body)
	if err != nil {
		return nil, err
	}

	u := &user.User{}
	if err = json.Unmarshal(body, u); err != nil {
		return nil, err
	}

	return u, nil
}

// CreateUnauthorizedUser - 認証機能なしのユーザ作成
func (c *Client) CreateUnauthorizedUser(ctx context.Context, name string, thumbnail string) (*user.User, error) {
	params := &createUnauthorizedUserRequest{
		Name:      name,
		Thumbnail: thumbnail,
	}

	paramsByte, _ := json.Marshal(params)
	paramsReader := bytes.NewReader(paramsByte)

	url := c.userAPIURL + "/internal/unauthorized-users"
	req, _ := http.NewRequest("POST", url, paramsReader)

	if err := setHeader(ctx, req); err != nil {
		return nil, err
	}

	res, err := getResponse(req)
	if err != nil {
		return nil, err
	}

	defer res.Body.Close()
	body, err := ioutil.ReadAll(res.Body)
	if err != nil {
		return nil, err
	}

	u := &user.User{}
	if err = json.Unmarshal(body, u); err != nil {
		return nil, err
	}

	return u, nil
}

// UserExists - ユーザーの存在性検証
func (c *Client) UserExists(ctx context.Context, userID string) (bool, error) {
	url := c.userAPIURL + "/internal/users/" + userID
	req, _ := http.NewRequest("GET", url, nil)

	if err := setHeader(ctx, req); err != nil {
		return false, err
	}

	res, err := getResponse(req)
	if err != nil {
		return false, err
	}

	if _, err := getStatus(res); err != nil {
		return false, err
	}

	return true, nil
}

// AddGroup - ユーザーをグループに追加
func (c *Client) AddGroup(ctx context.Context, userID string, groupID string) (*user.User, error) {
	url := c.userAPIURL + "/internal/users/" + userID + "/groups/" + groupID
	req, _ := http.NewRequest("POST", url, nil)

	if err := setHeader(ctx, req); err != nil {
		return nil, err
	}

	res, err := getResponse(req)
	if err != nil {
		return nil, err
	}

	if _, err := getStatus(res); err != nil {
		return nil, err
	}

	defer res.Body.Close()
	body, err := ioutil.ReadAll(res.Body)
	if err != nil {
		return nil, err
	}

	u := &user.User{}
	if err = json.Unmarshal(body, u); err != nil {
		return nil, err
	}

	return u, nil
}

// RemoveGroup - ユーザーをグループから削除
func (c *Client) RemoveGroup(ctx context.Context, userID string, groupID string) (*user.User, error) {
	url := c.userAPIURL + "/internal/users/" + userID + "/groups/" + groupID
	req, _ := http.NewRequest("DELETE", url, nil)

	if err := setHeader(ctx, req); err != nil {
		return nil, err
	}

	res, err := getResponse(req)
	if err != nil {
		return nil, err
	}

	if status, err := getStatus(res); err != nil {
		log.Println(status) // TODO: lintのエラーなくすため
		return nil, err
	}

	defer res.Body.Close()
	body, err := ioutil.ReadAll(res.Body)
	if err != nil {
		return nil, err
	}

	u := &user.User{}
	if err = json.Unmarshal(body, u); err != nil {
		return nil, err
	}

	return u, nil
}

// AddHiddenGroup - 非公開のグループ一覧に追加
func (c *Client) AddHiddenGroup(ctx context.Context, groupID string) (*user.User, error) {
	url := c.userAPIURL + "/internal/groups/" + groupID
	req, _ := http.NewRequest("POST", url, nil)

	if err := setHeader(ctx, req); err != nil {
		return nil, err
	}

	res, err := getResponse(req)
	if err != nil {
		return nil, err
	}

	if _, err := getStatus(res); err != nil {
		return nil, err
	}

	defer res.Body.Close()
	body, err := ioutil.ReadAll(res.Body)
	if err != nil {
		return nil, err
	}

	u := &user.User{}
	if err = json.Unmarshal(body, u); err != nil {
		return nil, err
	}

	return u, nil
}

// RemoveHiddenGroup - 非公開のグループ一覧から削除
func (c *Client) RemoveHiddenGroup(ctx context.Context, groupID string) (*user.User, error) {
	url := c.userAPIURL + "/internal/groups/" + groupID
	req, _ := http.NewRequest("DELETE", url, nil)

	if err := setHeader(ctx, req); err != nil {
		return nil, err
	}

	res, err := getResponse(req)
	if err != nil {
		return nil, err
	}

	if _, err := getStatus(res); err != nil {
		return nil, err
	}

	defer res.Body.Close()
	body, err := ioutil.ReadAll(res.Body)
	if err != nil {
		return nil, err
	}

	u := &user.User{}
	if err = json.Unmarshal(body, u); err != nil {
		return nil, err
	}

	return u, nil
}

/*
 * ###########################
 *  Private
 * ###########################
 */

func getResponse(req *http.Request) (*http.Response, error) {
	client := &http.Client{}

	res, err := client.Do(req)
	if err != nil {
		return nil, err
	}

	return res, nil
}

func setHeader(ctx context.Context, req *http.Request) error {
	gc, err := middleware.GinContextFromContext(ctx)
	if err != nil {
		return xerrors.New("Cannot convert to gin.Context")
	}

	authorization := gc.GetHeader("Authorization")
	if authorization == "" {
		return xerrors.New("Authorization Header is not contain.")
	}

	req.Header.Set("Content-Type", "application/json")
	req.Header.Set("Accept", "application/json")
	req.Header.Set("Authorization", authorization)

	return nil
}

func getStatus(res *http.Response) (int, error) {
	status := res.StatusCode
	err := xerrors.New("Failed to request to user api")

	switch {
	// 200 - 399
	case status >= 200 && status < 400:
		return status, nil
	// 400 - 499
	case status == 400:
		return status, domain.InvalidRequestValidation.New(err)
	case status == 401:
		return status, domain.Unauthorized.New(err)
	case status == 403:
		return status, domain.Forbidden.New(err)
	case status == 404:
		return status, domain.NotEqualRequestWithDatastore.New(err)
	case status == 409:
		return status, domain.AlreadyExistsInDatastore.New(err)
	// 500 - 599
	case status >= 500 && status < 600:
		return status, domain.ErrorInOtherAPI.New(err)
	// Other
	default:
		return status, domain.ErrorInOtherAPI.New(err)
	}
}
