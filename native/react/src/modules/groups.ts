import { Group, Groups } from "~/domain/models";

// Initial
export function createInitialState(): Groups.Model {
  return Groups.factory();
}

export type State = ReturnType<typeof createInitialState>;

// Actions
export const ADD = "presto-pay/groups/ADD";
export const UPDATE = "presto-pay/groups/UPDATE";
export const REMOVE = "presto-pay/groups/REMOVE";

// Action Creators
export function add(group: Group.Values) {
  return {
    type: ADD,
    payload: {
      group,
    },
  };
}

export function update(id: string, group: Group.Values) {
  return {
    type: UPDATE,
    payload: {
      id,
      group,
    },
  };
}

export function remove(id: string) {
  return {
    type: REMOVE,
    payload: {
      id,
    },
  };
}

export type Action =
  | Readonly<ReturnType<typeof add>>
  | Readonly<ReturnType<typeof update>>
  | Readonly<ReturnType<typeof remove>>;

// Reducer
export default function reducer(state: State = createInitialState(), action: Action) {
  const { payload } = action;

  switch (action.type) {
    case ADD:
      return Groups.add(state, payload.group);
    case UPDATE:
      return Groups.update(state, payload.id, payload.group);
    case REMOVE:
      return Groups.remove(state, payload.id);
    default:
      return state;
  }
}
