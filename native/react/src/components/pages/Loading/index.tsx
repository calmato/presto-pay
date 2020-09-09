import React from "react";
import { StyleSheet, View, Text, TouchableOpacity } from "react-native";

import * as UiContext from "~/contexts/ui";

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

function ChangeStateButton(props: { state: UiContext.Status }): JSX.Element {
  const { setApplicationState } = React.useContext(UiContext.Context);
  const { state } = props;

  return (
    <TouchableOpacity onPress={() => setApplicationState(state)}>
      <Text>
        Change state to
        {state}
      </Text>
    </TouchableOpacity>
  );
}

export default function Loading() {
  return (
    <View style={styles.container}>
      <Text style={styles.title}>Loading</Text>

      <ChangeStateButton state={UiContext.Status.AUTHORIZED} />
      <ChangeStateButton state={UiContext.Status.UN_AUTHORIZED} />
      <ChangeStateButton state={UiContext.Status.FIRST_OPEN} />
    </View>
  );
}
