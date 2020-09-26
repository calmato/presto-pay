import React from "react";
import { useDispatch } from "react-redux";

import { UserInfo } from "~/components/pages";
import { signOutAsync } from "~/usecases/auth";

export default function ConnectedUserInfo() {
  const dispatch = useDispatch();

  const actions = React.useMemo(
    () => ({
      signOut(): Promise<void> {
        return dispatch(signOutAsync());
      },
    }),
    [dispatch]
  );

  return <UserInfo actions={actions} />;
}
