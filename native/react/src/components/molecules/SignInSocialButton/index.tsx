import React from "react";
import { StyleSheet } from "react-native";

import { Button, Icon } from "~/components/atoms";

const styles = StyleSheet.create({
  icon: {
    fontSize: 22,
    marginRight: 10,
  },
  button: {
    margin: 10,
  },
});

interface Props {
  title: string;
  titleColor: string;
  icon: string;
  onPress: () => void;
}

const SocialIcon = (name: string): JSX.Element => {
  return <Icon name={name} style={styles.icon} />;
}

export default function SignInSocialButton(props: Props) {
  const { title, titleColor, icon, onPress } = props;

  return (
    <Button
      icon={SocialIcon(icon)}
      onPress={onPress}
      title={title}
      titleColor={titleColor}
      style={styles.button}
      type="outline"
    />
  );
}
