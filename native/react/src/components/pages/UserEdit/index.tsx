import React from "react";
import { StyleSheet, View, Text, TouchableOpacity } from "react-native";

import { Context, Status } from "~/contexts/ui";

const styles = StyleSheet.create({
  container: {
    flex: 1,
    justifyContent: "center",
    alignItems: "center",
  },
});

export default function UserEdit() {
  return (
    <View style={styles.container}>
      <Text>UserEdit</Text>
    </View>
  );
}
