package firestore

import (
	"context"

	"cloud.google.com/go/firestore"
	firebase "firebase.google.com/go/v4"
)

// Firestore - Firestoreの構造体
type Firestore struct {
	Client *firestore.Client
}

// Query - Where()メソッドのフィルタリング使用するクエリ構造体
type Query struct {
	Field    string
	Operator string
	Value    interface{}
}

// NewClient - Firestoreに接続
func NewClient(ctx context.Context, app *firebase.App) (*Firestore, error) {
	client, err := app.Firestore(ctx)
	if err != nil {
		return nil, err
	}

	return &Firestore{client}, nil
}

// Close - Firestoreとの接続を終了
func (f *Firestore) Close() error {
	return f.Client.Close()
}

// Get - 単一のドキュメントの内容を取得
func (f *Firestore) Get(ctx context.Context, collection string, document string) (*firestore.DocumentSnapshot, error) {
	doc, err := f.Client.Collection(collection).Doc(document).Get(ctx)
	if err != nil {
		return nil, err
	}

	return doc, nil
}

// GetAll - コレクション内のすべてのドキュメントを取得
func (f *Firestore) GetAll(ctx context.Context, collection string) *firestore.DocumentIterator {
	return f.Client.Collection(collection).Documents(ctx)
}

// GetByQuery - where()を使用して、特定の条件を満たすすべてのドキュメントを取得
func (f *Firestore) GetByQuery(ctx context.Context, collection string, query *Query) *firestore.DocumentIterator {
	return f.Client.Collection(collection).Where(query.Field, query.Operator, query.Value).Documents(ctx)
}

// GetByQueries - 複数のwhere()メソッドをつなぎ合わせて、特定の条件を満たすすべてのドキュメントを取得
func (f *Firestore) GetByQueries(ctx context.Context, collection string, queries []*Query) *firestore.DocumentIterator {
	c := f.Client.Collection(collection).Query

	for _, q := range queries {
		c = c.Where(q.Field, q.Operator, q.Value)
	}

	return c.Documents(ctx)
}

// Set - 単一のドキュメントを作成または上書き
func (f *Firestore) Set(ctx context.Context, collection string, document string, data interface{}) error {
	if _, err := f.Client.Collection(collection).Doc(document).Set(ctx, data); err != nil {
		return err
	}

	return nil
}

// DeleteDoc - ドキュメントを削除
func (f *Firestore) DeleteDoc(ctx context.Context, collection string, document string) error {
	if _, err := f.Client.Collection(collection).Doc(document).Delete(ctx); err != nil {
		return err
	}

	return nil
}
