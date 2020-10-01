import React from "react";

import { Loading } from "~/components/pages";
import { useReduxDispatch } from "~/modules";
import { authStateChangedAsync } from "~/usecases/auth";

export default function ConnectedLoading() {
  const dispatch = useReduxDispatch();

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
