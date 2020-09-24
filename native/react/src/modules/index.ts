import { combineReducers } from "redux";

import * as Auth from "./auth";

export function createInitialState() {
  return {
    auth: Auth.createInitialState(),
  };
}

export type AppState = Readonly<ReturnType<typeof createInitialState>>;

export default combineReducers<AppState>({
  auth: Auth.default,
});
