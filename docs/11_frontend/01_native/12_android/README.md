# ネイティブアプリ - Android

[root](./../../../../README.md) 
/ [11_frontend](./../../README.md) 
/ [01_native](./../README.md) 
/ [12_android](./README.md)


# 実行環境の設定  
1. android stdioの導入  
> https://developer.android.com/studio?hl=ja  
2. git clone  
> git clone https://github.com/calmato/presto-pay.git  
3. プロジェクトを開く  
naitive配下のandroidを開く  
4. エミュレーターの導入  
今回はAndroid platform versionを9.0に設定しているため、それに合わせてエミュレーターも9.0以上を導入  
5. buildして実行  
login画面が表示されれば成功

# 各種ドキュメント

# Firebase の認証方法に関して



## [ログイン機能の実装 について]
1. ログインのUI作成 
2. FirebaseへAndroidアプリ追加 (<- 完了している認識)
3. Firebase Authにメール認証のリクエスト投げれるように
4. Firebase AuthのレスポンスからJWT(認証用トークン)の取得
5. 取得したトークンの永続化 (Cookie, Session or LocalStorage)
6. OAuth認証の実装
## [ログイン機能の実装 参考URL]
### 3. Firebase Authにメール認証のリクエスト投げれるように
* リクエストは以下のメソッドを利用するとできそう
> https://firebase.google.com/docs/reference/kotlin/com/google/firebase/auth/FirebaseAuth?hl=ja#signinwithemailandpassword
* (実装内容の参考ドキュメント)
> https://firebase.google.com/docs/auth/android/password-auth?hl=ja#sign_in_a_user_with_an_email_address_and_password
### 4. Firebase AuthのレスポンスからJWT(認証用トークン)の取得
* ログインが成功したらFirebaseUser型のインスタンスが取得できる
> https://firebase.google.com/docs/reference/kotlin/com/google/firebase/auth/FirebaseUser?hl=ja
* 取得したインスタンスより認証用トークンの取得
> https://firebase.google.com/docs/reference/kotlin/com/google/firebase/auth/FirebaseUser?hl=ja#getIdToken(kotlin.Boolean)
* (実装内容の参考ドキュメント)
> https://firebase.google.com/docs/auth/admin/verify-id-tokens?hl=ja#retrieve_id_tokens_on_clients
### 5. 取得したトークンの永続化 (Cookie, Session or LocalStorage)
* iOSではUserDefaultsってのでトークンの管理しようと思ってるんやけど、Androidの場合はSharedPreferencesての使えば同じことできるらしい
> https://massu.qrunch.io/entries/Q4UEZJoUIK6550Lv 

# Firebase Test Labの使い方
> https://firebase.google.com/docs/test-lab/android/command-line?hl=ja
