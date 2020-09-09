import { createStackNavigator } from "@react-navigation/stack";
import React from "react";

import { UserInfo } from "~/components/pages";
import { USER_INFO } from "~/constants/path";

const Stack = createStackNavigator();

export default function UserInfoNavigator() {
  return (
    <Stack.Navigator initialRouteName={USER_INFO}>
      <Stack.Screen name={USER_INFO} component={UserInfo} />
    </Stack.Navigator>
  );
}
