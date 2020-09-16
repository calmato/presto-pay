import React from "react";
import { Image, ViewStyle, Text } from 'react-native';

interface Props {
  uri?: string;
  style?: ViewStyle;
}

export default function ProfileImage(props: Props) {
  const { uri } = props;

  return (
    <Image
      source={{
        uri: 'https://reactnative.dev/img/tiny_logo.png',
      }}
      style={{
        width: 50,
        height: 50,
        borderRadius: 25
      }}
    />
  );
}
