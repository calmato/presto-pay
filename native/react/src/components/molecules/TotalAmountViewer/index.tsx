import React from "react";
import { StyleSheet, View, Text } from "react-native";
import { TextInput } from "react-native-gesture-handler";

import Icon from "~/components/atoms/Icon";
import { COLOR } from "~/constants/theme";

const styles = StyleSheet.create({
  box: {
    justifyContent: "center",
    alignItems: "center",
    backgroundColor: "rgba(218,229,254,0.5)",
    alignSelf: "stretch",
    margin: 10,
    padding: 10,
    borderRadius: 15,
  },
  title: {
    color: COLOR.PRIMARY,
    fontWeight: "bold",
    marginBottom: 5,
  },
  price: {
    color: COLOR.PRIMARY,
    fontWeight: "bold",
    fontSize: 32,
  },
});

interface Props {
  value: number;
}

// TODO: Propsの型定義
// valueの正負判定は本来はbooleanでやるので要修正
const TotalAmountViewer = function TotalAmountViewer(props: Props) {
  const { value } = props;

  if (value < 0) {
    return (
      <View style={styles.box}>
        <Text style={styles.title}>貸付合計金額</Text>
        <Text style={{ ...styles.price, color: COLOR.ERROR }}>&nbsp;&yen;&nbsp;{value.toLocaleString()}</Text>
      </View>
    );
  } else {
    return (
      <View style={styles.box}>
        <Text style={styles.title}>貸付合計金額</Text>
        <Text style={styles.price}>&nbsp;&yen;&nbsp;{value.toLocaleString()}</Text>
      </View>
    );
  }
};

TotalAmountViewer.defaultProps = {
  value: 12000,
};

export default TotalAmountViewer;
