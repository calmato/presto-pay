import React from "react";
import { StyleSheet, TextStyle, ViewStyle } from "react-native";

import { Button, Icon } from "~/components/atoms";

interface Props {
  title: string;
  titleColor: string;
  icon: string;
  onPress: () => void;
}

interface Style {
  button: ViewStyle;
  icon: TextStyle;
}

const styles = StyleSheet.create<Style>({
  button: {
    margin: 10,
  },
  icon: {
    fontSize: 22,
    marginRight: 10,
  },
});

const SocialIcon = (name: string): JSX.Element => {
  return <Icon name={name} style={styles.icon} />;
};

export default function SignInSocialButton(props: Props) {
  const { title, titleColor, icon, onPress } = props;

  return (
    <Button
      title={title}
      titleColor={titleColor}
      type="outline"
      style={styles.button}
      icon={SocialIcon(icon)}
      onPress={onPress}
    />
  );
}
