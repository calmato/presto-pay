import React from 'react';
import {createStackNavigator, StackCardInterpolationProps} from '@react-navigation/stack';
import {createBottomTabNavigator} from '@react-navigation/bottom-tabs';
import {createDrawerNavigator} from '@react-navigation/drawer';
import {GROUPS, HOME, INITIAL, LOADING, SIGN_IN, USER_INFO} from '~/constants/path';
import {Initial, Loading, SignIn} from '~/components/pages';
import * as UiContext from '~/contexts/ui';
import Groups from './Groups';
import Home from './Home';
import UserInfo from './UserInfo';

const Stack = createStackNavigator();
const Tab = createBottomTabNavigator();
const GroupsDrawer = createDrawerNavigator();
const HomeDrawer = createDrawerNavigator();
const UserInfoDrawer = createDrawerNavigator();

const forFade = ({current}: StackCardInterpolationProps) => ({
  cardStyle: {
    opacity: current.progress,
  },
});

function GroupsWithDrawer(): JSX.Element {
  return (
    <GroupsDrawer.Navigator initialRouteName={GROUPS}>
      <GroupsDrawer.Screen name={GROUPS} component={Groups} />
      <GroupsDrawer.Screen name={HOME} component={Home} />
    </GroupsDrawer.Navigator>
  )
}

function HomeWithDrawer(): JSX.Element {
  return (
    <HomeDrawer.Navigator initialRouteName={HOME}>
      <HomeDrawer.Screen name={HOME} component={Home} />
      <HomeDrawer.Screen name={USER_INFO} component={UserInfo} />
    </HomeDrawer.Navigator>
  )
}

function UserInfoWithDrawer(): JSX.Element {
  return (
    <GroupsDrawer.Navigator initialRouteName={USER_INFO}>
      <GroupsDrawer.Screen name={USER_INFO} component={UserInfo} />
      <GroupsDrawer.Screen name={HOME} component={Home} />
    </GroupsDrawer.Navigator>
  )
}

function TabRoutes(): JSX.Element {
  return (
    <Tab.Navigator initialRouteName={HOME}>
      <Tab.Screen name={GROUPS} component={GroupsWithDrawer} />
      <Tab.Screen name={HOME} component={HomeWithDrawer} />
      <Tab.Screen name={USER_INFO} component={UserInfoWithDrawer} />
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
