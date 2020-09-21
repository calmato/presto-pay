import React from "react";

export interface Auth {
  id: string;
  name: string | null;
  createdAt: number | null;
  lastLoginAt: number | null;
}

export type AuthInformation = Auth | null;

export function createInitialState(): AuthInformation {
  return null;
}

export const Context = React.createContext({
  authState: createInitialState(),
  setAuthState: (_: AuthInformation) => {},
});
