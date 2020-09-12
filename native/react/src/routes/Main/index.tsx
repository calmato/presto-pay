import { createBottomTabNavigator } from "@react-navigation/bottom-tabs";
import { createDrawerNavigator } from "@react-navigation/drawer";
import { createStackNavigator, StackCardInterpolationProps } from "@react-navigation/stack";
import React from "react";

import Groups from "./Groups";
import Home from "./Home";
import UserInfo from "./UserInfo";

import { Initial, Loading, SignIn, SignUp } from "~/components/pages";
import { GROUPS, HOME, INITIAL, LOADING, SIGN_IN, SIGN_UP, USER_INFO } from "~/constants/path";
import * as UiContext from "~/contexts/ui";

const Stack = createStackNavigator();
const SignInStack = createStackNavigator();
const Tab = createBottomTabNavigator();
const GroupsDrawer = createDrawerNavigator();
const HomeDrawer = createDrawerNavigator();

// 画面遷移時のアニメーション
const forFade = ({ current }: StackCardInterpolationProps) => ({
  cardStyle: {
    opacity: current.progress,
  },
});

// 現在の画面名を取得
const getActiveRouteName = (state: any): string => {
  if (!state || !state.routes) {
    return "";
  }

  const route = state.routes[state.index];
  if (route.state) {
    return getActiveRouteName(route.state);
  }

  return route.name;
};

// ドロワー
function GroupsWithDrawer(): JSX.Element {
  return (
    <GroupsDrawer.Navigator initialRouteName={GROUPS}>
      <GroupsDrawer.Screen name={GROUPS} component={Groups} />
      <GroupsDrawer.Screen name={HOME} component={Home} />
    </GroupsDrawer.Navigator>
  );
}

function HomeWithDrawer(): JSX.Element {
  return (
    <HomeDrawer.Navigator initialRouteName={HOME}>
      <HomeDrawer.Screen name={HOME} component={Home} />
      <HomeDrawer.Screen name={USER_INFO} component={UserInfo} />
    </HomeDrawer.Navigator>
  );
}

function UserInfoWithDrawer(): JSX.Element {
  return (
    <GroupsDrawer.Navigator initialRouteName={USER_INFO}>
      <GroupsDrawer.Screen name={USER_INFO} component={UserInfo} />
      <GroupsDrawer.Screen name={HOME} component={Home} />
    </GroupsDrawer.Navigator>
  );
}

// フッタータブ
function TabRoutes(): JSX.Element {
  return (
    <Tab.Navigator
      initialRouteName={HOME}
      screenOptions={(props: any) => {
        const routeName = getActiveRouteName(props.route.state);

        return {
          tabBarVisible: routeName !== USER_INFO,
        };
      }}
    >
      <Tab.Screen name={GROUPS} component={GroupsWithDrawer} />
      <Tab.Screen name={HOME} component={HomeWithDrawer} />
      <Tab.Screen name={USER_INFO} component={UserInfoWithDrawer} />
    </Tab.Navigator>
  );
}

// ナビゲーター
function SignInNavigator(): JSX.Element {
  return (
    <SignInStack.Navigator initialRouteName={SIGN_IN} headerMode="none">
      <SignInStack.Screen name={SIGN_IN} component={SignIn} />
      <SignInStack.Screen name={SIGN_UP} component={SignUp} />
    </SignInStack.Navigator>
  );
}

function switchingAuthStatus(status: UiContext.Status): JSX.Element {
  switch (status) {
    case UiContext.Status.UN_AUTHORIZED:
      return <Stack.Screen name={SIGN_IN} component={SignInNavigator} />;
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
    <Stack.Navigator initialRouteName={LOADING} headerMode="none" screenOptions={{ cardStyleInterpolator: forFade }}>
      {uiContext.applicationState !== UiContext.Status.LOADING ? (
        switchingAuthStatus(uiContext.applicationState)
      ) : (
        <Stack.Screen name={LOADING} component={Loading} />
      )}
    </Stack.Navigator>
  );
}
