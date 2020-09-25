import React from "react";
import { useDispatch } from "react-redux";

import { SignIn } from "~/components/pages";
import { Auth } from "~/domain/models";
import { signInWithPasswordSync } from "~/usecases/auth";

export default function ConnectedSignIn() {
  const dispatch = useDispatch();

  const actions = React.useMemo(
    () => ({
      signInWithPassword(email: string, password: string): Promise<Auth.AuthValues> {
        return dispatch(signInWithPasswordSync(email, password));
      },
    }),
    [dispatch]
  );

  return <SignIn actions={actions} />;
}
