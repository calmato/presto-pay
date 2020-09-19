import { useNavigation } from "@react-navigation/native";
import React from "react";
import { StyleSheet, View, Text, TouchableOpacity } from "react-native";

import { Profile } from "~/components/organisms";
import { USER_EDIT, USER_PASSWORD_EDIT, NOTIFICATION_INFO } from "~/constants/path";
import { Context, Status } from "~/contexts/ui";

const styles = StyleSheet.create({
  container: {
    flex: 1,
  },
  title: {
    fontSize: 36,
    fontWeight: "bold",
  },
});

export default function UserInfo() {
  const navigation = useNavigation();
  const { setApplicationState } = React.useContext(Context);

  return (
    <View style={styles.container}>
      <Profile />
      <Text style={styles.title}>UserInfo</Text>
      <TouchableOpacity onPress={() => navigation.navigate(USER_EDIT)}>
        <Text>UserEdit</Text>
      </TouchableOpacity>
      <TouchableOpacity onPress={() => navigation.navigate(USER_PASSWORD_EDIT)}>
        <Text>UserPasswordEdit</Text>
      </TouchableOpacity>
      <TouchableOpacity onPress={() => navigation.navigate(NOTIFICATION_INFO)}>
        <Text>NotificationInfo</Text>
      </TouchableOpacity>
      <TouchableOpacity onPress={() => setApplicationState(Status.UN_AUTHORIZED)}>
        <Text>SignOut</Text>
      </TouchableOpacity>
    </View>
  );
}
