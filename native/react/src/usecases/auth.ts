import { Dispatch } from "redux";

import { Auth } from "~/domain/models";
import { axios } from "~/lib/axios";
import { firebase, signInWithPasswordToFirebase, signOutFromFirebase, AuthUser } from "~/lib/firebase";
import * as LocalStorage from "~/lib/local-storage";
import { AppState } from "~/modules";
import { setAuth, setUser, reset } from "~/modules/auth";
import { ShowAuthResponse } from "~/types/response/auth";

export function signInWithPasswordAsync(email: string, password: string) {
  return (dispatch: Dispatch, getState: () => AppState): Promise<void> => {
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

          const auth: Auth.Model = getState().auth;
          await LocalStorage.AuthInformation.save(auth);

          resolve();
        })
        .catch((err: Error) => reject(err));
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

export function showAuthUserAsync() {
  return (dispatch: Dispatch, getState: () => AppState): Promise<void> => {
    return new Promise((resolve: () => void, reject: (reason: Error) => void) => {
      axios
        .get("/v1/auth")
        .then(async (res) => {
          const data: ShowAuthResponse = res.data;
          const payload: Auth.UserValues = {
            name: data.name,
            username: data.username,
            thumbnailUrl: data.thumbnailUrl,
            groupIds: data.groupIds,
            friendIds: data.friendIds,
            createdAt: data.createdAt,
            updatedAt: data.updatedAt,
          };

          dispatch(setUser(payload));

          const auth: Auth.Model = getState().auth;
          await LocalStorage.AuthInformation.save(auth);

          resolve();
        })
        .catch((err: Error) => reject(err));
    });
  };
}

export function signOutAsync() {
  return (dispatch: Dispatch): Promise<void> => {
    return new Promise((resolve: () => void, reject: (reason: Error) => void) => {
      signOutFromFirebase()
        .catch((err: Error) => reject(err))
        .finally(async () => {
          dispatch(reset());
          await LocalStorage.AuthInformation.clear();

          resolve();
        });
    });
  };
}
