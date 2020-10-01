import React from "react";
import { View, StyleSheet } from "react-native";

import { ProfileImage, ProfileUserName } from "~/components/molecules";
import { Auth } from "~/domain/models";

interface Props {
  auth: Auth.Model;
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
  const { auth } = props;

  return (
    <View style={styles.card}>
      <ProfileImage uri={auth.thumbnailUrl} style={styles.profileImage} />
      <ProfileUserName name={auth.username} style={styles.name} />
    </View>
  );
};

Profile.defaultProps = {
  auth: {
    id: "test",
    name: "test-user-name",
    username: "test-display-user-name",
    email: "sample@sample.com",
    emailVerified: true,
    thumbnailUrl:
      "https://avatars3.githubusercontent.com/u/45778633?s=400&u=d3e6035ae09c6fc39656916113866903c43d73a9&v=4",
  },
};

export default Profile;
