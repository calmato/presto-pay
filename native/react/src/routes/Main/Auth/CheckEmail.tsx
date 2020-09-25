import { createStackNavigator } from "@react-navigation/stack";
import React from "react";

import { CheckEmail, SignUp } from "~/components/pages";
import { CHECK_EMAIL, SIGN_IN, SIGN_UP } from "~/constants/path";
import { SignIn } from "~/containers";

const Stack = createStackNavigator();

export default function CheckEmailStackScreen() {
  return (
    <Stack.Navigator initialRouteName={CHECK_EMAIL} headerMode="none">
      <Stack.Screen name={CHECK_EMAIL} component={CheckEmail} />
      <Stack.Screen name={SIGN_UP} component={SignUp} />
      <Stack.Screen name={SIGN_IN} component={SignIn} options={{ headerShown: false }} />
    </Stack.Navigator>
  );
}
