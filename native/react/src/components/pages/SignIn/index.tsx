import { useNavigation } from "@react-navigation/native";
import React from "react";
import { StyleSheet, View, Text, TouchableOpacity } from "react-native";
import { Card } from "react-native-elements";

import { Button, TextField } from "~/components/atoms";
import { SIGN_UP, PASSWORD_RESET } from "~/constants/path";
import { COLOR } from "~/constants/theme";
import { UiContext } from "~/contexts";
import { Status } from "~/contexts/ui";
import { Auth } from "~/domain/models";
import { useControlledComponent } from "~/lib/hooks";

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
  },
  link: {
    padding: 10,
    textAlign: "center",
    textDecorationLine: "underline",
    color: COLOR.PRIMARY,
  },
});

interface Props {
  actions: {
    signInWithPassword: (email: string, password: string) => Promise<Auth.AuthValues>;
  };
}

export default function SignIn(props: Props) {
  const navigation = useNavigation();
  const { setApplicationState } = React.useContext(UiContext);
  const email = useControlledComponent("");
  const password = useControlledComponent("");
  const { signInWithPassword } = props.actions;

  const handleSignInWithPassword = React.useCallback(async () => {
    await signInWithPassword(email.value, password.value)
      .then(() => setApplicationState(Status.AUTHORIZED))
      .catch((err: Error) => console.log("failure:", err)); // TODO: エラー処理
  }, [email.value, password.value, setApplicationState, signInWithPassword]);

  // TODO: コンポーネント分割, アイコン追加
  return (
    <View style={styles.container}>
      <Text style={styles.title}>Presto Pay</Text>
      <Card>
        <TextField placeholder="Email" value={email.value} onChangeText={email.onChangeText} />
        <TextField
          placeholder="Password"
          secureTextEntry={true}
          value={password.value}
          onChangeText={password.onChangeText}
        />
        <Button
          onPress={handleSignInWithPassword}
          title="ログイン"
          titleColor={COLOR.MAIN}
          style={styles.button}
          backgroundColor={COLOR.PRIMARY}
        />
        <TouchableOpacity onPress={() => navigation.navigate(PASSWORD_RESET)}>
          <Text style={styles.link}>パスワードを忘れた方</Text>
        </TouchableOpacity>
        <Card.Divider />
        <Button
          onPress={() => console.log("click", "google")}
          title="Googleでサインイン"
          titleColor={COLOR.DARK}
          style={styles.button}
          type="outline"
        />
        <Button
          onPress={() => console.log("click", "twitter")}
          title="Twitterでサインイン"
          titleColor={COLOR.DARK}
          style={styles.button}
          type="outline"
        />
        <Button
          onPress={() => console.log("click", "facebook")}
          title="Facebookでサインイン"
          titleColor={COLOR.DARK}
          style={styles.button}
          type="outline"
        />
        <TouchableOpacity onPress={() => navigation.navigate(SIGN_UP)}>
          <Text style={styles.link}>アカウントをお持ちでない方</Text>
        </TouchableOpacity>
      </Card>
    </View>
  );
}
