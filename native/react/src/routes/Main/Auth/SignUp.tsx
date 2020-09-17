import { createStackNavigator } from "@react-navigation/stack";
import React from "react";

import { SignIn, SignUp } from "~/components/pages";
import { CHECK_EMAIL, SIGN_IN, SIGN_UP } from "~/constants/path";

import CheckEmail from "./CheckEmail";

const Stack = createStackNavigator();

export default function SignUpStackScreen() {
  return (
    <Stack.Navigator initialRouteName={SIGN_UP}>
      <Stack.Screen name={SIGN_UP} component={SignUp} />
      <Stack.Screen name={SIGN_IN} component={SignIn} />
      <Stack.Screen name={CHECK_EMAIL} component={CheckEmail} options={{ headerShown: false }} />
    </Stack.Navigator>
  );
}
