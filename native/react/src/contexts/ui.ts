import React from "react";

export enum Status {
  LOADING = "loading", // アプリ起動時のロード処理中
  FIRST_OPEN = "firstOpen", // 初回起動時
  UN_AUTHORIZED = "unAuthorized", // 初回起動後で認証が済んでいない状態
  AUTHORIZED = "authorized", // 初回起動後で認証が済んでいる状態
}

export function createApplicationInitialState(): Status {
  return Status.LOADING;
}

type ErrorState = Error | null;

export function createErrorInitialState(): ErrorState {
  return null;
}

export function createSnackbarInitialState() {
  return {
    visible: false,
    message: "",
    label: "Done",
  };
}

type SnackbarState = ReturnType<typeof createSnackbarInitialState>;

export const Context = React.createContext({
  applicationState: createApplicationInitialState(),
  setApplicationState: (_: Status) => {},
  error: createErrorInitialState(),
  setError: (_: ErrorState) => {},
  snackbar: createSnackbarInitialState(),
  setSnackbar: (_: SnackbarState) => {},
});
