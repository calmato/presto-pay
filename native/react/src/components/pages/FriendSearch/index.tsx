import React from "react";
import { useNavigation } from "@react-navigation/native";
import { StyleSheet, View, Text, TouchableOpacity } from "react-native";

import { FRIEND_ADD, FRIEND_INVITED } from "~/constants/path";

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

export default function FriendSearch() {
  const navigation = useNavigation();

  return (
    <View style={styles.container}>
      <Text style={styles.title}>FriendSearch</Text>
      <TouchableOpacity onPress={() => navigation.goBack()}>
        <Text>goBack</Text>
      </TouchableOpacity>
      <TouchableOpacity onPress={() => navigation.navigate(FRIEND_ADD)}>
        <Text>FriendAdd</Text>
      </TouchableOpacity>
      <TouchableOpacity onPress={() => navigation.navigate(FRIEND_INVITED)}>
        <Text>FriendInvited</Text>
      </TouchableOpacity>
    </View>
  );
}
