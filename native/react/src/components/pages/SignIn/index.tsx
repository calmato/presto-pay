import React from "react";
import { StyleSheet, View, Text, ViewStyle, TextStyle } from "react-native";

import { SignInCard } from "~/components/organisms";
import { COLOR } from "~/constants/theme";

interface Props {
  actions: {
    signInWithPassword: (email: string, password: string) => Promise<void>;
    showAuthUser: () => Promise<void>;
  };
}

interface Style {
  container: ViewStyle;
  title: TextStyle;
}

const styles = StyleSheet.create<Style>({
  container: {
    flex: 1,
    justifyContent: "center",
    backgroundColor: COLOR.PRIMARY,
  },
  title: {
    textAlign: "center",
    fontSize: 36,
    fontWeight: "bold",
    color: COLOR.MAIN,
  },
});

export default function SignIn(props: Props) {
  const { signInWithPassword, showAuthUser } = props.actions;

  return (
    <View style={styles.container}>
      <Text style={styles.title}>Presto Pay</Text>
      <SignInCard actions={{ signInWithPassword, showAuthUser }} />
    </View>
  );
}
