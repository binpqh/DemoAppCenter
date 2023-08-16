package com.demosdk.UPSModule;

import android.util.Log;
import asim.sdk.common.Utils;
import asim.sdk.ups.UPSConfigs;
import asim.sdk.ups.UPSModel;
import com.lvrenyang.io.Pos;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SDKUPS {
    public Pos mPos = null;
    public COMIO mCom = null;
    public String portName = "/ttyXR6";
    public int baudrate = 2400;

    public SDKUPS(String portName, int baudrate) {
        this.mPos = new Pos();
        this.mCom = new COMIO();
        this.portName = portName;
        this.baudrate = baudrate;
        this.mPos.Set(this.mCom);
    }

    public boolean connect() {
        try {
            if (!this.mCom.IsOpened()) {
                this.mCom.Open(this.portName, this.baudrate, 0);
            }

            return true;
        } catch (Exception var2) {
            return false;
        }
    }

    public UPSModel getInfo() {
        byte[] status = new byte[256];
        this.mPos.GetIO().Write(UPSConfigs.cmdGetInfo, 0, UPSConfigs.cmdGetInfo.length);
        int readResult = this.mPos.GetIO().Read(status, 0, status.length, 1000);
        Log.d("Result bytes", String.valueOf(readResult));
        byte[] realStatus = Utils.getSubListByte(status, 0, readResult - 1);
        String totalInfo = (new String(realStatus)).trim();
        Log.d("totalInfo==", totalInfo);
        String[] allValues = totalInfo.split(" ");
        List<Double> allReals = new ArrayList();
        String[] var7 = allValues;
        int var8 = allValues.length;

        for(int var9 = 0; var9 < var8; ++var9) {
            String temp = var7[var9];
            String str = temp.replaceAll("[^\\d.]", "");

            try {
                double dval = Double.parseDouble(str);
                allReals.add(dval);
                Log.d("UPS"+var9,temp);
            } catch (Exception var14) {
                var14.printStackTrace();
            }
        }

        UPSModel data = new UPSModel("", 0.0, 0.0, 0.0, 0.0, 0.0);
        if (allReals.size() >= 11) {
            data.totalInfo = totalInfo;
            data.inputVoltage = (Double)allReals.get(0);
            data.outputVoltage = (Double)allReals.get(4);
            data.consumedLoad = (Double)allReals.get(6);
            data.frequencyOutput = (Double)allReals.get(7);
            data.batteryVoltage = (Double)allReals.get(10);
        }

        return data;
    }

    public double getBatteryLevel() {
        try {
            byte[] status = new byte[256];
            this.mPos.GetIO().Write(UPSConfigs.cmdGetBatteryLevel, 0, UPSConfigs.cmdGetBatteryLevel.length);
            int readResult = this.mPos.GetIO().Read(status, 0, status.length, 1000);
            byte[] realStatus = Utils.getSubListByte(status, 0, readResult - 1);
            Log.d("===BatteryLevel com===", new String(realStatus));
            double value = Double.parseDouble((new String(realStatus)).trim());
            return value;
        } catch (Exception var6) {
            return 0.0;
        }
    }
}
