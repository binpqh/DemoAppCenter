package com.demosdk;

import android.content.Context;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.os.Bundle;
import android.util.Log;
import asim.sdk.printer.POSCustomed;
import asim.sdk.printer.SDKPrints;
import com.facebook.react.ReactActivity;
import com.facebook.react.ReactActivityDelegate;
import com.facebook.react.ReactInstanceManager;
import com.facebook.react.ReactRootView;
import com.facebook.react.defaults.DefaultNewArchitectureEntryPoint;
import com.facebook.react.defaults.DefaultReactActivityDelegate;
import asim.sdk.sdksimdispenser.*;
import com.lvrenyang.io.USBPrinting;
import com.microsoft.codepush.react.CodePush;

import java.util.*;

public class MainActivity extends ReactActivity {
  private ReactRootView mReactRootView;
  private ReactInstanceManager mReactInstanceManager;
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
  @Override
  protected void onCreate(Bundle savedInstanceState) {

    super.onCreate(savedInstanceState);
    mReactInstanceManager = ReactInstanceManager.builder()
            // ...
            // Add CodePush package
            .addPackage(new CodePush("xWgI2T7EKfbpN2AmQ_eZhIcwB0v4c8MTyBrQU", getApplicationContext(), BuildConfig.DEBUG))
            // Get the JS Bundle File via Code Push
            .setJSBundleFile(CodePush.getJSBundleFile())
            // ...

            .build();
    mReactRootView.startReactApplication(mReactInstanceManager, "MainApplication", null);

    setContentView(mReactRootView);
  }

}
