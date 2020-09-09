import { createStackNavigator } from "@react-navigation/stack";
import React from "react";

import { Groups } from "~/components/pages";
import { GROUPS } from "~/constants/path";

const Stack = createStackNavigator();

export default function HomeNavigator() {
  return (
    <Stack.Navigator initialRouteName={GROUPS}>
      <Stack.Screen name={GROUPS} component={Groups} />
    </Stack.Navigator>
  );
}
