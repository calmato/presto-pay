import React from 'react';
import {createStackNavigator} from '@react-navigation/stack';
import {GROUPS} from '~/constants/path';
import {Groups} from '~/components/pages';

const Stack = createStackNavigator();

export default function HomeNavigator() {
  return (
    <Stack.Navigator initialRouteName={GROUPS}>
      <Stack.Screen name={GROUPS} component={Groups} />
    </Stack.Navigator>
  );
}
