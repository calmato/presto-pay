import React from "react";
import { StyleSheet, View, ActivityIndicator } from "react-native";

import { Context as AuthContext } from "~/contexts/auth";
import { Context as UiContext, Status } from "~/contexts/ui";
import { firebase } from "~/lib/firebase";
import * as LocalStorage from "~/lib/local-storage";

const styles = StyleSheet.create({
  container: {
    flex: 1,
    justifyContent: "center",
    alignItems: "center",
  },
  title: {
    fontSize: 36,
    paddingBottom: 30,
  },
});

function useAuthInformation() {
  const { setAuthState } = React.useContext(AuthContext);
  const { setApplicationState, setError } = React.useContext(UiContext);

  async function navigateNextScreen() {
    const isOpened = await LocalStorage.InitialLaunch.isInitialLaunch();
    if (!isOpened) {
      setApplicationState(Status.FIRST_OPEN);
      return;
    }

    setApplicationState(Status.UN_AUTHORIZED);
  }

  function initialiseFirebaseAuthentication(): void {
    firebase.auth().onAuthStateChanged((user: firebase.User | null) => {
      if (!user) {
        setApplicationState(Status.UN_AUTHORIZED);
        return;
      }

      setApplicationState(Status.AUTHORIZED);
    });
  }

  async function retrieveAuthInformation() {
    try {
      const authInformation = await LocalStorage.AuthInformation.retrieve();

      if (!authInformation) {
        await navigateNextScreen();
        return;
      }

      setAuthState(authInformation);
      await initialiseFirebaseAuthentication();
    } catch (err) {
      setError(err);
    }
  }

  return retrieveAuthInformation;
}

export default function Loading() {
  const retrieveAuthInformation = useAuthInformation();

  React.useEffect(() => {
    retrieveAuthInformation();
  }, [retrieveAuthInformation]);

  return (
    <View style={styles.container}>
      <ActivityIndicator size="large" />
    </View>
  );
}
