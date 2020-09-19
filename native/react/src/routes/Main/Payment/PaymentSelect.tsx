import { createStackNavigator } from "@react-navigation/stack";
import React from "react";

import { PaymentAdd, PaymentSelect } from "~/components/pages";
import { PAYMENT_ADD, PAYMENT_SELECT } from "~/constants/path";

const Stack = createStackNavigator();

export default function PaymentSelectStackScreen() {
  return (
    <Stack.Navigator initialRouteName={PAYMENT_SELECT}>
      <Stack.Screen name={PAYMENT_SELECT} component={PaymentSelect} />
      <Stack.Screen name={PAYMENT_ADD} component={PaymentAdd} />
    </Stack.Navigator>
  );
}
