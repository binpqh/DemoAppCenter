import React from 'react';
import {TouchableOpacity, Text, ColorValue} from 'react-native';

interface IButton {
  onPress: any;
  title: string;
  color: ColorValue;
}
const CustomButton = (props: IButton) => {
  return (
    <TouchableOpacity
      onPress={props.onPress}
      style={{
        backgroundColor: props.color,
        padding: 10,
        borderRadius: 5,
      }}>
      <Text>{props.title}</Text>
    </TouchableOpacity>
  );
};

export default CustomButton;
