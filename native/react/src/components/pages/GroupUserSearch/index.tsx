import React from "react";
import { useNavigation } from "@react-navigation/native";
import { StyleSheet, View, Text, TouchableOpacity } from "react-native";

import { GROUP_USER_ADD, GROUP_USER_SEARCH, FRIEND_INVITED } from "~/constants/path";

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

export default function GroupUserSearch() {
  const navigation = useNavigation();

  return (
    <View style={styles.container}>
      <Text style={styles.title}>GroupUserSearch</Text>
      <TouchableOpacity onPress={() => navigation.goBack()}>
        <Text>GroupList</Text>
      </TouchableOpacity>
      <TouchableOpacity onPress={() => navigation.navigate(GROUP_USER_ADD)}>
        <Text>GroupUserAdd</Text>
      </TouchableOpacity>
      <TouchableOpacity onPress={() => navigation.navigate(FRIEND_INVITED)}>
        <Text>FriendInvited</Text>
      </TouchableOpacity>
    </View>
  );
}
