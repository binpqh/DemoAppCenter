package com.demosdk.DeviceModule;

import android.content.Context;
import android.util.Log;
import com.demosdk.Controls.SimDepenserControl;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.TimeUnit;

public class Despenser extends ReactContextBaseJavaModule {

    private static final SimDepenserControl CONTROL = new SimDepenserControl();
    public final Context context;
    public Despenser(ReactApplicationContext reactContext) {
        super(reactContext);
        this.context = reactContext.getApplicationContext();
    }
    @ReactMethod
    public void initDespenser(Integer index , String port,Promise promise){
        boolean status = CONTROL.InitDispenser(index, port);
        promise.resolve(status);
    }
    @ReactMethod
    public void checkReadArea(Integer index , Promise promise){
        boolean status = CONTROL.checkSensorReadArea(index);
        promise.resolve(status);
    }
    @ReactMethod
    public void checkOutPut(Integer index , Promise promise){
        boolean status = CONTROL.checkSensorOutPutCard(index);
        promise.resolve(status);
    }
    @ReactMethod
    public void moveCard(Integer index,Promise promise){
        boolean status = CONTROL.moveCardToRead(index);
        promise.resolve(status);
    }
    @ReactMethod
    public void outCard(Integer index,Promise promise){
        boolean status = CONTROL.injectcard(index);
        promise.resolve(status);
    }
    @ReactMethod
    public void rejectCard(Integer index,Promise promise){
        boolean status = CONTROL.rejectCardInReadArea(index);
        promise.resolve(status);
    }
    @ReactMethod
    public void readSerial(Integer index,Promise promise){
        String serial = CONTROL.readCCID(index);
        promise.resolve(serial);
    }

    @NotNull
    @Override
    public String getName() {
        return "Despenser";
    }
}
