import React from 'react';
import {createStackNavigator} from '@react-navigation/stack';
import {GROUPS, HOME, USER_INFO} from '~/constants/path';
import {Groups, Home, UserInfo} from '~/components/pages';
import {HeaderLeft} from '~/routes/Header';

const Stack = createStackNavigator();

export default function HomeNavigator() {
  return (
    <Stack.Navigator initialRouteName={HOME}>
      <Stack.Screen
        name={HOME}
        component={Home}
        options={{headerLeft: () => <HeaderLeft />}}
      />
      <Stack.Screen name={GROUPS} component={Groups} />
      <Stack.Screen name={USER_INFO} component={UserInfo} />
    </Stack.Navigator>
  );
}
