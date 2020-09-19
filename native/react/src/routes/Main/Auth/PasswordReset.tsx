import { createStackNavigator } from "@react-navigation/stack";
import React from "react";

import CheckEmail from "./CheckEmail";

import { SignIn, PasswordReset } from "~/components/pages";
import { CHECK_EMAIL, SIGN_IN, PASSWORD_RESET } from "~/constants/path";

const Stack = createStackNavigator();

export default function SignUpStackScreen() {
  return (
    <Stack.Navigator initialRouteName={PASSWORD_RESET}>
      <Stack.Screen name={PASSWORD_RESET} component={PasswordReset} />
      <Stack.Screen name={SIGN_IN} component={SignIn} />
      <Stack.Screen name={CHECK_EMAIL} component={CheckEmail} options={{ headerShown: false }} />
    </Stack.Navigator>
  );
}
