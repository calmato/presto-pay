import { createStackNavigator } from "@react-navigation/stack";
import React from "react";

import { GroupEdit, GroupUserList, GroupUserSearch } from "~/components/pages";
import { GROUP_EDIT, GROUP_USER_LIST, GROUP_USER_SEARCH } from "~/constants/path";

const Stack = createStackNavigator();

export default function GroupUserListStackScreen() {
  return (
    <Stack.Navigator initialRouteName={GROUP_USER_LIST}>
      <Stack.Screen name={GROUP_USER_LIST} component={GroupUserList} />
      <Stack.Screen name={GROUP_EDIT} component={GroupEdit} />
      <Stack.Screen name={GROUP_USER_SEARCH} component={GroupUserSearch} />
    </Stack.Navigator>
  );
}
