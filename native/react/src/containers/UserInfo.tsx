import React from "react";
import { useDispatch } from "react-redux";

import { UserInfo } from "~/components/pages";
import { signOutAsync, showAuthUserAsync } from "~/usecases/auth";

export default function ConnectedUserInfo() {
  const dispatch = useDispatch();

  const actions = React.useMemo(
    () => ({
      showAuthUser(): Promise<void> {
        return dispatch(showAuthUserAsync());
      },
      signOut(): Promise<void> {
        return dispatch(signOutAsync());
      },
    }),
    [dispatch]
  );

  return <UserInfo actions={actions} />;
}
