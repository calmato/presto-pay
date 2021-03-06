import { Ionicons } from "@expo/vector-icons";
import React from "react";
import { TextStyle, ViewStyle } from "react-native";

import { COLOR } from "~/constants/theme";

interface Props {
  name: string;
  color: string;
  style?: TextStyle;
}

const Icon = function Icon(props: Props) {
  const { name, style, color } = props;

  return <Ionicons name={name} color={color} style={style} />;
};

Icon.defaultProps = {
  color: COLOR.MAIN_DARK,
};

export default Icon;
