import React from "react";
import { Text, View, StyleSheet, ViewStyle } from 'react-native';

interface Props {
  uri?: string;
  style?: ViewStyle;
}

const styles = StyleSheet.create({
  name: {
  }
})

export default function ProfileUserName(props: Props) {
  // const { onPress, title, style, disabled, disabledStyle, icon } = props;

  return (
    <View>
      <Text>
        浜田広大
      </Text>
    </View>
  );
}

