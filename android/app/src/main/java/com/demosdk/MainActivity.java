package com.demosdk;

import com.facebook.react.ReactActivity;
import com.facebook.react.ReactActivityDelegate;
import com.facebook.react.defaults.DefaultNewArchitectureEntryPoint;
import com.facebook.react.defaults.DefaultReactActivityDelegate;

import java.util.Objects;

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
