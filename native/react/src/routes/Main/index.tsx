import React from 'react';
import {createStackNavigator, StackCardInterpolationProps} from '@react-navigation/stack';
import {createBottomTabNavigator} from '@react-navigation/bottom-tabs';
import {GROUPS, HOME, INITIAL, LOADING, SIGN_IN, USER_INFO} from '~/constants/path';
import {Groups, Home, Initial, Loading, SignIn, UserInfo} from '~/components/pages';
import * as UiContext from '~/contexts/ui';

const Stack = createStackNavigator();
const Tab = createBottomTabNavigator();

const forFade = ({current}: StackCardInterpolationProps) => ({
  cardStyle: {
    opacity: current.progress,
  },
});

function TabRoutes(): JSX.Element {
  return (
    <Tab.Navigator initialRouteName={HOME}>
      <Tab.Screen name={GROUPS} component={Groups} />
      <Tab.Screen name={HOME} component={Home} />
      <Tab.Screen name={USER_INFO} component={UserInfo} />
    </Tab.Navigator>
  )
}

function switchingAuthStatus(status: UiContext.Status): JSX.Element {
  switch (status) {
    case UiContext.Status.UN_AUTHORIZED:
      return <Stack.Screen name={SIGN_IN} component={SignIn} />;
    case UiContext.Status.AUTHORIZED:
      return <Stack.Screen name={HOME} component={TabRoutes} />;
    case UiContext.Status.FIRST_OPEN:
    default:
      return <Stack.Screen name={INITIAL} component={Initial} />;
  }
}

export default function AuthWithRoutes() {
  const uiContext = React.useContext(UiContext.Context);

  return (
    <Stack.Navigator initialRouteName={LOADING} headerMode="none" screenOptions={{cardStyleInterpolator: forFade}}>
      {uiContext.applicationState !== UiContext.Status.LOADING ? (
        switchingAuthStatus(uiContext.applicationState)
      ) : (
        <Stack.Screen name={LOADING} component={Loading} />
      )}
    </Stack.Navigator>
  )
}
