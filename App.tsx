import React, {useEffect, useState} from 'react';
import {Colors, Header} from 'react-native/Libraries/NewAppScreen';
import {NativeModules} from 'react-native';
import CustomButton from './components/Button';
import {
  SafeAreaView,
  ScrollView,
  StatusBar,
  Text,
  useColorScheme,
  View,
} from 'react-native';
const {DeviceModule,Despenser,Printer,DevicePeripherals} = NativeModules;
const App = () => {
  const [result, setResult] = useState<any>();
  useEffect(() => {
  }, []);
const fakeData = {
  kioskId: "yourKioskId",
  receiptNumber: "123456",
  language: "en",
  hotline: "03821808722",
  email: "cskh@myLocal.vn.com",
  total: {
    quantity: "12",
    price: "1.000.000",
  },
  commodityList: [
    { description: "Sim 1", quantity: "5", price: "500,000" },
    { description: "Sim 2", quantity: "7", price: "300,000" },
  ],
};

//testing for printer
// const handleTest = async ()=> {
//  const statusprint = await Printer.PrinterBill(
//     fakeData.kioskId,
//     fakeData.receiptNumber,
//     fakeData.language,
//     fakeData.hotline,
//     fakeData.email,
//     fakeData.total,
//     fakeData.commodityList
//   );
//   console.log("status after printer : ",statusprint);
//   const status = await Printer.StatusOutOfPaper();
//   console.log("status warning : ",status);
// };


  // const handleGetStatus = () => {
  //   setResult('Get Status Sim Dispener' + DeviceModule.getStatus(1));
  //   DeviceModule.getStatusBoxCard(1);
  // };
  // const handleMoveCardToCheckBox = () => {
  //   DeviceModule.moveCardToBoxCheck(0);
  // };
  // const handleReadSerialSim = () => {
  //   DeviceModule.readSerialSim(0, (res: any, err: any) => {
  //     if (res != null) {
  //       console.log(res);
  //     } else {
  //       console.log(err);
  //     }
  //   });
  // };
  // const handleEjectOneCard = () => {
  //   DeviceModule.Printest();
  // };
  // const handleRebootDevice = () => {
  //   // DeviceModule.rebootDevice();
  // };
  // const handleMoveCardFront = () => {
  //   DeviceModule.moveToFront(0);
  // };
  // const handleMoveOneCard = () => {
  //   DeviceModule.moveOneCard(1);
  // };
  // const  handleInit = () => {
  //  var test = DeviceModule.InitDispenser(1,"/dev/ttyS0");
    
  //  console.log(test);
  // };
  // const handleMovecard = () => {
  //   DeviceModule.moveCardToRead(1);
  // };
  
  // const handTestBtn = () => {
  //   DeviceModule.getUSBDeviceHasDriver(0);
  // };
  // const handlePrintReceipt = () => {
  //   setResult(
  //     DeviceModule.printReceipt(
  //       kioskId,
  //       deviceId,
  //       vendorId,
  //       productId,
  //       receiptNumber,
  //       language,
  //     ),
  //   );
  //   console.log(result);
  // };
  // const handleGetTemperature = () => {
  //   console.log(DeviceModule.TemperatureConnect(0));
  // };
// //testing for despenser
//   const handleTest = async ()=> {
//       //const status = await Despenser.initDespenser(1,"/dev/ttyS0");
//       const status = await Despenser.outCard(1);
//       console.log("status SIM : ",status);
//   };
const handleTest = async ()=> {
  const tem = await DevicePeripherals.getTemperature();
  console.log("nhiet do : ",tem);
}
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
            onPress={handleTest}
            title={'Testing Function'}
            color={'Red'}
          />

        </View>
      </ScrollView>
    </SafeAreaView>
  );
};

export default App;
