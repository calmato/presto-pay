import React from "react";
import { StyleSheet, View } from "react-native";

import SearchBar from "~/components/molecules/SearchBar";
import TotalAmountViewer from "~/components/molecules/TotalAmountViewer";
import HomeSwipeList from "~/components/organisms/HomeSwipeList";

const styles = StyleSheet.create({
  container: {
    flex: 1,
    alignItems: "center",
  },
});

const DATA = [
  {
    id: "1",
    title: "test1",
    img: "https://simpleicon.com/wp-content/uploads/account.png",
  },
  {
    id: "2",
    title: "test2",
    img: "https://simpleicon.com/wp-content/uploads/account.png",
  },
  {
    id: "3",
    title: "test3",
    img: "https://simpleicon.com/wp-content/uploads/account.png",
  },
];

export default function Home() {
  return (
    <View style={styles.container}>
      <SearchBar />
      <TotalAmountViewer />
      <HomeSwipeList items={DATA} />
    </View>
  );
}
