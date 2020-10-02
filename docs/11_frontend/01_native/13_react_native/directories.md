# React Native - ディレクトリ構造

[root](./../../../../README.md)
/ [11_frontend](./../../README.md)
/ [01_native](./../README.md)
/ [13_react_native](./README.md)
/ [ディレクトリ構造](./directories.md)

## ディレクトリ構造

<pre>
.
├── assets
└── src
    ├── components
    │   ├── atoms
    │   ├── molecules
    │   ├── organisms
    │   └── pages
    ├── constants
    ├── containers
    ├── contexts
    ├── domain
    │   └── models
    ├── lib
    │   ├── axios
    │   ├── firebase
    │   ├── hooks
    │   └── local-storage
    ├── modules
    ├── routes
    │   ├── Header
    │   └── Main
    ├── selectors
    ├── types
    │   ├── request
    │   ├── response
    │   └── selectors
    └── usecases
</pre>

|    ディレクトリ名    | 関連ライブラリ |                                    説明                                    |
| :------------------- | :------------- | :------------------------------------------------------------------------- |
| components           |                | UI設計用                                                                   |
| components/atoms     |                | 最も小さい粒度の要素                                                       |
| components/molecules |                | 2つ以上のAtomsを組み合わせたシンプルなUI要素                               |
| components/organisms |                | 切り離して単体でも機能する分子の集まり                                     |
| components/pages     |                | 実際のコンテンツを適用したもの                                             |
| containers           | react-redux    | ComponentsとStore を繋ぐ役割                                               |
| domain/models        | redux          | モデルの定義, Vuex Mutationsのイメージ                                     |
| modules              | redux          | Actionの定義, アプリケーションのデータフローを表現, Vuex Actionsのイメージ |
| selectors            | reselect       | Componentsで表示する用のデータを生成, Vuex Gettersのイメージ               |
| usecases             | redux-thunk    | ユースケースを表現, Actionsの詳細を記述する感じ                            |
| types                |                | アプリ内で使用する関数,変数の型を定義                                      |
| types/request        |                | APIへのリクエストの型定義                                                  |
| types/response       |                | APIからのレスポンスの型定義                                                |
| types/selectors      |                | Selectorsで取得する値の型定義                                              |
