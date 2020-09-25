import React from "react";
import { StyleSheet, View, Text, TouchableOpacity } from "react-native";

import { Context, Status } from "~/contexts/ui";
import * as LocalStorage from "~/lib/local-storage";

const styles = StyleSheet.create({
  container: {
    flex: 1,
    justifyContent: "center",
    alignItems: "center",
  },
  title: {
    textAlign: "center",
    fontSize: 36,
    fontWeight: "bold",
  },
});

export default function Initial() {
  const { setApplicationState } = React.useContext(Context);

  const onEnd = React.useCallback(() => {
    LocalStorage.InitialLaunch.markAsTutorialIsDone().finally(() => {
      setApplicationState(Status.UN_AUTHORIZED);
    });
  }, [setApplicationState]);

  return (
    <View style={styles.container}>
      <Text style={styles.title}>Initial</Text>
      <TouchableOpacity onPress={onEnd}>
        <Text>SignIn</Text>
      </TouchableOpacity>
    </View>
  );
}
