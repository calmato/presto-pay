import { useNavigation } from "@react-navigation/native";
import React from "react";
import { StyleSheet, View, Text, TouchableOpacity } from "react-native";

import { FRIEND_SEARCH, GROUP, GROUP_NEW } from "~/constants/path";

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

export default function GroupList() {
  const navigation = useNavigation();

  return (
    <View style={styles.container}>
      <Text style={styles.title}>Groups</Text>
      <TouchableOpacity onPress={() => navigation.navigate(GROUP)}>
        <Text>Group</Text>
      </TouchableOpacity>
      <TouchableOpacity onPress={() => navigation.navigate(GROUP_NEW)}>
        <Text>GroupNew</Text>
      </TouchableOpacity>
      <TouchableOpacity onPress={() => navigation.navigate(FRIEND_SEARCH)}>
        <Text>FriendSearch</Text>
      </TouchableOpacity>
    </View>
  );
}
