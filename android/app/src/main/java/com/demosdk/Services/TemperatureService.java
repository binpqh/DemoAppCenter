package com.demosdk.Services;

import android.content.Context;
import asim.sdk.locker.DeviceInfo;
import asim.sdk.tempandhum.SDKTemperatureAndHumidity;
import asim.sdk.tempandhum.TempHumiData;
import com.demosdk.Helper;

import java.util.List;

public class TemperatureService {
    private static final int temphuProductId = 29987;
    private static final int temphuVendorId = 6790;
    private static final int temphuDeviceId = 1009;
    private static final int temphuBaurate = 9600;

    public double getTemperatureService(Context context ) {
        double temperature= 0.0;
        List<DeviceInfo> listDevice = Helper.getPortDevice(context,temphuVendorId,temphuProductId,temphuDeviceId);
        if(listDevice!= null)
        {
            SDKTemperatureAndHumidity tempHuSDK = new SDKTemperatureAndHumidity();
            boolean status = tempHuSDK.connect(context, temphuDeviceId, listDevice.get(0).port, temphuBaurate);
            if (status)
            {
                TempHumiData tempHumiData =  tempHuSDK.getTempHumiData();
                if(tempHumiData != null) temperature = tempHumiData.temperature;
            }
        }

        return temperature;
    }
}
