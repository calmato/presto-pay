import { createStackNavigator } from "@react-navigation/stack";
import React from "react";

import { FriendSearch, Group, GroupEdit, GroupList } from "~/components/pages";
import { FRIEND_SEARCH, GROUP, GROUP_EDIT, GROUP_LIST } from "~/constants/path";

const Stack = createStackNavigator();

export default function GrouptackScreen() {
  return (
    <Stack.Navigator initialRouteName={GROUP}>
      <Stack.Screen name={GROUP} component={Group} />
      <Stack.Screen name={GROUP_LIST} component={GroupList} />
      <Stack.Screen name={GROUP_EDIT} component={GroupEdit} />
      <Stack.Screen name={FRIEND_SEARCH} component={FriendSearch} />
    </Stack.Navigator>
  );
}
