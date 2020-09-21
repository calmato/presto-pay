import React from "react";
import { Text, ViewStyle, View, StyleSheet } from "react-native";

import Icon from "~/components/atoms/Icon";
import { COLOR } from "~/constants/theme";

interface Props {
  title: string;
  icon: string;
  style?: ViewStyle;
}

const styles = StyleSheet.create({
  list: {
    flexDirection: "row",
    height: 40,
    alignItems: "center",
    backgroundColor: COLOR.MAIN,
    borderBottomColor: COLOR.MAIN_LIGHT,
    borderBottomWidth: 1,
  },
  icon: {
    paddingLeft: 20,
    flex: 0.1,
    fontSize: 24,
  },
  title: {
    flex: 0.9,
    fontSize: 16,
  },
});

const UserInfoListItem = function UserInfoListItem(props: Props) {
  const { title, icon } = props;

  return (
    <View style={styles.list}>
      <Icon name={icon} style={styles.icon} />
      <Text style={styles.title}>{title}</Text>
    </View>
  );
};

UserInfoListItem.defaultProps = {
  title: "test",
};

export default UserInfoListItem;
