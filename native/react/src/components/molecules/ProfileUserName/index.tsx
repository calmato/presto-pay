import React from "react";
import { Text, StyleSheet, ViewStyle } from "react-native";

import { width } from "~/lib/window";

interface Props {
  name: string;
  style?: ViewStyle;
}

const styles = StyleSheet.create({
  name: {
    margin: 20,
    fontSize: 18,
    maxWidth: width * 0.6,
  },
});

const ProfileUserName = function ProfileUserName(props: Props) {
  const { name } = props;

  return (
    <Text style={styles.name} numberOfLines={1}>
      {name}
    </Text>
  );
};

ProfileUserName.defaultProps = {
  name: "test-user",
};

export default ProfileUserName;
