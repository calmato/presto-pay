import { Auth } from "~/domain/models";

// Initial
export function createInitialState(): Auth.Model {
  return Auth.factory();
}

export type State = ReturnType<typeof createInitialState>;

// Actions
export const SET_AUTH = "presto-pay/auth/SET_AUTH";
export const SET_USER = "presto-pay/auth/SET_USER";

// Action Creators
export function setAuth(auth: Auth.AuthValues) {
  return {
    type: SET_AUTH,
    payload: {
      auth,
    },
  };
}

export function setUser(auth: Auth.UserValues) {
  return {
    type: SET_USER,
    payload: {
      auth,
    },
  };
}

export type Action = Readonly<ReturnType<typeof setAuth>> | Readonly<ReturnType<typeof setUser>>;

// Reducer
export default function reducer(state = createInitialState(), action: Action) {
  const { payload } = action;

  switch (action.type) {
    case SET_AUTH:
      return Auth.setAuth(state, payload.auth);
    case SET_USER:
      return Auth.setUser(state, payload.auth);
    default:
      return state;
  }
}
