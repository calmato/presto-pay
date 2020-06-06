package middleware

import (
	"bytes"
	"encoding/json"
	"io"
	"io/ioutil"

	"github.com/gin-gonic/gin"
	log "github.com/sirupsen/logrus"
)

const (
	logFilter = "******"
)

// Logging - ログの出力
func Logging() gin.HandlerFunc {
	return func(c *gin.Context) {
		buf, _ := ioutil.ReadAll(c.Request.Body)
		rdr1 := ioutil.NopCloser(bytes.NewBuffer(buf)) // ログ出力で使用する用
		rdr2 := ioutil.NopCloser(bytes.NewBuffer(buf)) // ログ出力以降で使用する用

		requestLogging(c, rdr1)

		c.Request.Body = rdr2
		c.Next()
	}
}

func requestLogging(c *gin.Context, reader io.Reader) {
	// json ファイルの読み込み -> 値がなければ return
	data, _ := ioutil.ReadAll(reader)
	if len(data) == 0 {
		return
	}

	// ログ出力用
	fields := log.Fields{}

	// json を データ型に変換する
	params := make(map[string]interface{})
	if err := json.Unmarshal(data, &params); err != nil {
		fields["request"] = "ng"
		log.WithFields(fields).Info("JSONの整形に失敗しました")
		return
	}

	// password のみフィルターにかける
	if params["password"] != nil {
		params["password"] = logFilter
	}

	if params["passwordConfirmation"] != nil {
		params["passwordConfirmation"] = logFilter
	}

	// thumbnail は長いので省略
	if params["thumbnail"] != nil {
		params["thumbnail"] = logFilter
	}

	// ログの整形・出力
	// message := ""
	// for key, value := range params {
	// 	message += fmt.Sprintf("%s:'%s' ", key, value)
	// }

	fields["request"] = "ok"
	// fields["params"] = fmt.Frintf("{ %s}", message)
	fields["params"] = params
	log.WithFields(fields).Info()

	// FLuent Bitへログ転送
	fields["path"] = c.Request.URL.Path
	SendFluent("request", fields)
}
