import React from "react";
import { StyleSheet, View, ActivityIndicator } from "react-native";

import { Context as AuthContext } from "~/contexts/auth";
import { Context as UiContext, Status } from "~/contexts/ui";
import * as LocalStorage from "~/lib/local-storage";
import { firebase } from "~/lib/firebase";

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

interface Props {
  actions: object;
}

function useAuthInformation(): () => Promise<void> {
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

  function initialiseFirebaseAuthentication() {
    return new Promise((resolve, reject) => {
      firebase.auth().onAuthStateChanged((user) => {
        if (!user) {
          setApplicationState(Status.UN_AUTHORIZED);
          return;
        }

        console.log("debug:", "initialiseFirebaseAuthentication", user);
      });
    });
  }

  async function retrieveAuthFormation() {
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

  return retrieveAuthFormation;
}

export default function Loading(props: Props) {
  console.log("props:", "Loading", props);
  const retrieveAuthFormation = useAuthInformation();

  React.useEffect(() => {
    retrieveAuthFormation();
  }, [retrieveAuthFormation]);

  return (
    <View style={styles.container}>
      <ActivityIndicator size="large" />
    </View>
  );
}
