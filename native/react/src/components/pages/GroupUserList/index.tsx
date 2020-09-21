import { useNavigation } from "@react-navigation/native";
import React from "react";
import { StyleSheet, View, Text, TouchableOpacity } from "react-native";

import { GROUP_USER_SEARCH } from "~/constants/path";

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

export default function GroupUserList() {
  const navigation = useNavigation();

  return (
    <View style={styles.container}>
      <Text style={styles.title}>GroupEdit</Text>
      <TouchableOpacity onPress={() => navigation.goBack()}>
        <Text>GroupEdit</Text>
      </TouchableOpacity>
      <TouchableOpacity onPress={() => navigation.navigate(GROUP_USER_SEARCH)}>
        <Text>GroupUserSearch</Text>
      </TouchableOpacity>
    </View>
  );
}
