import React from "react";
import { useSelector, useDispatch } from "react-redux";

import { UserInfo } from "~/components/pages";
import { Auth } from "~/domain/models";
import { authSelector } from "~/selectors/auth";
import { signOutAsync, showAuthUserAsync } from "~/usecases/auth";

export default function ConnectedUserInfo() {
  const auth: Auth.Model = useSelector(authSelector);

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

  return <UserInfo auth={auth} actions={actions} />;
}
