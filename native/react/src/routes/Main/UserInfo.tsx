import React from 'react';
import {createStackNavigator} from '@react-navigation/stack';
import {USER_INFO} from '~/constants/path';
import {UserInfo} from '~/components/pages';

const Stack = createStackNavigator();

export default function UserInfoNavigator() {
  return (
    <Stack.Navigator initialRouteName={USER_INFO}>
      <Stack.Screen name={USER_INFO} component={UserInfo} />
    </Stack.Navigator>
  );
}
