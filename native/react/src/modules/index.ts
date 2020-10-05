import { useDispatch } from "react-redux";
import { combineReducers, Action } from "redux";
import { ThunkDispatch } from "redux-thunk";

import * as Auth from "./auth";
import * as Groups from "./groups";

export function createInitialState() {
  return {
    auth: Auth.createInitialState(),
    groups: Groups.createInitialState(),
  };
}

export type AppState = Readonly<ReturnType<typeof createInitialState>>;

export default combineReducers<AppState>({
  auth: Auth.default,
  groups: Groups.default,
});

// TODO: 型定義
// eslint-disable-next-line @typescript-eslint/no-explicit-any
export type ReduxDispatch = ThunkDispatch<AppState, any, Action>;

export function useReduxDispatch(): ReduxDispatch {
  return useDispatch<ReduxDispatch>();
}
