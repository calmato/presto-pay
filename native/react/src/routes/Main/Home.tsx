import { createStackNavigator } from "@react-navigation/stack";
import React from "react";

import { GroupList, Home, UserInfo } from "~/components/pages";
import { GROUP_LIST, HOME, USER_INFO } from "~/constants/path";
import { HeaderLeft } from "~/routes/Header";

const Stack = createStackNavigator();

export default function HomeNavigator() {
  return (
    <Stack.Navigator initialRouteName={HOME}>
      <Stack.Screen name={HOME} component={Home} options={{ headerLeft: () => <HeaderLeft /> }} />
      <Stack.Screen name={GROUP_LIST} component={GroupList} />
      <Stack.Screen name={USER_INFO} component={UserInfo} />
    </Stack.Navigator>
  );
}
