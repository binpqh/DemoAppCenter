package com.demosdk.DeviceModule;

import android.content.Context;
import com.demosdk.Controls.TemperatureControl;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import org.jetbrains.annotations.NotNull;

public class DevicePeripherals extends ReactContextBaseJavaModule {

    TemperatureControl temperatureControl = new TemperatureControl();
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



    @NotNull
    @Override
    public String getName() {
        return "DevicePeripherals";
    }
}
