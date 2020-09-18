import React from "react";
import { StyleSheet, View, Text, TouchableOpacity } from "react-native";

import { Profile } from "~/components/organisms";
import { Context, Status } from "~/contexts/ui";

const styles = StyleSheet.create({
  container: {
    flex: 1,
  },
});

export default function UserInfo() {
  const { setApplicationState } = React.useContext(Context);

  return (
    <View style={styles.container}>
      <Profile />
      <Text>UserInfo</Text>
      <TouchableOpacity onPress={() => setApplicationState(Status.UN_AUTHORIZED)}>
        <Text>SignOut</Text>
      </TouchableOpacity>
    </View>
  );
}
