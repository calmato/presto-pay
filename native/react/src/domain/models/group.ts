import { assertIsDefined } from "~/lib/assert";
import { formatDateTime } from "~/lib/datetime";

// Models
export interface Model {
  readonly id: string;
  readonly name: string;
  readonly thumbnailUrl: string;
  readonly userIds: string[];
  readonly createdAt: string;
  readonly updatedAt: string;
}

const initialState: Model = {
  id: "",
  name: "N/A",
  thumbnailUrl: "https://storage.cloud.google.com/presto-pay-dev.appspot.com/thumbnail_default.png",
  userIds: [],
  createdAt: "",
  updatedAt: "",
};

// Inputs
export interface Values {
  id: string;
  name: string;
  thumbnailUrl: string;
  userIds: string[];
  createdAt: string;
  updatedAt: string;
}

// Functions
export function factory(): Model {
  return initialState;
}

export function set(group: Model, values: Values): Model {
  assertIsDefined(values.id);
  assertIsDefined(values.name);

  const createdAt: string = formatDateTime(values.createdAt);
  const updatedAt: string = formatDateTime(values.updatedAt);

  return {
    ...group,
    ...values,
    createdAt,
    updatedAt,
  };
}
