package com.demosdk.DeviceModule;

import android.content.Context;
import android.util.Log;
import com.demosdk.Controls.LockerControl;
import com.demosdk.Controls.TemperatureControl;
import com.demosdk.Controls.UpsControl;
import com.demosdk.Helper;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import org.jetbrains.annotations.NotNull;

public class DevicePeripherals extends ReactContextBaseJavaModule {

    TemperatureControl temperatureControl = new TemperatureControl();
    LockerControl lockerControl = new LockerControl();
    UpsControl upsControl = new UpsControl();

    public final Context context;
    public DevicePeripherals(ReactApplicationContext reactContext) {
        super(reactContext);
        this.context = reactContext.getApplicationContext();
    }

    /**
     *
     * return temperature sensor by promise
     */
    @ReactMethod
    public void getTemperature(Promise promise){
       double tem =  temperatureControl.getTemperatureControl(context);
       promise.resolve(tem);
    }

    /**
     *
     * return true if open locker success and else
     */
    @ReactMethod
    public void openLocker(Promise promise){
        promise.resolve(lockerControl.openLockerController(context));
    }

    /**
     *
     * return true if close locker success and else
     */
    @ReactMethod
    public void closeLocker(Promise promise){
        promise.resolve(lockerControl.closeLockerController(context));
    }

    /**
     *
     * return battery Level Ups in Kiosk if return null connect false
     */
    @ReactMethod
    public void getBatteryLevel(Promise promise){promise.resolve(upsControl.getBatteryLevelControl());}

    /**
     *
     *return info Ups in Kiosk if return 0 connect false
     */
    @ReactMethod
    public void getInfoUps(Promise promise){
        promise.resolve(upsControl.getInfoUpsControl());
    }
    @NotNull
    @Override
    public String getName() {
        return "DevicePeripherals";
    }
}
