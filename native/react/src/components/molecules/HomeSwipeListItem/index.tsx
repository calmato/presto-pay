import React from "react";
import { StyleSheet, View, Text, TouchableHighlight, Image } from "react-native";
import { SwipeListView } from "react-native-swipe-list-view";

import { COLOR } from "~/constants/theme";

const styles = StyleSheet.create({
  list: {
    flexDirection: "row",
    height: 50,
    alignItems: "center",
    backgroundColor: COLOR.MAIN,
  },
  img: {
    width: 44,
    height: 44,
    borderRadius: 22,
    marginLeft: 16,
  },
  title: {
    fontSize: 18,
    marginLeft: 16,
    flex: 0.9,
  },
});

interface Props {
  title: string;
  img: string;
}

const HomeSwipeListItem = function HomeSwipeListItem(props: Props) {
  const { title, img } = props;

  return (
    <TouchableHighlight>
      <View style={styles.list}>
        <Image style={styles.img} source={{ uri: img }} />
        <Text style={styles.title}>{title}</Text>
      </View>
    </TouchableHighlight>
  );
};

HomeSwipeListItem.defaultProps = {
  title: "item",
  img: "https://reactnative.dev/img/tiny_logo.png",
};

export default HomeSwipeListItem;
