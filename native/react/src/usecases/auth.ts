import { Dispatch } from "redux";

import { Auth } from "~/domain/models";
import { firebase, signInWithPasswordToFirebase, signOutFromFirebase, AuthUser } from "~/lib/firebase";
import * as LocalStorage from "~/lib/local-storage";
import { setAuth, reset } from "~/modules/auth";

export function signInWithPasswordAsync(email: string, password: string) {
  return (dispatch: Dispatch): Promise<Auth.AuthValues> => {
    return new Promise((resolve: (value: Auth.AuthValues) => void, reject: (reason: Error) => void) => {
      signInWithPasswordToFirebase(email, password)
        .then(async (res: AuthUser) => {
          const payload: Auth.AuthValues = {
            id: res.id,
            email: res.email,
            emailVerified: res.emailVerified,
            token: res.token,
          };

          dispatch(setAuth(payload));
          await LocalStorage.AuthInformation.save(payload);

          resolve(payload);
        })
        .catch((err: Error) => {
          reject(err);
        });
    });
  };
}

export function authStateChangedAsync() {
  return (): Promise<void> => {
    return new Promise((resolve: () => void, reject: (reason: Error) => void) => {
      firebase.auth().onAuthStateChanged((user: firebase.User | null) => {
        if (user) {
          resolve();
        }

        reject(new Error("User is not exists"));
      });
    });
  };
}

export function signOutAsync() {
  return (dispatch: Dispatch): Promise<void> => {
    return new Promise((resolve: () => void, reject: (reason: Error) => void) => {
      signOutFromFirebase()
        .catch((err: Error) => {
          reject(err);
        })
        .finally(async () => {
          dispatch(reset());
          await LocalStorage.AuthInformation.clear();

          resolve();
        });
    });
  };
}
