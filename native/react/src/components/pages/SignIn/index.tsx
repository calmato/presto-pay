import { useNavigation } from "@react-navigation/native";
import React from "react";
import { StyleSheet, View, Text, TouchableOpacity } from "react-native";
import { Card } from "react-native-elements";

import { Button, Icon } from "~/components/atoms";
import { SignInForm } from "~/components/molecules";
import { SIGN_UP, PASSWORD_RESET } from "~/constants/path";
import { COLOR } from "~/constants/theme";
import { UiContext } from "~/contexts";
import { Status } from "~/contexts/ui";
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
  icon: {
    fontSize: 22,
    marginRight: 10,
  },
  button: {
    margin: 10,
  },
  link: {
    padding: 10,
    textAlign: "center",
    textDecorationLine: "underline",
    color: COLOR.PRIMARY,
  },
});

const icon = (name: string): JSX.Element => {
  return <Icon name={name} style={styles.icon} />;
};

interface Props {
  actions: {
    signInWithPassword: (email: string, password: string) => Promise<void>;
    showAuthUser: () => Promise<void>;
  };
}

export default function SignIn(props: Props) {
  const navigation = useNavigation();
  const { setApplicationState } = React.useContext(UiContext);
  const email = useControlledComponent("");
  const password = useControlledComponent("");
  const { signInWithPassword, showAuthUser } = props.actions;

  const handleSignInWithPassword = React.useCallback(async () => {
    await signInWithPassword(email.value, password.value)
      .then(async () => {
        await showAuthUser();
        setApplicationState(Status.AUTHORIZED);
      })
      .catch((err: Error) => console.log("failure:", err)); // TODO: エラー処理
  }, [email.value, password.value, setApplicationState, signInWithPassword]);

  // TODO: コンポーネント分割, アイコン追加
  return (
    <View style={styles.container}>
      <Text style={styles.title}>Presto Pay</Text>
      <Card>
        <SignInForm formData={{ email, password }} actions={{ handleSignIn: handleSignInWithPassword }} />
        <TouchableOpacity onPress={() => navigation.navigate(PASSWORD_RESET)}>
          <Text style={styles.link}>パスワードを忘れた方</Text>
        </TouchableOpacity>
        <Card.Divider />
        <Button
          icon={icon("logo-google")}
          onPress={() => console.log("click", "google")}
          title="Googleでサインイン"
          titleColor={COLOR.DARK}
          style={styles.button}
          type="outline"
        />
        <Button
          icon={icon("logo-twitter")}
          onPress={() => console.log("click", "twitter")}
          title="Twitterでサインイン"
          titleColor={COLOR.DARK}
          style={styles.button}
          type="outline"
        />
        <Button
          icon={icon("logo-facebook")}
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
