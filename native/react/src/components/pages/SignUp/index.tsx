import { useNavigation } from "@react-navigation/native";
import React from "react";
import { StyleSheet, View, Text, TouchableOpacity } from "react-native";

import { SIGN_IN, CHECK_EMAIL } from "~/constants/path";
import { Context, Status } from "~/contexts/ui";

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

export default function SignUp() {
  const { navigate, goBack } = useNavigation();
  const { setApplicationState } = React.useContext(Context);

  return (
    <View style={styles.container}>
      <Text style={styles.title}>SignUp</Text>
      <TouchableOpacity onPress={() => goBack()}>
        <Text>SignIn</Text>
      </TouchableOpacity>
      <TouchableOpacity onPress={() => setApplicationState(Status.AUTHORIZED)}>
        <Text>SignUp</Text>
      </TouchableOpacity>
      <TouchableOpacity onPress={() => navigate(CHECK_EMAIL)}>
        <Text>CheckEmail</Text>
      </TouchableOpacity>
    </View>
  );
}
