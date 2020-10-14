import { StyleSheet } from "react-native";

import { Button, TextField } from "~/components/atoms";
import { COLOR } from "~/constants/theme";
import { Form, useControlledComponent } from "~/lib/hooks";

const styles = StyleSheet.create({
  button: {
    margin: 10,
  },
});

interface Props {
  formData: {
    email: Form;
    password: Form;
  };
  actions: {
    handleSignIn: () => void;
  };
}

export default function SignInForm(props: Props) {
  const { email, password } = props.formData;
  const { handleSignIn } = props.actions;

  return (
    <>
      <TextField placeholder="Email" value={email.value} onChangeText={email.onChangeText} />
      <TextField placeholder="Password" value={password.value} onChangeText={password.onChangeText} />
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

SignInForm.defaultProps = {
  formData: {
    email: useControlledComponent(""),
    password: useControlledComponent(""),
  },
};
