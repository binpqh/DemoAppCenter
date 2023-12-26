package com.demosdk.Test;

import android.content.Context;
import android.content.Intent;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.net.Uri;
import android.util.Log;
import androidx.core.content.FileProvider;
import asim.sdk.locker.SDKLocker;
import com.demosdk.DefineState;
import com.demosdk.Helper;
import com.demosdk.Services.PrinterService;
import com.demosdk.UPSModule.SDKUPS;
import asim.sdk.common.Utils;
import asim.sdk.locker.CustomProber;
import asim.sdk.locker.DeviceInfo;
import asim.sdk.printer.POSCustomed;
import asim.sdk.printer.SDKPrints;
import asim.sdk.sdksimdispenser.SimdispenserMain;
import asim.sdk.tempandhum.SDKTemperatureAndHumidity;
import asim.sdk.tempandhum.TempHumiData;
import asim.sdk.ups.UPSModel;
import com.demosdk.UPSModule.SerialPortExample;
import com.facebook.react.bridge.*;
import com.hoho.android.usbserial.driver.UsbSerialDriver;
import com.hoho.android.usbserial.driver.UsbSerialProber;
import com.lvrenyang.io.USBPrinting;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class DeviceModule extends ReactContextBaseJavaModule {
    private static final String timetoRecyceCard = "60000";
    private static final String baurate = "115200";
    private static final String SIM_DISPENSER_SDK_CHANNEL = "SDKSimDispenser";
    private static final String EVENT_STATUS_CHANGED = "statusChanged";
    private static List<SimdispenserMain> listSimDispenserMain;
    private final Context context;
    private static final int temphuProductId = 0;
    private static final int temphuVendorId = 0;
    private static final int temphuDeviceId = 0;
    private static final int temphuBaurate = 9600;
    SimdispenserMain[] listDispenser = new SimdispenserMain[4];
    //private boolean statusDispenser ;
    public DeviceModule(ReactApplicationContext reactContext) {
        super(reactContext);
        this.context = reactContext.getApplicationContext();
        listSimDispenserMain = Arrays.asList(new SimdispenserMain(),new SimdispenserMain() , new SimdispenserMain(), new SimdispenserMain());

    }
    public HashMap<String, Object> GetAllsStatusBox(int idSimDispenser)
    {
        return SimdispenserMain.m_control.controlCheckSensorStatus(listSimDispenserMain.get(idSimDispenser));
    }
    @NotNull
    @Override
    public String getName() {
        return "DeviceModule";
    }
    @ReactMethod
    public void openUPS()
    {
        SerialPortExample x = new SerialPortExample();
        x.open();
    }

    
    @ReactMethod()
    public boolean InitDispenser(int locationDespencer , String port ){

        HashMap<String,Object> status = listSimDispenserMain.get(locationDespencer).Init(timetoRecyceCard,port,baurate);
        return (boolean) status.get("status");
    }



    @ReactMethod
    public String getStatusUPS(){
        Log.d("UPS1","Step 0");
        SDKUPS sdkups = new SDKUPS("/dev/ttyXR6",2400);
        Log.d("UPS1","Step 1");
        UPSModel upsData = null;
        if (sdkups.connect()) {
            Log.d("UPS1","Step 2 Connect status"+sdkups.connect());
            upsData = sdkups.getInfo();
            upsData.batteryLevel = sdkups.getBatteryLevel();
        }
        assert upsData != null;
        Log.d("BinXiudeptrai","totalInfo :"+upsData.totalInfo+" batteryVoltage :"+upsData.batteryVoltage+" inputVoltage :"+upsData.inputVoltage+" outputVoltage: "+upsData.outputVoltage
                +" consumedLoad: "+upsData.consumedLoad + " "+upsData.frequencyOutput);
        return "totalInfo :"+upsData.totalInfo+" batteryVoltage :"+upsData.batteryVoltage+" inputVoltage :"+upsData.inputVoltage+" outputVoltage: "+upsData.outputVoltage
                +" consumedLoad: "+upsData.consumedLoad + " "+upsData.frequencyOutput;
    }
    @ReactMethod
    public void install(String path) {
        String cmd = "chmod 777 " + path;
        try {
            Runtime.getRuntime().exec(cmd);
        } catch (Exception e) {
            e.printStackTrace();
        }
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Uri apkUri = FileProvider.getUriForFile(context, context.getApplicationContext().getPackageName() + ".fileprovider", new File(path));
        intent.setDataAndType(apkUri, "application/vnd.android.package-archive");
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        context.startActivity(intent);
    }
    @ReactMethod
    public void rebootDevice() {
        Log.d("Đã nhấn reboot","");
        try {
            Runtime.getRuntime().exec("reboot");
            Log.d("Reboot Action: ==", "Reboot successfully");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @ReactMethod
    public String initSimDispenser() {
//        Log.d("DeviceModule", "initSimDispenser: " + timeToRecyleCard + ", " + comName + ", " + baurate + ", " + idSimDispenser);
        HashMap<String, Object> initData = listSimDispenserMain.get(1).Init("60000", "/dev/ttyS0", "115200");
        return Helper.convertToJsonString(initData);
    }

    @ReactMethod
    public String getStatus(int idSimDispenser) {
        Log.d("DeviceModule", "getStatus: " + idSimDispenser);

        HashMap<String, Object> getStatusResult = SimdispenserMain.m_control.controlCheckSensorStatus(listSimDispenserMain.get(idSimDispenser));
        Log.d("status sim : ",Helper.convertToJsonString(getStatusResult));
        return Helper.convertToJsonString(getStatusResult);

    }
   @ReactMethod
    public boolean getStatusBoxCard(int idSimDispenser){
        HashMap<String, Object> getStatusResult = SimdispenserMain.m_control.controlCheckSensorStatus(listSimDispenserMain.get(idSimDispenser));
       return getStatusResult.get("sensor7") == "have card";
   }
   @ReactMethod
    public boolean getStatusReadIdArea(int idSimDispenser)
   {
        HashMap<String,Object> allStatus = GetAllsStatusBox(idSimDispenser);
        return allStatus.get("sensor") == "have card";
   }
    @ReactMethod
    public boolean moveCardToBoxCheck(int idSimDispenser) {
        Log.d("DeviceModule", "moveCardToBoxCheck: " + idSimDispenser);

        return SimdispenserMain.m_control.controlICCardPosiion(listSimDispenserMain.get(idSimDispenser));
    }

    @ReactMethod
    public void readSerialSim(int idSimDispenser, Callback callback) {
        Log.d("DeviceModule", "readSeriSim: " + idSimDispenser);

        HashMap<String, Object> readSeriSimResult = SimdispenserMain.m_control.controlReadICCIDSim(listSimDispenserMain.get(idSimDispenser));
        if (readSeriSimResult != null) {
            Log.d("Result Read SerialSim" , Helper.convertToJsonString(readSeriSimResult));
            callback.invoke(null, Helper.convertToJsonString(readSeriSimResult));
        } else {
            callback.invoke("Read seri Sim not available.", null);
        }
    }

    @ReactMethod
    public void retainCard(int idSimDispenser, Callback callback) {
        Log.d("DeviceModule", "retainCard: " + idSimDispenser);

        boolean retainCard = SimdispenserMain.m_control.controlRetainCard(listSimDispenserMain.get(idSimDispenser));
        callback.invoke(null, retainCard);
    }

    @ReactMethod
    public boolean moveToFront(int idSimDispenser) {
        Log.d("DeviceModule", "moveToFront: " + idSimDispenser);

        return SimdispenserMain.m_control.controlClipPosiion(listSimDispenserMain.get(idSimDispenser));
    }

    @ReactMethod
    public boolean ejectCard() {
        Log.d("DeviceModule", "ejectCard: " + 0);
        int idSim = 0;
        return SimdispenserMain.m_control.controlRetainCard(listSimDispenserMain.get(idSim));
    }

    @ReactMethod
    public HashMap<String, Object> ejectOneCard() {
        Log.d("DeviceModule", "ejectOneCard");
        try
        {
            return listSimDispenserMain.get(0).ejectOneCard();
        }
        catch(Exception e)
        {
            return null;
        }

    }
    @ReactMethod
    public boolean printReceipt(String kioskId, int deviceId, int vendorId, int productId, String receiptNumber, String language) {
        if (language == null) language = "en";
//        if (vendorId == null) vendorId = 4070;
//        if (productId == null) productId = 33054;
        Map<String, String> total = new HashMap<>();
        total.put("price","1.000.000 VND" );
        total.put("quantity", "5");
        total.put("serials", "1232131231233123213123"); // không sử dụng (để giai đoạn 2)

        List<Map<String, String>> commodityList = new ArrayList<>();
        Map<String, String> commodity = new HashMap<>();
        commodity.put("description", "Sim 4G (30 Day)");
        commodity.put("quantity", "1");
        commodity.put("price", "200.000 VND");
        commodityList.add(commodity);

        Map<String, String> commodity2 = new HashMap<>();
        commodity2.put("description", "Sim 4G (40 Day)");
        commodity2.put("quantity", "1");
        commodity2.put("price", "500.000 VND");
        commodityList.add(commodity2);

        POSCustomed mPos = new POSCustomed();
        USBPrinting mUsb = new USBPrinting();
        mPos.Set(mUsb);
        SDKPrints printController = new  SDKPrints();
        SDKPrints.PrintStatus status = printController.getStatus(vendorId, productId, deviceId, context, mPos, mUsb);
        mUsb.Close();
        Log.d("Status papers", String.valueOf(status.isIsOutOfPaper()));
        if (!status.isIsOutOfPaper()) {
            SDKPrints.print(vendorId, productId, deviceId, null,commodityList, total, kioskId, receiptNumber, context, mPos, mUsb, language);
            return true;
        }
        return false;
    }
    @ReactMethod
    public void Printest(){

//        if (vendorId == null) vendorId = 4070;
//        if (productId == null) productId = 33054;
        Map<String, String> total = new HashMap<>();
        total.put("price","1.000.000 VND" );
        total.put("quantity", "5");
        total.put("serials", "1232131231233123213123"); // không sử dụng (để giai đoạn 2)

        List<Map<String, String>> commodityList = new ArrayList<>();
        Map<String, String> commodity = new HashMap<>();
        commodity.put("description", "Sim 4G (30 Day)");
        commodity.put("quantity", "1");
        commodity.put("price", "200.000 VND");
        commodityList.add(commodity);

        PrinterService printerService = new PrinterService();
        //printerService.printReceiptService("haiba ", "123456",total,commodityList,"en",context);
    }
    @ReactMethod
    public void TemperatureConnect(int portnum, Promise promise){
        List<DeviceInfo> devices = SDKLocker.getAllUsbDevicesHasDriver(context);
        for(int i = 0 ; i < devices.size(); i++){
            Log.d("log port ",""+devices.get(i).port);
            Log.d("log id ",""+devices.get(i).device.getDeviceId());
        };
        SDKTemperatureAndHumidity temperatureAndHumidity = new SDKTemperatureAndHumidity();
       boolean status  =  temperatureAndHumidity.connect(context,7003,portnum,9600);
       if(status)
       {
          double temp =  temperatureAndHumidity.getTempHumiData().temperature;
           Log.d("temp ",""+temp);
       }
       promise.resolve(status);
    }
    @ReactMethod
    public void Testing(){
            SDKLocker locker = new SDKLocker();
        List<DeviceInfo> listItems =  locker.getAllUsbDeviceHasDriverByVendorIdProductIdAndDeviceId(context,6790,29987,1009);

        SDKTemperatureAndHumidity tempHuSDK = new SDKTemperatureAndHumidity();
        boolean status = tempHuSDK.connect(context,1009,listItems.get(0).port,9600);
        Log.d("status log connect ", " "+status);
        if(status){
            TempHumiData tem = tempHuSDK.getTempHumiData();
            Log.d("nhiet do la  ", " "+tem.temperature);
        }

    }
    public void Testinglocker(){
        SDKLocker locker = new SDKLocker();
        List<DeviceInfo> listItems =  locker.getAllUsbDeviceHasDriverByVendorIdProductIdAndDeviceId(context,6790,29987,1);

        SDKTemperatureAndHumidity tempHuSDK = new SDKTemperatureAndHumidity();
        boolean status = tempHuSDK.connect(context,1009,listItems.get(0).port,9600);
        Log.d("status log connect ", " "+status);
        if(status){
            TempHumiData tem = tempHuSDK.getTempHumiData();
            Log.d("nhiet do la  ", " "+tem.temperature);
        }

    }
    @ReactMethod
    public String getTemperature()
    {
        StringBuilder result = new StringBuilder();
        SDKTemperatureAndHumidity tempHuSDK = new SDKTemperatureAndHumidity();

        TempHumiData temphuData = null;

        List<DeviceInfo> devices = SDKLocker.getAllUsbDevicesHasDriver(context);
        Log.d("log all driver ",""+devices);
//        List<DeviceInfo> devices = getUSBDeviceHasDriver();
        if(devices.size() == 0) {
            Log.d("Huhu","Không có thiết bị nào hết hic");
            return "Không có thiết bị nào có driver hết";
        }
        int maxTry = 0;
        while(maxTry < 5 && temphuData == null) {
            Log.d("Thông báo", " đã vào vòng lặp");
            for (DeviceInfo each : devices) {
                boolean connect = tempHuSDK.connect(context, each, temphuBaurate);
                Log.d("Result SDKTempe connect",String.valueOf(connect));
                Log.d("Device Information","Name : "+each.device.getDeviceName()+" Id : "+each.device.getVendorId()+" ProductId : "+ each.device.getProductId());
                if (temphuDeviceId == -1 && each.device.getVendorId() == temphuVendorId && each.device.getProductId() == temphuProductId) {


                    if (connect) {
                        temphuData = tempHuSDK.getTempHumiData();
                        tempHuSDK.disconnect();
                    }
                    continue;
                }

                if (temphuDeviceId != -1 && each.device.getVendorId() == temphuVendorId && each.device.getProductId() == temphuProductId && Utils.compareTwoDeviceId(each.device.getDeviceId(), temphuDeviceId)) {
                    if (connect) {
                        temphuData = tempHuSDK.getTempHumiData();
                    }
                }
                if(temphuData != null)
                {
                    result.append("Device : ").append(each.device.getDeviceId()).append("\nTemperature").append(temphuData.temperature).append("\nHumidity").append(temphuData.humidity).append("\nDewdrop").append(temphuData.dewdrop);
                    Log.d("Temperature","Device : "+each.device.getDeviceId()+"\nTemperature" + temphuData.temperature+"\nHumidity" + temphuData.humidity+"\nDewdrop" +temphuData.dewdrop);
                }
            }
            Utils.sleep(1);
            maxTry += 1;
        }
        Log.d("result tempe",result.toString());
        return result.toString();
    }
    @ReactMethod
    private List<DeviceInfo> getUSBDeviceHasDriver(int vitri)
    {
        UsbManager usbManager = (UsbManager)context.getSystemService(Context.USB_SERVICE);
        Log.d("List USB",""+usbManager);
        UsbSerialProber usbDefaultProber = UsbSerialProber.getDefaultProber();
        UsbSerialProber usbCustomProber = CustomProber.getCustomProber();
        List<DeviceInfo> listItems = new ArrayList();
        Iterator var5 = usbManager.getDeviceList().values().iterator();
        UsbSerialDriver driver = null;
        while(driver != null) {
            while(var5.hasNext()) {
                UsbDevice device = (UsbDevice)var5.next();
                driver = usbDefaultProber.probeDevice(device);
                if (driver == null) {
                    driver = usbCustomProber.probeDevice(device);
                }

                if (driver != null) {
                    for(int port = 0; port < driver.getPorts().size(); ++port) {
                        listItems.add(new DeviceInfo(device, port, driver));
                    }
                } else {
                    listItems.add(new DeviceInfo(device, 0, usbCustomProber.probeDevice(device)));
                }
            }
        }
        Log.d("List USB",""+ listItems.get(vitri));
        return listItems;
    }
    private void DoiNoiGoiSim(String goisim)
    {
        //call api
        //get status api =>
        // if true => da sim ga
        // false => eject the?
    }
}
