package com.demosdk.Controls;

import android.content.Context;
import com.demosdk.Services.TemperatureService;

public class TemperatureControl {
    TemperatureService temperatureService = new TemperatureService();
    public double getTemperatureControl(Context context){
        return temperatureService.getTemperatureService(context);
    }
}
