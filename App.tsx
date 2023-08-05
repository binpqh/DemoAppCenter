import React, {useEffect, useState} from 'react';
import {Colors, Header} from 'react-native/Libraries/NewAppScreen';
import {NativeModules} from 'react-native';
import CustomButton from './components/Button';
import RNFS from 'react-native-fs';
import {
  SafeAreaView,
  ScrollView,
  StatusBar,
  Text,
  useColorScheme,
  View,
} from 'react-native';
const urlAPK =
  'https://drive.google.com/file/d/13mfbsxqDsquYD_PQfrQMGRcffNSe0L_N/view?usp=sharing';
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
  const handleInstallApk = async () => {
    const apkUrl =
      'https://drive.google.com/file/d/13mfbsxqDsquYD_PQfrQMGRcffNSe0L_N';
    const downloadDest = `${RNFS.DownloadDirectoryPath}/app-debug1.apk`;
    await RNFS.downloadFile({
      fromUrl:
        'https://drive.google.com/file/d/15zQac2pakJkNeCR7FQA-eNdjRz7CgPlm/view?usp=sharing',
      toFile: downloadDest,
      progress: data => {
        console.log(data.contentLength);
        const progress = data.bytesWritten / data.contentLength;
        console.log(`Download progress: ${progress.toFixed(2)}`);
      },
    }).promise.then(res => {
      if (res.statusCode === 200) {
        console.log(res.bytesWritten);
        DeviceModule.install(downloadDest);
      } else {
        console.log('Download failed!');
      }
    });
  };
  const handleGetStatus = () => {
    setResult('Get Status Sim Dispener' + DeviceModule.getStatus(0));
  };
  const handleMoveCardToCheckBox = () => {
    DeviceModule.moveCardToBoxCheck(0);
  };
  const handleEjectOneCard = () => {
    setResult(DeviceModule.ejectOneCard());
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
          <CustomButton
            onPress={handleInstallApk}
            title={'Install APK'}
            color={'Red'}
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
