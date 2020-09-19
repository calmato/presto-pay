import React from "react";
import { useNavigation } from "@react-navigation/native";
import { StyleSheet, View, Text, TouchableOpacity } from "react-native";

import { GROUP } from "~/constants/path";

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

export default function PaymentAdd() {
  const navigation = useNavigation();

  return (
    <View style={styles.container}>
      <Text style={styles.title}>PaymentAdd</Text>
      <TouchableOpacity onPress={() => navigation.goBack()}>
        <Text>PaymentSelect</Text>
      </TouchableOpacity>
      <TouchableOpacity onPress={() => navigation.navigate(GROUP)}>
        <Text>Group</Text>
      </TouchableOpacity>
    </View>
  );
}
