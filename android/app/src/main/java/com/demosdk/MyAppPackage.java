package com.demosdk;
import com.demosdk.DeviceModule.Despenser;
import com.demosdk.DeviceModule.DevicePeripherals;
import com.demosdk.DeviceModule.Printer;
import com.demosdk.Test.DeviceModule;
import com.facebook.react.ReactPackage;
import com.facebook.react.bridge.NativeModule;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.uimanager.ViewManager;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MyAppPackage implements ReactPackage {

    @NotNull
    @Override
    public List<ViewManager> createViewManagers(@NotNull ReactApplicationContext reactContext) {
        return Collections.emptyList();
    }

    @NotNull
    @Override
    public List<NativeModule> createNativeModules(
            @NotNull ReactApplicationContext reactContext) {
        List<NativeModule> modules = new ArrayList<>();
        modules.add(new DeviceModule(reactContext));
        modules.add(new Despenser(reactContext));
        modules.add(new Printer(reactContext));
        modules.add(new DevicePeripherals(reactContext));
        return modules;
    }

}