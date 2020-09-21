import React from "react";
import { StyleSheet, View, Text, TouchableOpacity } from "react-native";

import { Profile, UserInfoList } from "~/components/organisms";
import { Context, Status } from "~/contexts/ui";

const styles = StyleSheet.create({
  container: {
    flex: 1,
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

export default function Groups() {
  const { setApplicationState } = React.useContext(Context);

  return (
    <View style={styles.container}>
      <Profile />
      <UserInfoList items={DATA} />
      <Text>UserInfo</Text>
      <TouchableOpacity onPress={() => setApplicationState(Status.UN_AUTHORIZED)}>
        <Text>SignOut</Text>
      </TouchableOpacity>
    </View>
  );
}
