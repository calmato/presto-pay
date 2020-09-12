import { useNavigation } from "@react-navigation/native";
import React from "react";
import { StyleSheet, View, Text, TouchableOpacity } from "react-native";
import { Card } from "react-native-elements";

import { Button, TextField } from "~/components/atoms";
import { SIGN_UP } from "~/constants/path";
import { COLOR } from "~/constants/theme";
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
    fontWeight: "bold",
    color: COLOR.MAIN,
  },
  button: {
    padding: 10,
    color: COLOR.PRIMARY,
  },
  link: {
    padding: 10,
    textAlign: "center",
  },
});

export default function SignIn() {
  const { navigate } = useNavigation();
  const { setApplicationState } = React.useContext(Context);

  // TODO: デザイン, コードリファクタ
  return (
    <View style={styles.container}>
      <Text style={styles.title}>Presto Pay</Text>
      <Card>
        <TextField label="E-Mail" />
        <TextField label="Password" secureTextEntry={true} />
        <Button onPress={() => setApplicationState(Status.AUTHORIZED)} title="ログイン" style={styles.button} />
        <Text style={styles.link}>パスワードを忘れた方</Text>
        <Card.Divider />
        <Button onPress={() => console.log("click", "google")} title="Googleでサインイン" style={styles.button} />
        <Button onPress={() => console.log("click", "twitter")} title="Twitterでサインイン" style={styles.button} />
        <Button onPress={() => console.log("click", "facebook")} title="Facebookでサインイン" style={styles.button} />
        <TouchableOpacity onPress={() => navigate(SIGN_UP)}>
          <Text style={styles.link}>アカウントをお持ちでない方</Text>
        </TouchableOpacity>
      </Card>
    </View>
  );
}
