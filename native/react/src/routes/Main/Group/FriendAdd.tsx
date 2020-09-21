import { createStackNavigator } from "@react-navigation/stack";
import React from "react";

import { FriendAdd, FriendSearch, GroupList } from "~/components/pages";
import { FRIEND_ADD, FRIEND_SEARCH, GROUP_LIST } from "~/constants/path";

const Stack = createStackNavigator();

export default function FriendAddStackScreen() {
  return (
    <Stack.Navigator initialRouteName={FRIEND_ADD}>
      <Stack.Screen name={FRIEND_ADD} component={FriendAdd} />
      <Stack.Screen name={FRIEND_SEARCH} component={FriendSearch} />
      <Stack.Screen name={GROUP_LIST} component={GroupList} />
    </Stack.Navigator>
  );
}
