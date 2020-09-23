import { Dispatch } from "redux";

import { Auth } from "~/domain/models";
import { signInWithPasswordToFirebase, AuthUser } from "~/lib/firebase";
import * as LocalStorage from "~/lib/local-storage";
import { AppState } from "~/modules";
import { setAuth } from "~/modules/auth";

export function signInWithPasswordSync(email: string, password: string) {
  return (dispatch: Dispatch, getState: () => AppState) => {
    return new Promise((resolve: () => void, reject: (reason: Error) => void) => {
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
          console.log(getState()); // TODO: remove

          resolve();
        })
        .catch((err: Error) => {
          reject(err);
        });
    });
  };
}
