import { createStackNavigator } from "@react-navigation/stack";
import React from "react";

import FriendSearchStackScreen from "./FriendSearch";
import GroupStackScreen from "./Group";

import { GroupNew, GroupList } from "~/components/pages";
import { FRIEND_SEARCH, GROUP, GROUP_NEW, GROUP_LIST } from "~/constants/path";

const Stack = createStackNavigator();

export default function GroupNewStackScreen() {
  return (
    <Stack.Navigator initialRouteName={GROUP_NEW}>
      <Stack.Screen name={GROUP_NEW} component={GroupNew} />
      <Stack.Screen name={GROUP_LIST} component={GroupList} />
      <Stack.Screen name={GROUP} component={GroupStackScreen} />
      <Stack.Screen name={FRIEND_SEARCH} component={FriendSearchStackScreen} />
    </Stack.Navigator>
  );
}
