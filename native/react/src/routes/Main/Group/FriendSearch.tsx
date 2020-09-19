import { createStackNavigator } from "@react-navigation/stack";
import React from "react";

import { FriendAdd, FriendSearch, FriendInvited } from "~/components/pages";
import { FRIEND_ADD, FRIEND_SEARCH, FRIEND_INVITED } from "~/constants/path";

const Stack = createStackNavigator();

export default function FriendSearchStackScreen() {
  return (
    <Stack.Navigator initialRouteName={FRIEND_SEARCH}>
      <Stack.Screen name={FRIEND_SEARCH} component={FriendSearch} />
      <Stack.Screen name={FRIEND_ADD} component={FriendAdd} />
      <Stack.Screen name={FRIEND_INVITED} component={FriendInvited} />
    </Stack.Navigator>
  );
}
