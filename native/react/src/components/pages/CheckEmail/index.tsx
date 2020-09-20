import { useNavigation } from "@react-navigation/native";
import React from "react";
import { StyleSheet, View, Text } from "react-native";
import { TouchableOpacity } from "react-native-gesture-handler";

import { CHECK_EMAIL, SIGN_IN } from "~/constants/path";

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

export default function CheckEmail() {
  const navigation = useNavigation();

  return (
    <View style={styles.container}>
      <Text style={styles.title}>CheckEmail</Text>
      <TouchableOpacity onPress={() => navigation.navigate(SIGN_IN)}>
        <Text>SignIn</Text>
      </TouchableOpacity>
    </View>
  );
}
