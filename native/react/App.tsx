import React from 'react';
import { StyleSheet, Text, View } from 'react-native';
import * as UiContext from '~/contexts/ui';
import Routes from '~/routes';

export default function App() {
  const [applicationState, setApplicationState] = React.useState(UiContext.createApplicationInitialState());

  return (
    <UiContext.Context.Provider value={{applicationState, setApplicationState}}>
      <Routes />
    </UiContext.Context.Provider>
  );
}
