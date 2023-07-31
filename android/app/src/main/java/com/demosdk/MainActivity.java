package com.demosdk;

import android.content.Context;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.util.Log;
import asim.sdk.printer.POSCustomed;
import asim.sdk.printer.SDKPrints;
import com.facebook.react.ReactActivity;
import com.facebook.react.ReactActivityDelegate;
import com.facebook.react.defaults.DefaultNewArchitectureEntryPoint;
import com.facebook.react.defaults.DefaultReactActivityDelegate;
import asim.sdk.sdksimdispenser.*;
import com.lvrenyang.io.USBPrinting;

import java.util.*;

public class MainActivity extends ReactActivity {
  @Override
  protected String getMainComponentName() {
    return "DemoSDK";
  }

  @Override
  protected ReactActivityDelegate createReactActivityDelegate() {
    return new DefaultReactActivityDelegate(
        this,
            Objects.requireNonNull(getMainComponentName()),
        DefaultNewArchitectureEntryPoint.getFabricEnabled());
  }
}
