import React from "react";
import { FlatList } from "react-native";

import UserInfoListItem from "~/components/molecules/UserInfoListItem";

// TODO: 型定義を別に切り出す
interface ListData {
  id: string;
  title: string;
  icon: string;
}

interface Props {
  items: ListData[];
}

const Item = ({ title, icon }) => <UserInfoListItem title={title} icon={icon} />;

const UserInfoList = function UserInfoList(props: Props) {
  const { items } = props;

  const renderItem = ({ item }) => <Item title={item.title} icon={item.icon} />;

  return <FlatList data={items} renderItem={renderItem} keyExtractor={(item) => item.id} />;
};

export default UserInfoList;
