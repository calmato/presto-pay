import { createStackNavigator } from "@react-navigation/stack";
import React from "react";

import GroupUserListStackScreen from "./GroupUserList";

import { Group, GroupEdit, GroupUserList } from "~/components/pages";
import { GROUP, GROUP_EDIT, GROUP_USER_LIST } from "~/constants/path";

const Stack = createStackNavigator();

export default function GroupEditStackScreen() {
  return (
    <Stack.Navigator initialRouteName={GROUP_EDIT}>
      <Stack.Screen name={GROUP_EDIT} component={GroupEdit} />
      <Stack.Screen name={GROUP} component={Group} />
      <Stack.Screen name={GROUP_USER_LIST} component={GroupUserListStackScreen} />
    </Stack.Navigator>
  );
}
