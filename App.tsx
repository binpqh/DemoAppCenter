import React, {useEffect, useState} from 'react';
import {
  SafeAreaView,
  ScrollView,
  StatusBar,
  Text,
  useColorScheme,
  View,
} from 'react-native';

import {Colors, Header} from 'react-native/Libraries/NewAppScreen';
import {NativeModules} from 'react-native';
import CustomButton from './components/Button';

const {DeviceModule} = NativeModules;
const commodityList: Array<{
  // description: string;
  quantity: string;
  price: string;
}> = [
  {
    // description: 'Sim description',
    quantity: '50',
    price: '500,000VND',
  },
];
const total: {price: string; quantity: string; serials: string} = {
  price: '250,000VND',
  quantity: '5',
  serials: '1232131231233123213123', // khong su dung (de giai doan 2)
};
const kioskId = '1';
const deviceId = 1005;
const receiptNumber = 'unknown';
const vendorId = 4070;
const productId = 33054;
const language = 'vi';
const App = () => {
  const [result, setResult] = useState<any>();
  useEffect(() => {
    DeviceModule.initSimDispenser();
  }, []);
  const handleGetStatus = () => {
    setResult('Get Status Sim Dispener' + DeviceModule.getStatus(0));
  };
  const handleMoveCardToCheckBox = () => {
    DeviceModule.moveCardToBoxCheck(0);
  };
  const handleEjectOneCard = () => {
    setResult(DeviceModule.ejectOneCard());
  };
  const handleInitSimDispenser = () => {
    DeviceModule.initSimDispenser();
  };
  const handleRebootDevice = () => {
    // DeviceModule.rebootDevice();
  };
  const handleMoveCardFront = () => {
    DeviceModule.moveToFront(0);
  };
  const handlePrintReceipt = () => {
    setResult(
      DeviceModule.printReceipt(
        kioskId,
        deviceId,
        vendorId,
        productId,
        receiptNumber,
        language,
      ),
    );
    console.log(result);
  };
  const handleGetTemperature = () => {
    setResult(DeviceModule.getTemperature());
  };

  const isDarkMode = useColorScheme() === 'dark';

  const backgroundStyle = {
    backgroundColor: isDarkMode ? Colors.darker : Colors.lighter,
  };

  return (
    <SafeAreaView style={backgroundStyle}>
      <StatusBar
        barStyle={isDarkMode ? 'light-content' : 'dark-content'}
        backgroundColor={backgroundStyle.backgroundColor}
      />
      <ScrollView
        contentInsetAdjustmentBehavior="automatic"
        style={backgroundStyle}>
        <Header />
        <View
          style={{
            backgroundColor: isDarkMode ? Colors.black : Colors.white,
          }}>
          <CustomButton
            onPress={handleGetStatus}
            title={'Get Status'}
            color={'Blue'}
          />
          <CustomButton
            onPress={handlePrintReceipt}
            title={'Print Receipt'}
            color={'Blue'}
          />

          <CustomButton
            onPress={handleMoveCardToCheckBox}
            title={'Move Card'}
            color={'Blue'}
          />
          <CustomButton
            onPress={handleEjectOneCard}
            title={'Eject Card'}
            color={'Blue'}
          />
          <CustomButton
            onPress={handleMoveCardFront}
            title={'Out Card'}
            color={'Blue'}
          />
          <CustomButton
            onPress={handleGetTemperature}
            title={'Get Temperature'}
            color={'Blue'}
          />
          <Text>{result}</Text>
          {/* <CustomButton
            onPress={undefined}
            title={'Read Series Sim'}
            color={'Blue'}
          /> */}
          {/* <LearnMoreLinks /> */}
        </View>
      </ScrollView>
    </SafeAreaView>
  );
};

export default App;
