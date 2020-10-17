import React from "react";
import { StyleSheet, Text, TextStyle, TouchableOpacity, ViewStyle } from "react-native";

import { COLOR } from "~/constants/theme";

const styles = StyleSheet.create({
  link: {
    textDecorationLine: "underline",
  },
});

interface Props {
  text: string;
  color: string;
  style?: TextStyle;
  onPress: () => void;
}

export default function Link(props: Props) {
  const { text, color, style, onPress } = props;

  return (
    <TouchableOpacity onPress={onPress}>
      <Text style={[styles.link, style, { color }]}>{text}</Text>
    </TouchableOpacity>
  );
}

Link.defaultProps = {
  color: COLOR.PRIMARY,
}
