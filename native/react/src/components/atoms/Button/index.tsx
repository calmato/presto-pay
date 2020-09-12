import React from "react";
import { TextStyle, ViewStyle } from "react-native";
import { Button as ElementsButton } from "react-native-elements";

interface Props {
  onPress: () => void;
  style?: ViewStyle | ViewStyle[];
  textStyle?: TextStyle | TextStyle[];
  title?: string;
  disabled?: boolean;
  disabledStyle?: ViewStyle | ViewStyle[];
  icon?: boolean;
}

export default function Button(props: Props) {
  const { onPress, title, style, disabled, disabledStyle, icon } = props;

  return (
    <ElementsButton
      onPress={onPress}
      title={title}
      style={style}
      disabled={disabled}
      disabledStyle={disabledStyle}
      icon={icon}
    />
  );
}
