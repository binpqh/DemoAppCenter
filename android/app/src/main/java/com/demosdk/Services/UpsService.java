package com.demosdk.Services;

import asim.sdk.ups.SDKUPS;

public class UpsService {
    private static final String portName = "/dev/ttyXR6";
    private  static final int baudRate = 2400;
    private static final SDKUPS sdkups = new SDKUPS(portName,baudRate);
    public boolean connecUpsService(){
        return sdkups.connect();
    }
    public asim.sdk.ups.UPSModel getInfoUps(){
        return sdkups.getInfo();
    }
    public double getBatteryLevel(){
        return  sdkups.getBatteryLevel();
    }
}
