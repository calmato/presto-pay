import { createStackNavigator } from "@react-navigation/stack";
import React from "react";

import { GroupList } from "~/components/pages";
import { GROUP_LIST } from "~/constants/path";

const Stack = createStackNavigator();

export default function GroupListNavigator() {
  return (
    <Stack.Navigator initialRouteName={GROUP_LIST}>
      <Stack.Screen name={GROUP_LIST} component={GroupList} />
    </Stack.Navigator>
  );
}
