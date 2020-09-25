import React from "react";
import { SafeAreaProvider } from "react-native-safe-area-context";
import { Provider } from "react-redux";

import * as AuthContext from "~/contexts/auth";
import * as NetworkContext from "~/contexts/network";
import * as UiContext from "~/contexts/ui";
import Routes from "~/routes";
import store from "~/store";

export default function App() {
  const [applicationState, setApplicationState] = React.useState(UiContext.createApplicationInitialState());
  const [error, setError] = React.useState(UiContext.createErrorInitialState());
  const [snackbar, setSnackbar] = React.useState(UiContext.createSnackbarInitialState());

  const [networkState, dispatchNetworkActions] = React.useReducer(
    NetworkContext.reducer,
    NetworkContext.createInitialState()
  );

  const [authState, setAuthState] = React.useState(AuthContext.createInitialState());

  return (
    <Provider store={store}>
      <SafeAreaProvider>
        <UiContext.Context.Provider
          value={{ applicationState, setApplicationState, error, setError, snackbar, setSnackbar }}
        >
          <NetworkContext.Context.Provider value={{ networkState, dispatchNetworkActions }}>
            <AuthContext.Context.Provider value={{ authState, setAuthState }}>
              <Routes />
            </AuthContext.Context.Provider>
          </NetworkContext.Context.Provider>
        </UiContext.Context.Provider>
      </SafeAreaProvider>
    </Provider>
  );
}
