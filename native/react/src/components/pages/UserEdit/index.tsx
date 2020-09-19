import { useNavigation } from "@react-navigation/native";
import React from "react";
import { StyleSheet, View, Text, TouchableOpacity } from "react-native";

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

export default function UserEdit() {
  const navigation = useNavigation();

  return (
    <View style={styles.container}>
      <Text style={styles.title}>UserEdit</Text>
      <TouchableOpacity onPress={() => navigation.goBack()}>
        <Text>goBack</Text>
      </TouchableOpacity>
    </View>
  );
}
