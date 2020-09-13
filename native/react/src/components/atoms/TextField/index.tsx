import React from "react";
import { ViewStyle } from "react-native";
import { Input } from "react-native-elements";

interface Props {
  label?: string;
  placeholder?: string;
  onChangeText?: (str: string) => void;
  style?: ViewStyle;
  secureTextEntry?: boolean;
}

export default function TextField(props: Props) {
  const { label, placeholder, onChangeText = () => {}, style, secureTextEntry } = props;

  return (
    <Input
      label={label}
      placeholder={placeholder}
      onChangeText={onChangeText}
      style={style}
      secureTextEntry={secureTextEntry}
    />
  );
}
