import React from "react";
import { TextStyle, ViewStyle } from "react-native";
import { Button as ElementsButton } from "react-native-elements";

import { COLOR } from "~/constants/theme";

interface Props {
  backgroundColor?: string;
  disabled?: boolean;
  disabledStyle?: ViewStyle | ViewStyle[];
  icon?: any;
  onPress: () => void;
  style?: ViewStyle | ViewStyle[];
  title?: string;
  titleColor?: string;
  type?: "solid" | "clear" | "outline";
  textStyle?: TextStyle | TextStyle[];
}

export default function Button(props: Props) {
  const {
    backgroundColor = COLOR.MAIN,
    disabled = false,
    disabledStyle,
    icon,
    onPress,
    style,
    title,
    titleColor = COLOR.PRIMARY,
    type = "solid",
  } = props;

  return (
    <ElementsButton
      buttonStyle={[{ borderColor: titleColor, backgroundColor }]}
      containerStyle={style}
      disabled={disabled}
      disabledStyle={disabledStyle}
      icon={icon}
      onPress={onPress}
      title={title}
      titleStyle={[{ color: titleColor }]}
      type={type}
    />
  );
}
