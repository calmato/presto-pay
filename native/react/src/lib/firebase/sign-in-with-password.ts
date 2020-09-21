import firebase from "./config";

// TODO: アカウント情報の型, レスポンスのエラー処理が決まり次第リファクタ
export default async function signInWithPassword(
  email: string,
  password: string
): Promise<firebase.auth.UserCredential | void> {
  return await firebase
    .auth()
    .signInWithEmailAndPassword(email, password)
    .then((res: firebase.auth.UserCredential) => {
      return res; // TODO: refactor
    })
    .catch((err: any) => {
      console.log("error:", "signInWithPassword", err); // TODO: refactor
    });
}
