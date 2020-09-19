import React from "react";
import { useNavigation } from "@react-navigation/native";
import { StyleSheet, View, Text, TouchableOpacity } from "react-native";

import { GROUP, GROUP_LIST, FRIEND_SEARCH } from "~/constants/path";

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
  }
});

export default function GroupNew() {
  const navigation = useNavigation();

  return (
    <View style={styles.container}>
      <Text style={styles.title}>GroupNew</Text>
      <TouchableOpacity onPress={() => navigation.goBack()}>
        <Text>GroupList</Text>
      </TouchableOpacity>
      <TouchableOpacity onPress={() => navigation.navigate(GROUP)}>
        <Text>Group</Text>
      </TouchableOpacity>
      <TouchableOpacity onPress={() => navigation.navigate(FRIEND_SEARCH)}>
        <Text>FriendSearch</Text>
      </TouchableOpacity>
    </View>
  );
}
