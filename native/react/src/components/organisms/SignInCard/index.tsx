import { useNavigation } from "@react-navigation/native";
import React from "react";
import { StyleSheet, TextStyle } from "react-native";
import { Card } from "react-native-elements";

import { Link } from "~/components/atoms";
import { SignInForm, SignInSocialButton } from "~/components/molecules";
import { SIGN_UP, PASSWORD_RESET } from "~/constants/path";
import { COLOR } from "~/constants/theme";
import { UiContext } from "~/contexts";
import { Status } from "~/contexts/ui";
import { useControlledComponent } from "~/lib/hooks";

interface Props {
  actions: {
    signInWithPassword: (email: string, password: string) => Promise<void>;
    showAuthUser: () => Promise<void>;
  };
}

interface Style {
  link: TextStyle;
}

const styles = StyleSheet.create<Style>({
  link: {
    padding: 20,
    textAlign: "center",
  },
});

export default function SignInCard(props: Props) {
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

  return (
    <Card>
      <SignInForm formData={{ email, password }} actions={{ handleSignIn: handleSignInWithPassword }} />
      <Link
        text="パスワードを忘れた方"
        style={styles.link}
        onPress={() => navigation.navigate(PASSWORD_RESET)}
      />
      <Card.Divider />
      <SignInSocialButton
        title="Googleでサインイン"
        titleColor={COLOR.DARK}
        icon="logo-google"
        onPress={() => console.log("click", "google")}
      />
      <SignInSocialButton
        title="Twitterでサインイン"
        titleColor={COLOR.DARK}
        icon="logo-twitter"
        onPress={() => console.log("click", "twitter")}
      />
      <SignInSocialButton
        title="Facebookでサインイン"
        titleColor={COLOR.DARK}
        icon="logo-facebook"
        onPress={() => console.log("click", "facebook")}
      />
      <Link
        text="アカウントをお持ちでない方"
        style={styles.link}
        onPress={() => navigation.navigate(SIGN_UP)}
      />
    </Card>
  );
}
