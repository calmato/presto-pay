import React from "react";
import { useDispatch } from "react-redux";

import { SignIn } from "~/components/pages";
import { Auth } from "~/domain/models";
import { signInWithPasswordAsync, showAuthUserAsync } from "~/usecases/auth";

export default function ConnectedSignIn() {
  const dispatch = useDispatch();

  const actions = React.useMemo(
    () => ({
      signInWithPassword(email: string, password: string): Promise<void> {
        return dispatch(signInWithPasswordAsync(email, password)).then(() => {
          dispatch(showAuthUserAsync());
        });
      },
      showAuthUser(): Promise<void> {
        return dispatch(showAuthUserAsync());
      },
    }),
    [dispatch]
  );

  return <SignIn actions={actions} />;
}
