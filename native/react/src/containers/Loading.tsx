import React from "react";
import { useDispatch } from "react-redux";

import { Loading } from "~/components/pages";
import { authStateChangedAsync } from "~/usecases/auth";

export default function ConnectedLoading() {
  const dispatch = useDispatch();

  const actions = React.useMemo(
    () => ({
      authStateChanged(): Promise<void> {
        return dispatch(authStateChangedAsync());
      },
    }),
    [dispatch]
  );

  return <Loading actions={actions} />;
}
