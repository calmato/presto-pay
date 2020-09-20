import { createStackNavigator } from "@react-navigation/stack";
import React from "react";

import { GroupUserAdd, GroupUserList, GroupUserSearch } from "~/components/pages";
import { GROUP_USER_ADD, GROUP_USER_LIST, GROUP_USER_SEARCH } from "~/constants/path";

const Stack = createStackNavigator();

export default function GroupUserAddStackScreen() {
  return (
    <Stack.Navigator initialRouteName={GROUP_USER_ADD}>
      <Stack.Screen name={GROUP_USER_ADD} component={GroupUserAdd} />
      <Stack.Screen name={GROUP_USER_SEARCH} component={GroupUserSearch} />
      <Stack.Screen name={GROUP_USER_LIST} component={GroupUserList} />
    </Stack.Navigator>
  );
}
