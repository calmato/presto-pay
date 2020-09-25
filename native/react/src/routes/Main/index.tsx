import { Ionicons } from "@expo/vector-icons";
import { createBottomTabNavigator } from "@react-navigation/bottom-tabs";
import { createDrawerNavigator } from "@react-navigation/drawer";
import { createStackNavigator, StackCardInterpolationProps } from "@react-navigation/stack";
import React from "react";

import { SignInStackScreen } from "./Auth";
import { GroupListStackScreen } from "./Group";
import { default as HomeStackScreen } from "./Home";
import { PaymentSelectStackScreen } from "./Payment";
import { UserInfoStackScreen } from "./System";

import { Initial } from "~/components/pages";
import { GROUP_LIST, HOME, INITIAL, LOADING, SIGN_IN, USER_INFO } from "~/constants/path";
import { COLOR } from "~/constants/theme";
import { Loading } from "~/containers";
import * as UiContext from "~/contexts/ui";

const Stack = createStackNavigator();
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

// TODO: Refactor - ドロワー
function GroupsWithDrawer(): JSX.Element {
  return (
    <GroupsDrawer.Navigator initialRouteName={GROUP_LIST}>
      <GroupsDrawer.Screen name={HOME} component={HomeStackScreen} />
      <GroupsDrawer.Screen name={GROUP_LIST} component={GroupListStackScreen} />
    </GroupsDrawer.Navigator>
  );
}

function HomeWithDrawer(): JSX.Element {
  return (
    <HomeDrawer.Navigator initialRouteName={HOME}>
      <HomeDrawer.Screen name={HOME} component={HomeStackScreen} />
      <HomeDrawer.Screen name={USER_INFO} component={UserInfoStackScreen} />
    </HomeDrawer.Navigator>
  );
}

function UserInfoWithDrawer(): JSX.Element {
  return (
    <GroupsDrawer.Navigator initialRouteName={USER_INFO}>
      <GroupsDrawer.Screen name={HOME} component={HomeStackScreen} />
      <GroupsDrawer.Screen name={USER_INFO} component={UserInfoStackScreen} />
    </GroupsDrawer.Navigator>
  );
}

// TODO: Refactor - フッタータブ
function TabRoutes(): JSX.Element {
  return (
    <Tab.Navigator
      initialRouteName={HOME}
      screenOptions={({ route }) => ({
        tabBarIcon: ({ focused, size }) => {
          let iconName: string;

          switch (route.name) {
            case "HOME":
              iconName = "md-home";
              break;
            case "USER_INFO":
              iconName = "md-person";
              break;
            case "GROUP_LIST":
              iconName = "md-people";
              break;
            default:
              iconName = "md-person";
              break;
          }

          return <Ionicons name={iconName} size={size} color={focused ? COLOR.PRIMARY : COLOR.MAIN_DARK} />;
        },
      })}
      tabBarOptions={{
        tabStyle: {
          backgroundColor: COLOR.MAIN,
        },
        inactiveTintColor: COLOR.MAIN_DARK,
      }}
    >
      <Tab.Screen name={GROUP_LIST} component={GroupsWithDrawer} />
      <Tab.Screen name={HOME} component={HomeWithDrawer} />
      <Tab.Screen name={USER_INFO} component={UserInfoWithDrawer} />
    </Tab.Navigator>
  );
}

// TODO: 認証機能実装後リファクタ
// アプリ起動時、認証情報によってページ遷移
function SwitchingAuthStatus(status: UiContext.Status): JSX.Element {
  switch (status) {
    case UiContext.Status.UN_AUTHORIZED:
      return <Stack.Screen name={SIGN_IN} component={SignInStackScreen} />;
    case UiContext.Status.AUTHORIZED:
      return <Stack.Screen name={HOME} component={TabRoutes} />;
    case UiContext.Status.FIRST_OPEN:
    default:
      return <Stack.Screen name={INITIAL} component={Initial} />;
  }
}

export default function MainRoutes() {
  const uiContext = React.useContext(UiContext.Context);

  return (
    <Stack.Navigator initialRouteName={LOADING} headerMode="none" screenOptions={{ cardStyleInterpolator: forFade }}>
      {uiContext.applicationState !== UiContext.Status.LOADING ? (
        SwitchingAuthStatus(uiContext.applicationState)
      ) : (
        <Stack.Screen name={LOADING} component={Loading} />
      )}
    </Stack.Navigator>
  );
}
