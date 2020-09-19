import { createStackNavigator } from "@react-navigation/stack";
import React from "react";

import GroupEditStackScreen from "./GroupEdit";

import { Group, GroupList } from "~/components/pages";
import { GROUP, GROUP_EDIT, GROUP_LIST } from "~/constants/path";

const Stack = createStackNavigator();

export default function GrouptackScreen() {
  return (
    <Stack.Navigator initialRouteName={GROUP}>
      <Stack.Screen name={GROUP} component={Group} />
      <Stack.Screen name={GROUP_LIST} component={GroupList} />
      <Stack.Screen name={GROUP_EDIT} component={GroupEditStackScreen} />
    </Stack.Navigator>
  );
}
