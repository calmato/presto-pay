import { useNavigation } from "@react-navigation/native";
import React from "react";
import { StyleSheet, View, Text, TouchableOpacity } from "react-native";

import { GROUP_USER_LIST } from "~/constants/path";

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

export default function GroupUserAdd() {
  const navigation = useNavigation();

  return (
    <View style={styles.container}>
      <Text style={styles.title}>GroupUserAdd</Text>
      <TouchableOpacity onPress={() => navigation.goBack()}>
        <Text>GroupUserSearch</Text>
      </TouchableOpacity>
      <TouchableOpacity onPress={() => navigation.navigate(GROUP_USER_LIST)}>
        <Text>GroupUserList</Text>
      </TouchableOpacity>
    </View>
  );
}
