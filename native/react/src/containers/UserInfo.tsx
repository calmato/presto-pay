import React from "react";
import { useDispatch } from "react-redux";

import { UserInfo } from "~/components/pages";
import { signOutSync } from "~/usecases/auth";

export default function ConnectedUserInfo() {
  const dispatch = useDispatch();

  const actions = React.useMemo(
    () => ({
      signOut(): Promise<void> {
        return dispatch(signOutSync());
      },
    }),
    [dispatch]
  );

  return <UserInfo actions={actions} />;
}
