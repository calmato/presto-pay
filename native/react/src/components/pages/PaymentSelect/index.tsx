import { useNavigation } from "@react-navigation/native";
import React from "react";
import { StyleSheet, View, Text, TouchableOpacity } from "react-native";

import { PAYMENT_ADD } from "~/constants/path";

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

export default function PaymentSelect() {
  const navigation = useNavigation();

  return (
    <View style={styles.container}>
      <Text style={styles.title}>PaymentSelect</Text>
      <TouchableOpacity onPress={() => navigation.goBack()}>
        <Text>goBack</Text>
      </TouchableOpacity>
      <TouchableOpacity onPress={() => navigation.navigate(PAYMENT_ADD)}>
        <Text>PaymentAdd</Text>
      </TouchableOpacity>
    </View>
  );
}
