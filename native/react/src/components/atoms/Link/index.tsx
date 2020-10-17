import React from "react";
import { StyleSheet, Text, TextStyle, TouchableOpacity } from "react-native";

import { COLOR } from "~/constants/theme";

interface Props {
  text: string;
  color: string;
  style?: TextStyle;
  onPress: () => void;
}

interface Style {
  link: TextStyle;
}

const styles = StyleSheet.create<Style>({
  link: {
    textDecorationLine: "underline",
  },
});

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
