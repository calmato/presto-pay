import { createStackNavigator } from "@react-navigation/stack";
import React from "react";

import { FriendSearch, Group, GroupNew, GroupList } from "~/components/pages";
import { FRIEND_SEARCH, GROUP, GROUP_NEW, GROUP_LIST } from "~/constants/path";

const Stack = createStackNavigator();

export default function GroupListStackScreen() {
  return (
    <Stack.Navigator initialRouteName={GROUP_LIST}>
      <Stack.Screen name={GROUP_LIST} component={GroupList} />
      <Stack.Screen name={GROUP} component={Group} />
      <Stack.Screen name={GROUP_NEW} component={GroupNew} />
      <Stack.Screen name={FRIEND_SEARCH} component={FriendSearch} />
    </Stack.Navigator>
  );
}
