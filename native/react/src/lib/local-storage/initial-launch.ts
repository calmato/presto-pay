import AsyncStorage from "@react-native-community/async-storage";

import { IS_OPENED_KEY } from "./key";

export async function isInitialLaunch() {
  const opened = await AsyncStorage.getItem(IS_OPENED_KEY);
  return !!opened;
}
