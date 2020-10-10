import { assertIsDefined } from "~/lib/assert";
import { formatDateTime } from "~/lib/datetime";

// Model関連
export interface Model {
  readonly id: string;
  readonly token: string;
  readonly email: string;
  readonly emailVerified: boolean;
  readonly name: string;
  readonly username: string;
  readonly thumbnailUrl: string;
  readonly groupIds: string[];
  readonly friendIds: string[];
  readonly createdAt: string;
  readonly updatedAt: string;
}

const initialState: Model = {
  id: "",
  token: "",
  email: "",
  emailVerified: false,
  name: "",
  username: "",
  thumbnailUrl: "",
  groupIds: [],
  friendIds: [],
  createdAt: "",
  updatedAt: "",
};

// Input関連
export interface AuthValues {
  id: string;
  email?: string;
  emailVerified?: boolean;
  token: string;
}

export interface UserValues {
  name: string;
  username: string;
  thumbnailUrl: string;
  groupIds: string[];
  friendIds: string[];
  createdAt: string;
  updatedAt: string;
}

// Functions
export function factory(): Model {
  return initialState;
}

export function setAuth(auth: Model, values: AuthValues): Model {
  assertIsDefined(values.token);

  return {
    ...auth,
    ...values,
  };
}

export function setUser(auth: Model, values: UserValues): Model {
  const createdAt: string = formatDateTime(values.createdAt);
  const updatedAt: string = formatDateTime(values.updatedAt);

  return {
    ...auth,
    ...values,
    createdAt,
    updatedAt,
  };
}
