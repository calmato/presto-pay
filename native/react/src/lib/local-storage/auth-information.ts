import AsyncStorage from "@react-native-community/async-storage";

import { AUTH_KEY } from "./key";

import { AuthInformation } from "~/contexts/auth";

export async function save(authInformation: AuthInformation) {
  await AsyncStorage.setItem(AUTH_KEY, JSON.stringify(authInformation));
}

export async function retrieve() {
  const serialized = await AsyncStorage.getItem(AUTH_KEY);
  if (!serialized) {
    return null;
  }

  return JSON.parse(serialized);
}

export async function clear() {
  await AsyncStorage.removeItem(AUTH_KEY);
}
