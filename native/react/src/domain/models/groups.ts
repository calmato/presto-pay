import { filter } from "@januswel/object-utilities";

import * as Group from "./group";

// Models
export interface Model {
  [id: string]: Group.Model;
}

const initialState: Model = {};

// Inputs

// Functions
export function factory(): Model {
  return initialState;
}

export function add(groups: Model, group: Group.Model): Model {
  return {
    ...groups,
    [group.id]: group,
  };
}

export function update(groups: Model, id: string, values: Group.Values): Model {
  return {
    ...groups,
    [id]: Group.set(groups[id], values),
  };
}

export function remove(groups: Model, target: string): Model {
  return filter(groups, (id: string) => id !== target);
}
