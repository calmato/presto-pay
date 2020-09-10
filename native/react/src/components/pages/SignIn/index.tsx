import { useNavigation } from "@react-navigation/native";
import React from "react";
import { StyleSheet, View, Text, TouchableOpacity } from "react-native";

import { SIGN_UP } from "~/constants/path";
import { Context, Status } from "~/contexts/ui";

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

export default function SignIn() {
  const { navigate } = useNavigation();
  const { setApplicationState } = React.useContext(Context);

  return (
    <View style={styles.container}>
      <Text style={styles.title}>SignIn</Text>
      <TouchableOpacity onPress={() => setApplicationState(Status.AUTHORIZED)}>
        <Text>SignIn</Text>
      </TouchableOpacity>
      <TouchableOpacity onPress={() => navigate(SIGN_UP)}>
        <Text>SignUp</Text>
      </TouchableOpacity>
    </View>
  );
}
