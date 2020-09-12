import React from "react";
import { StyleSheet, View, Text, TouchableOpacity } from "react-native";

import { Context, Status } from "~/contexts/ui";

const styles = StyleSheet.create({
  container: {
    flex: 1,
    justifyContent: "center",
    alignItems: "center",
  },
});

export default function SignUp() {
  const { setApplicationState } = React.useContext(Context);

  return (
    <View style={styles.container}>
      <Text>SignUp</Text>
      <TouchableOpacity onPress={() => setApplicationState(Status.AUTHORIZED)}>
        <Text>SignUp</Text>
      </TouchableOpacity>
    </View>
  );
}
