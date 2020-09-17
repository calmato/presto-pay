import { createStackNavigator } from "@react-navigation/stack";
import React from "react";

import SignUp from "./SignUp";

import { PasswordReset, SignIn } from "~/components/pages";
import { PASSWORD_RESET, SIGN_IN, SIGN_UP } from "~/constants/path";

const Stack = createStackNavigator();

export default function SignInStackScreen() {
  return (
    <Stack.Navigator initialRouteName={SIGN_IN} mode="modal" headerMode="none">
      <Stack.Screen name={SIGN_IN} component={SignIn} />
      <Stack.Screen name={SIGN_UP} component={SignUp} />
      <Stack.Screen name={PASSWORD_RESET} component={PasswordReset} />
    </Stack.Navigator>
  );
}
