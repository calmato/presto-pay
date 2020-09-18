import React from "react";
import { Image, ViewStyle } from "react-native";

interface Props {
  uri: string;
  style?: ViewStyle;
}

const ProfileImage = function ProfileImage(props: Props) {
  const { uri } = props;

  return (
    <Image
      source={{ uri }}
      style={{
        width: 50,
        height: 50,
        borderRadius: 25,
      }}
    />
  );
};

ProfileImage.defaultProps = {
  uri: "https://reactnative.dev/img/tiny_logo.png",
};

export default ProfileImage;
