import { useNavigation } from "@react-navigation/native";
import React from "react";
import { StyleSheet, View, Text, TouchableOpacity } from "react-native";
import { Card } from 'react-native-elements'

import { SIGN_UP } from "~/constants/path";
import { COLOR } from '~/constants/theme';
import { Context, Status } from "~/contexts/ui";

const styles = StyleSheet.create({
  container: {
    flex: 1,
    justifyContent: "center",
    backgroundColor: COLOR.PRIMARY,
  },
  title: {
    textAlign: "center",
    fontSize: 36,
    fontWeight: 'bold',
    color: COLOR.MAIN,
  },
});

export default function SignIn() {
  const { navigate } = useNavigation();
  const { setApplicationState } = React.useContext(Context);

  return (
    <View style={styles.container}>
      <Text style={styles.title}>Presto Pay</Text>
      <Card>
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
      </Card>
    </View>
  );
}
