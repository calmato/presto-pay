import React from "react";

export function createInitialState(): number {
  return 0;
}

type State = ReturnType<typeof createInitialState>;

interface Action {
  type: "begin" | "end";
}

interface Context {
  networkState: number;
  dispatchNetworkActions: React.Dispatch<Action>;
}

export function reducer(state: State, action: Action): number {
  switch (action.type) {
    case "begin":
      return state + 1;
    case "end":
      return state - 1;
    default:
      return state;
  }
}

export const Context = React.createContext<Context>({
  networkState: createInitialState(),
  dispatchNetworkActions: () => {},
});
