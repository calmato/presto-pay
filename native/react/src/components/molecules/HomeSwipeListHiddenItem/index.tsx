import React from "react";
import { StyleSheet, Text, TouchableOpacity, View } from "react-native";

import { COLOR } from "~/constants/theme";

const styles = StyleSheet.create({
  rowBack: {
    justifyContent: "flex-end",
    height: 50,
    flexDirection: "row",
  },
  leftItem: {
    justifyContent: "center",
    alignItems: "center",
    backgroundColor: COLOR.MAIN_LIGHT,
    width: 70,
  },
  rightItem: {
    alignItems: "center",
    justifyContent: "center",
    backgroundColor: COLOR.ERROR,
    width: 70,
  },
});

const HomeSwipeListHiddenItem = function HomeSwipeListHiddenItem() {
  return (
    <View style={styles.rowBack}>
      <TouchableOpacity style={styles.leftItem} onPress={() => console.log("hidden click")}>
        <Text>隠す</Text>
      </TouchableOpacity>
      <TouchableOpacity style={styles.rightItem} onPress={() => console.log("delete click")}>
        <Text>削除</Text>
      </TouchableOpacity>
    </View>
  );
};

export default HomeSwipeListHiddenItem;
