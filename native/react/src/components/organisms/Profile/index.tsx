import React from "react";
import { Text, View, TextStyle, ViewStyle, StyleSheet } from 'react-native';
import { Card } from "react-native-elements";
import { ProfileImage, ProfileUserName } from "~/components/molecules"

interface Props {
}

const styles = StyleSheet.create({
  card: {
    justifyContent: "center",
    flexDirection: "row",
  },
  profileImage: {
    flex: 0.4,
  },
  name: {
    fontSize: 18,
    flex: 0.6,
    height: 50
  },
})

export default function Profile(props: Props) {
  // const { onPress, title, style, disabled, disabledStyle, icon } = props;

  return (
    <Card>
      <View>
        <ProfileImage
          style={styles.profileImage}
        />
        <ProfileUserName
          style={styles.name}
        />
      </View>
    </Card>
  );
}

