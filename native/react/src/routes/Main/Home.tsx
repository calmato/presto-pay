import { createStackNavigator } from "@react-navigation/stack";
import React from "react";

import { GroupListStackScreen } from "./Group";
import { PaymentSelectStackScreen } from "./Payment";
import { UserInfoStackScreen } from "./System";

import { Home } from "~/components/pages";
import { GROUP_LIST, HOME, PAYMENT_SELECT, USER_INFO } from "~/constants/path";
import { HEADER_STYLE } from "~/constants/theme";
import { HeaderLeft } from "~/routes/Header";

const Stack = createStackNavigator();

export default function HomeStackScreen() {
  return (
    <Stack.Navigator initialRouteName={HOME} screenOptions={{ ...HEADER_STYLE }}>
      <Stack.Screen name={HOME} component={Home} options={{ headerLeft: () => <HeaderLeft /> }} />
      <Stack.Screen name={GROUP_LIST} component={GroupListStackScreen} />
      <Stack.Screen name={PAYMENT_SELECT} component={PaymentSelectStackScreen} />
      <Stack.Screen name={USER_INFO} component={UserInfoStackScreen} />
    </Stack.Navigator>
  );
}
