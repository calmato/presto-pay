import React from "react";
import { StyleSheet, Text, TouchableOpacity } from "react-native";

const styles = StyleSheet.create({
  link: {
    padding: 10,
    textAlign: "center",
    textDecorationLine: "underline",
  },
});

interface Props {
  text: string;
  color: string;
  onPress: () => void;
}

export default function SignInLink(props: Props) {
  const { text, color, onPress } = props;

  return (
    <TouchableOpacity onPress={onPress}>
      <Text style={{ ...styles.link, color }}>{text}</Text>
    </TouchableOpacity>
  );
}
