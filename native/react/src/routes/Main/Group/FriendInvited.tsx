import { createStackNavigator } from "@react-navigation/stack";
import React from "react";

import { FriendInvited, GroupList, GroupUserSearch } from "~/components/pages";
import { FRIEND_INVITED, GROUP_LIST, GROUP_USER_SEARCH } from "~/constants/path";

const Stack = createStackNavigator();

export default function FriendInvitedStackScreen() {
  return (
    <Stack.Navigator initialRouteName={FRIEND_INVITED}>
      <Stack.Screen name={FRIEND_INVITED} component={FriendInvited} />
      <Stack.Screen name={GROUP_LIST} component={GroupList} />
      <Stack.Screen name={GROUP_USER_SEARCH} component={GroupUserSearch} />
    </Stack.Navigator>
  );
}
