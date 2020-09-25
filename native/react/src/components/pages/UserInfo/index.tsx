import { useNavigation } from "@react-navigation/native";
import React from "react";
import { StyleSheet, View, Text, TouchableOpacity } from "react-native";

import { Profile, UserInfoList } from "~/components/organisms";
import { USER_EDIT, USER_PASSWORD_EDIT, NOTIFICATION_INFO } from "~/constants/path";
import { Context, Status } from "~/contexts/ui";

const styles = StyleSheet.create({
  container: {
    flex: 1,
  },
  title: {
    fontSize: 36,
    fontWeight: "bold",
  },
});

const DATA = [
  {
    id: "bd7acbea-c1b1-46c2-aed5-3ad53abb28ba",
    title: "通知の設定",
    icon: "md-notifications",
  },
  {
    id: "3ac68afc-c605-48d3-a4f8-fbd91aa97f63",
    title: "パスワードの変更",
    icon: "md-lock",
  },
  {
    id: "58694a0f-3da1-471f-bd96-145571e29d72",
    title: "Prest Payについて",
    icon: "md-phone-portrait",
  },
  {
    id: "58694a0f-3da1-471f-bd96-145571e29d81",
    title: "ヘルプ",
    icon: "md-information-circle",
  },
];

interface Props {
  actions: {
    signOut: () => Promise<void>;
  };
}

export default function UserInfo(props: Props) {
  const navigation = useNavigation();
  const { setApplicationState } = React.useContext(Context);
  const { signOut } = props.actions;

  const handleSignOut = React.useCallback(() => {
    signOut().finally(() => setApplicationState(Status.UN_AUTHORIZED));
  }, [signOut]);

  return (
    <View style={styles.container}>
      <Profile />
      <UserInfoList items={DATA} />
      <Text>UserInfo</Text>
      <Text style={styles.title}>UserInfo</Text>
      <TouchableOpacity onPress={() => navigation.navigate(USER_EDIT)}>
        <Text>UserEdit</Text>
      </TouchableOpacity>
      <TouchableOpacity onPress={() => navigation.navigate(USER_PASSWORD_EDIT)}>
        <Text>UserPasswordEdit</Text>
      </TouchableOpacity>
      <TouchableOpacity onPress={() => navigation.navigate(NOTIFICATION_INFO)}>
        <Text>NotificationInfo</Text>
      </TouchableOpacity>
      <TouchableOpacity onPress={handleSignOut}>
        <Text>SignOut</Text>
      </TouchableOpacity>
    </View>
  );
}
