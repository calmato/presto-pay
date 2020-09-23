import React from "react";
import { StyleSheet, View } from "react-native";
import { TextInput } from "react-native-gesture-handler";

import Icon from "~/components/atoms/Icon";
import { COLOR } from "~/constants/theme";

const styles = StyleSheet.create({
  form: {
    margin: 10,
    borderRadius: 15,
    flexDirection: "row",
    alignItems: "center",
    backgroundColor: COLOR.MAIN_LIGHT,
    height: 30,
  },
  icon: {
    paddingLeft: 10,
    fontSize: 16,
    flex: 0.05,
  },
  textInput: {
    flex: 0.95,
    marginRight: 10,
    backgroundColor: COLOR.MAIN_LIGHT,
  },
});

// TODO: Propsの型定義
// TODO: Inputされた文字列をどう使うか
const SearchBar = function SearchBar() {
  const [value, onChangeText] = React.useState("Useless Placeholder");

  return (
    <View style={styles.form}>
      <Icon name="md-search" style={styles.icon} />
      <TextInput
        style={styles.textInput}
        placeholder="Search"
        onChangeText={(text) => onChangeText(text)}
        value={value}
      />
    </View>
  );
};

export default SearchBar;
