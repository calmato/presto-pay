import React from "react";

export interface Auth {
  id: string;
  name?: string;
  username?: string;
  email?: string;
  thumbnailUrl?: string;
  createdAt?: number;
  updatedAt?: number;
  lastLoginAt?: number;
}

export type AuthInformation = Auth | null;

export function createInitialState(): AuthInformation {
  return null;
}

export const Context = React.createContext({
  authState: createInitialState(),
  setAuthState: (_: AuthInformation) => {},
});
