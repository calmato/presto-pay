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

// Client - 他のAPI管理用の構造体
type Client struct {
	userAPIURL string
}

// NewAPIClient - API Clientの初期化
func NewAPIClient(userAPIURL string) *Client {
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
	url := c.userAPIURL + "/v1/users"
	req, _ := http.NewRequest("GET", url, nil)

	if err := setHeader(ctx, req); err != nil {
		return nil, err
	}

	res, err := getResponse(req)
	if err != nil {
		return nil, err
	}

	if res.StatusCode < 200 || res.StatusCode > 299 {
		return nil, xerrors.New("Failed to request to user api")
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

	if res.StatusCode < 200 || res.StatusCode > 299 {
		return false, xerrors.New("Failed to request to user api")
	}

	return true, nil
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
