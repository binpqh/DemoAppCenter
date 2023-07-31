package com.demosdk;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.util.Log;
import android.widget.Toast;
import asim.sdk.common.Utils;
import asim.sdk.locker.CustomProber;
import asim.sdk.locker.DeviceInfo;
import asim.sdk.locker.SDKLocker;
import asim.sdk.printer.POSCustomed;
import asim.sdk.printer.SDKPrints;
import asim.sdk.sdksimdispenser.SimdispenserMain;
import com.facebook.react.bridge.*;
import com.hoho.android.usbserial.driver.UsbSerialDriver;
import com.hoho.android.usbserial.driver.UsbSerialProber;
import com.lvrenyang.io.USBPrinting;
import org.jetbrains.annotations.NotNull;
import asim.sdk.tempandhum.SDKTemperatureAndHumidity;
import asim.sdk.tempandhum.TempHumiData;
import java.io.IOException;
import java.util.*;

public class DeviceModule extends ReactContextBaseJavaModule {

    private static final String SIM_DISPENSER_SDK_CHANNEL = "SDKSimDispenser";
    private static final String EVENT_STATUS_CHANGED = "statusChanged";
    private static List<SimdispenserMain> listSimDispenserMain;
    private final Context context;
    private static int temphuProductId = 0;
    private static int temphuVendorId = 0;
    private static int temphuDeviceId = 0;
    private static int temphuBaurate = 9600;
    public DeviceModule(ReactApplicationContext reactContext) {
        super(reactContext);
        this.context = reactContext.getApplicationContext();
        listSimDispenserMain = Arrays.asList(new SimdispenserMain(), new SimdispenserMain(), new SimdispenserMain(), new SimdispenserMain());
    }

    @NotNull
    @Override
    public String getName() {
        return "DeviceModule";
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
    public HashMap<String, String> initSimDispenser() {
//        Log.d("DeviceModule", "initSimDispenser: " + timeToRecyleCard + ", " + comName + ", " + baurate + ", " + idSimDispenser);
        HashMap<String, Object> initData = listSimDispenserMain.get(1).Init("60000", "/dev/ttyS0", "115200");
        return Helper.convertToMapString(initData);
    }

    @ReactMethod
    public String getStatus(int idSimDispenser) {
        Log.d("DeviceModule", "getStatus: " + idSimDispenser);

        HashMap<String, Object> getStatusResult = listSimDispenserMain.get(idSimDispenser).m_control.controlCheckSensorStatus(listSimDispenserMain.get(idSimDispenser));
        var x =  Helper.convertToMapString(getStatusResult);
        List<String> valuesList = new ArrayList<>(x.values());
        String a = "";
        for (var item: valuesList
             ) {
            a += item;
        }
        return a;
    }

    @ReactMethod
    public boolean moveCardToBoxCheck(int idSimDispenser) {
        Log.d("DeviceModule", "moveCardToBoxCheck: " + idSimDispenser);

        return listSimDispenserMain.get(idSimDispenser).m_control.controlICCardPosiion(listSimDispenserMain.get(idSimDispenser));
    }

    @ReactMethod
    public void readSeriSim(int idSimDispenser, Callback callback) {
        Log.d("DeviceModule", "readSeriSim: " + idSimDispenser);

        HashMap<String, Object> readSeriSimResult = listSimDispenserMain.get(idSimDispenser).m_control.controlReadICCIDSim(listSimDispenserMain.get(idSimDispenser));
        if (readSeriSimResult != null) {
            callback.invoke(null, Helper.convertToMapString(readSeriSimResult));
        } else {
            callback.invoke("Read seri Sim not available.", null);
        }
    }

    @ReactMethod
    public void retainCard(int idSimDispenser, Callback callback) {
        Log.d("DeviceModule", "retainCard: " + idSimDispenser);

        boolean retainCard = listSimDispenserMain.get(idSimDispenser).m_control.controlRetainCard(listSimDispenserMain.get(idSimDispenser));
        callback.invoke(null, retainCard);
    }

    @ReactMethod
    public boolean moveToFront(int idSimDispenser) {
        Log.d("DeviceModule", "moveToFront: " + idSimDispenser);

        return listSimDispenserMain.get(idSimDispenser).m_control.controlClipPosiion(listSimDispenserMain.get(idSimDispenser));
    }

    @ReactMethod
    public boolean ejectCard() {
        Log.d("DeviceModule", "ejectCard: " + 0);
        int idSim = 0;
        return listSimDispenserMain.get(idSim).m_control.controlResetAndEject(listSimDispenserMain.get(idSim));
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
        total.put("price","250,000VND" );
        total.put("quantity", "5");
        total.put("serials", "1232131231233123213123"); // không sử dụng (để giai đoạn 2)

        List<Map<String, String>> commodityList = new ArrayList<>();
        Map<String, String> commodity = new HashMap<>();
        commodity.put("description", "Sim description");
        commodity.put("quantity", "50");
        commodity.put("price", "500,000VND");
        commodityList.add(commodity);

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
    public String getTemperature()
    {
        StringBuilder result = new StringBuilder();
        SDKTemperatureAndHumidity tempHuSDK = new SDKTemperatureAndHumidity();

        TempHumiData temphuData = null;

//        List<DeviceInfo> devices = SDKLocker.getAllUsbDevicesHasDriver(context);
        List<DeviceInfo> devices = getUSBDeviceHasDriver();
        if(devices.size() == 0) {
            Log.d("Huhu","Không có thiết bị nào hết hic");
            return "Không có thiết bị nào có driver hết";
        }
        int maxTry = 0;
        while(maxTry < 5 && temphuData == null) {
            Log.d("Thông báo", " đã vào vòng lặp");
            for (DeviceInfo each : devices) {
                Log.d("Device Information","Name : "+each.device.getDeviceName()+"Id : "+each.device.getVendorId());
                if (temphuDeviceId == -1 && each.device.getVendorId() == temphuVendorId && each.device.getProductId() == temphuProductId) {
                    boolean connect = tempHuSDK.connect(context, each, temphuBaurate);
                    if (connect) {
                        temphuData = tempHuSDK.getTempHumiData();
                        tempHuSDK.disconnect();
                    }
                    continue;
                }

                if (temphuDeviceId != -1 && each.device.getVendorId() == temphuVendorId && each.device.getProductId() == temphuProductId && Utils.compareTwoDeviceId(each.device.getDeviceId(), temphuDeviceId)) {
                    boolean connect = tempHuSDK.connect(context, each, temphuBaurate);
                    if (connect) {
                        temphuData = tempHuSDK.getTempHumiData();
                    }
                }
                result.append("Device : ").append(each.device.getDeviceId()).append("\nTemperature").append(temphuData.temperature).append("\nHumidity").append(temphuData.humidity).append("\nDewdrop").append(temphuData.dewdrop);
                Log.d("Temperature","Device : "+each.device.getDeviceId()+"\nTemperature" + temphuData.temperature+"\nHumidity" + temphuData.humidity+"\nDewdrop" +temphuData.dewdrop);
            }
            Utils.sleep(1);
            maxTry += 1;
        }
        return result.toString();
    }
    private List<DeviceInfo> getUSBDeviceHasDriver()
    {
        UsbManager usbManager = (UsbManager)context.getSystemService(Context.USB_SERVICE);
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
        return listItems;
    }
}
