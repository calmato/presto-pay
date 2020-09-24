import { AuthUser } from "./authentication";
import firebase from "./config";

export default async function signInWithPassword(email: string, password: string): Promise<AuthUser> {
  return await firebase
    .auth()
    .signInWithEmailAndPassword(email, password)
    .then(async (res: firebase.auth.UserCredential) => {
      if (!res.user) {
        throw new Error("user is null");
      }

      const authInformation: AuthUser = {
        id: res.user.uid,
        email: res.user.email || "",
        emailVerified: res.user.emailVerified,
        token: "",
        creationTime: res.user.metadata?.creationTime,
        lastSignInTime: res.user.metadata?.lastSignInTime,
      };

      await firebase
        .auth()
        .currentUser?.getIdToken()
        .then((token: string) => {
          authInformation.token = token;
        })
        .catch((err: Error) => {
          throw err;
        });

      return authInformation;
    })
    .catch((err: Error) => {
      throw err;
    });
}
