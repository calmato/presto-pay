import React from "react";

/* eslint-disable @typescript-eslint/no-explicit-any */
export interface Form {
  value: any;
  onChangeText: (value: any) => void;
}
/* eslint-enable @typescript-eslint/no-explicit-any */

export default function useControlledComponent<T>(initialValue: T) {
  const [value, setValue] = React.useState(initialValue);

  function onChangeText(newValue: T) {
    setValue(newValue);
  }

  return {
    value,
    onChangeText,
  };
}
