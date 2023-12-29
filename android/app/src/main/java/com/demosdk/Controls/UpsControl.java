package com.demosdk.Controls;

import asim.sdk.ups.UPSModel;
import com.demosdk.Services.UpsService;

public class UpsControl {
    private static final UpsService upsControl = new UpsService();
    public UPSModel getInfoUpsControl(){
        UPSModel upsModel = null;
        if(upsControl.connecUpsService()) upsModel = upsControl.getInfoUps();
        return upsModel;
    }
    public double getBatteryLevelControl(){
        double batteryLevel = 0;
        if(upsControl.connecUpsService()) batteryLevel =  upsControl.getBatteryLevel();
        return batteryLevel;
    }
}
