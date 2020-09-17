import React from "react";
import { Text, View, TextStyle, ViewStyle, StyleSheet } from "react-native";

import { ProfileImage, ProfileUserName } from "~/components/molecules";

// TODO: 型定義を別に切り出す
interface User {
  uid: string;
  name: string;
  displayName: string;
  email: string;
  emailVerified: boolean;
  phoneNumber: string;
  password: string;
  photoURL: string;
  disabled: boolean;
}

interface Props {
  user: User;
}

const styles = StyleSheet.create({
  card: {
    justifyContent: "center",
    flexDirection: "row",
    marginTop: 30,
    marginBottom: 30,
    alignItems: "center",
  },
  profileImage: {
    flex: 0.4,
  },
  name: {
    flex: 0.6,
    height: 50,
  },
});

const Profile = function Profile(props: Props) {
  const { user } = props;

  return (
    <View style={styles.card}>
      <ProfileImage uri={user.photoURL} style={styles.profileImage} />
      <ProfileUserName name={user.displayName} style={styles.name} />
    </View>
  );
};

Profile.defaultProps = {
  user: {
    uid: "test",
    name: "test-user-name",
    displayName: "test-display-user-name",
    email: "sample@sample.com",
    emailVerified: true,
    phoneNumber: "00011112222",
    password: "hash",
    photoURL: "https://avatars3.githubusercontent.com/u/45778633?s=400&u=d3e6035ae09c6fc39656916113866903c43d73a9&v=4",
    disabled: false,
  },
};

export default Profile;
