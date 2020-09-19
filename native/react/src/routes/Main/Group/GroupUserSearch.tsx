import { createStackNavigator } from "@react-navigation/stack";
import React from "react";

import { GroupUserAdd, GroupUserList, GroupUserSearch, FriendInvited } from "~/components/pages";
import { GROUP_USER_ADD, GROUP_USER_LIST, GROUP_USER_SEARCH, FRIEND_INVITED } from "~/constants/path";

const Stack = createStackNavigator();

export default function GroupUserSearchStackScreen() {
  return (
    <Stack.Navigator initialRouteName={GROUP_USER_SEARCH}>
      <Stack.Screen name={GROUP_USER_SEARCH} component={GroupUserSearch} />
      <Stack.Screen name={GROUP_USER_LIST} component={GroupUserList} />
      <Stack.Screen name={GROUP_USER_ADD} component={GroupUserAdd} />
      <Stack.Screen name={FRIEND_INVITED} component={FriendInvited} />
    </Stack.Navigator>
  );
}
