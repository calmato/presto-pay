import React from "react";
import { useNavigation } from "@react-navigation/native";
import { StyleSheet, View, Text, TouchableOpacity } from "react-native";

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

export default function UserPasswordEdit() {
  const navigation = useNavigation();

  return (
    <View style={styles.container}>
      <Text style={styles.title}>UserPasswordEdit</Text>
      <TouchableOpacity onPress={() => navigation.goBack()}>
        <Text>goBack</Text>
      </TouchableOpacity>
    </View>
  );
}
