import React from "react";
import { SafeAreaProvider } from "react-native-safe-area-context";
import { Provider } from "react-redux";

import * as UiContext from "~/contexts/ui";
import Routes from "~/routes";
import store from "~/store";

export default function App() {
  const [applicationState, setApplicationState] = React.useState(UiContext.createApplicationInitialState());
  const [error, setError] = React.useState(UiContext.createErrorInitialState()); // TODO: 後でエラー処理の実装に使う
  const [snackbar, setSnackbar] = React.useState(UiContext.createSnackbarInitialState()); // TODO: 後で通知の実装に使う

  return (
    <Provider store={store}>
      <SafeAreaProvider>
        <UiContext.Context.Provider
          value={{ applicationState, setApplicationState, error, setError, snackbar, setSnackbar }}
        >
          <Routes />
        </UiContext.Context.Provider>
      </SafeAreaProvider>
    </Provider>
  );
}
