import React from "react";
import { StyleSheet, ViewStyle } from "react-native";

import { Button, TextField } from "~/components/atoms";
import { COLOR } from "~/constants/theme";
import { Form } from "~/lib/hooks";

interface Props {
  formData: {
    email: Form;
    password: Form;
  };
  actions: {
    handleSignIn: () => void;
  };
}

interface Style {
  button: ViewStyle;
}

const styles = StyleSheet.create<Style>({
  button: {
    margin: 10,
  },
});

export default function SignInForm(props: Props) {
  const { email, password } = props.formData;
  const { handleSignIn } = props.actions;

  return (
    <>
      <TextField placeholder="Email" value={email.value} onChangeText={email.onChangeText} />
      <TextField
        placeholder="Password"
        value={password.value}
        onChangeText={password.onChangeText}
        secureTextEntry={true}
      />
      <Button
        onPress={handleSignIn}
        title="ログイン"
        titleColor={COLOR.MAIN}
        style={styles.button}
        backgroundColor={COLOR.PRIMARY}
      />
    </>
  );
}
