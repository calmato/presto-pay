import { createStackNavigator } from "@react-navigation/stack";
import React from "react";

import { GroupStackScreen } from "../Group";

import { PaymentAdd, PaymentSelect } from "~/components/pages";
import { PAYMENT_ADD, PAYMENT_SELECT, GROUP } from "~/constants/path";

const Stack = createStackNavigator();

export default function PaymentSelectStackScreen() {
  return (
    <Stack.Navigator initialRouteName={PAYMENT_ADD}>
      <Stack.Screen name={PAYMENT_ADD} component={PaymentAdd} />
      <Stack.Screen name={PAYMENT_SELECT} component={PaymentSelect} />
      <Stack.Screen name={GROUP} component={GroupStackScreen} />
    </Stack.Navigator>
  );
}
