import { useNavigation } from "@react-navigation/native";
import React from "react";
import { StyleSheet, View, Text, TouchableOpacity } from "react-native";

import { SIGN_UP } from "~/constants/path";
import { COLOR } from '~/constants/theme';
import { Context, Status } from "~/contexts/ui";

const styles = StyleSheet.create({
  container: {
    flex: 1,
    justifyContent: "center",
    alignItems: "center",
    backgroundColor: COLOR.PRIMARY,
  },
  title: {
    fontSize: 36,
    fontWeight: 'bold',
    paddingBottom: 30,
    color: COLOR.MAIN,
  },
});

export default function SignIn() {
  const { navigate } = useNavigation();
  const { setApplicationState } = React.useContext(Context);

  return (
    <View style={styles.container}>
      <Text style={styles.title}>Presto Pay</Text>
      <Text>Googleでサインイン</Text>
      <Text>Twitterでサインイン</Text>
      <Text>Facebookでサインイン</Text>
      <TouchableOpacity onPress={() => setApplicationState(Status.AUTHORIZED)}>
        <Text>ログイン</Text>
      </TouchableOpacity>
      <TouchableOpacity onPress={() => navigate(SIGN_UP)}>
        <Text>新規登録する</Text>
      </TouchableOpacity>
      <Text>パスワードを忘れた</Text>
    </View>
  );
}
