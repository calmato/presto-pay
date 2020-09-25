import { createStackNavigator } from "@react-navigation/stack";
import React from "react";

import { NotificationInfo, UserEdit, UserInfo, UserPasswordEdit } from "~/components/pages";
import { NOTIFICATION_INFO, USER_EDIT, USER_INFO, USER_PASSWORD_EDIT } from "~/constants/path";
import { HEADER_STYLE } from "~/constants/theme";

const Stack = createStackNavigator();

export default function UserInfoStackScreen() {
  return (
    <Stack.Navigator initialRouteName={USER_INFO} screenOptions={{ ...HEADER_STYLE }}>
      <Stack.Screen name={USER_INFO} component={UserInfo} />
      <Stack.Screen name={USER_EDIT} component={UserEdit} />
      <Stack.Screen name={USER_PASSWORD_EDIT} component={UserPasswordEdit} />
      <Stack.Screen name={NOTIFICATION_INFO} component={NotificationInfo} />
    </Stack.Navigator>
  );
}
