import { createStackNavigator } from "@react-navigation/stack";
import React from "react";

import FriendAddStackScreen from "./FriendAdd";
import FriendInvitedStackScreen from "./FriendInvited";

import { FriendSearch } from "~/components/pages";
import { FRIEND_ADD, FRIEND_SEARCH, FRIEND_INVITED } from "~/constants/path";

const Stack = createStackNavigator();

export default function FriendSearchStackScreen() {
  return (
    <Stack.Navigator initialRouteName={FRIEND_SEARCH}>
      <Stack.Screen name={FRIEND_SEARCH} component={FriendSearch} />
      <Stack.Screen name={FRIEND_ADD} component={FriendAddStackScreen} />
      <Stack.Screen name={FRIEND_INVITED} component={FriendInvitedStackScreen} />
    </Stack.Navigator>
  );
}
