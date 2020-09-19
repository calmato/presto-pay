import React from "react";
import { useNavigation } from "@react-navigation/native";
import { StyleSheet, View, Text, TouchableOpacity } from "react-native";

import { GROUP_EDIT } from "~/constants/path";

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

export default function Group() {
  const navigation = useNavigation();

  return (
    <View style={styles.container}>
      <Text style={styles.title}>Group</Text>
      <TouchableOpacity onPress={() => navigation.goBack()}>
        <Text>GroupList</Text>
      </TouchableOpacity>
      <TouchableOpacity onPress={() => navigation.navigate(GROUP_EDIT)}>
        <Text>GroupEdit</Text>
      </TouchableOpacity>
    </View>
  );
}
