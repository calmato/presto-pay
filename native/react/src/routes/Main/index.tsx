import React from 'react';
import {createStackNavigator, StackCardInterpolationProps} from '@react-navigation/stack';
import {HOME, INITIAL, LOADING, SIGN_IN} from '~/constants/path';
import {Home, Initial, Loading, SignIn} from '~/components/pages';
import * as UiContext from '~/contexts/ui';

const Stack = createStackNavigator();

const forFade = ({current}: StackCardInterpolationProps) => ({
  cardStyle: {
    opacity: current.progress,
  },
});

function switchingAuthStatus(status: UiContext.Status): JSX.Element {
  switch (status) {
    case UiContext.Status.UN_AUTHORIZED:
      return <Stack.Screen name={SIGN_IN} component={SignIn} />;
    case UiContext.Status.AUTHORIZED:
      return <Stack.Screen name={HOME} component={Home} />;
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
