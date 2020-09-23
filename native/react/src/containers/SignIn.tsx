import React from "react";
import { useDispatch } from "react-redux";

import { SignIn } from "~/components/pages";
import { Auth } from "~/domain/models";
import { setAuth, setUser } from "~/modules/auth";
import { signInWithPasswordSync } from "~/usecases/auth";

export default function ConnectedSignIn() {
  const dispatch = useDispatch();

  const actions = React.useMemo(
    () => ({
      setAuth(values: Auth.AuthValues) {
        dispatch(setAuth(values));
      },
      setUser(values: Auth.UserValues) {
        dispatch(setUser(values));
      },
      signInWithPasswordSync(email: string, password: string) {
        dispatch(signInWithPasswordSync(email, password));
      },
    }),
    [dispatch]
  );

  return <SignIn actions={actions} />;
}
