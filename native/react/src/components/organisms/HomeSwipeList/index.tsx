import React from "react";
import { StyleSheet, View, Text } from "react-native";
import { SwipeListView } from "react-native-swipe-list-view";

import HomeSwipeListHiddenItem from "~/components/molecules/HomeSwipeListHiddenItem";
import HomeSwipeListItem from "~/components/molecules/HomeSwipeListItem";

const styles = StyleSheet.create({
  list: {
    alignSelf: "stretch",
  },
});

interface ListData {
  id: string;
  title: string;
  img: string;
}

interface Props {
  items: ListData[];
}

const HomeSwipeList = function HomeSwipeList(props: Props) {
  const { items } = props;

  const renderItem = ({ item }) => <HomeSwipeListItem title={item.title} img={item.img} />;
  const renderHiddenItem = (_data, _rowMao) => <HomeSwipeListHiddenItem />;

  return (
    <SwipeListView
      style={styles.list}
      useFlatList={true}
      data={items}
      renderItem={renderItem}
      renderHiddenItem={renderHiddenItem}
      keyExtractor={(item) => item.id}
      rightOpenValue={-140}
    />
  );
};

export default HomeSwipeList;
