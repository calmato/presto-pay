package api

import (
	"context"
	"encoding/json"
	"io/ioutil"
	"net/http"

	"github.com/calmato/presto-pay/api/calc/internal/domain/user"
	"github.com/calmato/presto-pay/api/calc/middleware"
	"golang.org/x/xerrors"
)

// APIClient - 他のAPI操作用インターフェース
type APIClient interface {
	Authentication(ctx context.Context) (*user.User, error)
	ShowUser(ctx context.Context, userID string) (*user.User, error)
	UserExists(ctx context.Context, userID string) (bool, error)
	AddGroup(ctx context.Context, userID string, groupID string) error
	RemoveGroup(ctx context.Context, userID string, groupID string) error
	AddHiddenGroup(ctx context.Context, groupID string) (*user.User, int, error)
	RemoveHiddenGroup(ctx context.Context, groupID string) (*user.User, int, error)
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

	status, err := getStatus(res)
	if err != nil {
		return nil, err
	}

	// TODO: refactor
	if status < 200 || status > 299 {
		return nil, xerrors.New("Unknown error")
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
func (c *Client) AddGroup(ctx context.Context, userID string, groupID string) error {
	url := c.userAPIURL + "/internal/users/" + userID + "/groups/" + groupID
	req, _ := http.NewRequest("POST", url, nil)

	if err := setHeader(ctx, req); err != nil {
		return err
	}

	res, err := getResponse(req)
	if err != nil {
		return err
	}

	if _, err := getStatus(res); err != nil {
		return err
	}

	return nil
}

// RemoveGroup - ユーザーをグループから削除
func (c *Client) RemoveGroup(ctx context.Context, userID string, groupID string) error {
	url := c.userAPIURL + "/internal/users/" + userID + "/groups/" + groupID
	req, _ := http.NewRequest("DELETE", url, nil)

	if err := setHeader(ctx, req); err != nil {
		return err
	}

	res, err := getResponse(req)
	if err != nil {
		return err
	}

	if _, err := getStatus(res); err != nil {
		return err
	}

	return nil
}

// AddHiddenGroup - 非公開のグループ一覧に追加
func (c *Client) AddHiddenGroup(ctx context.Context, groupID string) (*user.User, int, error) {
	url := c.userAPIURL + "/internal/groups/" + groupID
	req, _ := http.NewRequest("POST", url, nil)

	if err := setHeader(ctx, req); err != nil {
		return nil, 0, err
	}

	res, err := getResponse(req)
	if err != nil {
		return nil, 0, err
	}

	if status, err := getStatus(res); err != nil {
		return nil, status, err
	}

	defer res.Body.Close()
	body, err := ioutil.ReadAll(res.Body)
	if err != nil {
		return nil, 0, err
	}

	u := &user.User{}
	if err = json.Unmarshal(body, u); err != nil {
		return nil, 0, err
	}

	return u, 0, nil
}

// RemoveHiddenGroup - 非公開のグループ一覧から削除
func (c *Client) RemoveHiddenGroup(ctx context.Context, groupID string) (*user.User, int, error) {
	url := c.userAPIURL + "/internal/groups/" + groupID
	req, _ := http.NewRequest("DELETE", url, nil)

	if err := setHeader(ctx, req); err != nil {
		return nil, 0, err
	}

	res, err := getResponse(req)
	if err != nil {
		return nil, 0, err
	}

	if status, err := getStatus(res); err != nil {
		return nil, status, err
	}

	defer res.Body.Close()
	body, err := ioutil.ReadAll(res.Body)
	if err != nil {
		return nil, 0, err
	}

	u := &user.User{}
	if err = json.Unmarshal(body, u); err != nil {
		return nil, 0, err
	}

	return u, 0, nil
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
	if status < 200 || status > 299 {
		return status, xerrors.New("Failed to request to user api")
	}

	return status, nil
}
