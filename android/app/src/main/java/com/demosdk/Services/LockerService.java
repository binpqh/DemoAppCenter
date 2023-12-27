package com.demosdk.Services;

import android.content.Context;
import android.util.Log;
import asim.sdk.common.Utils;
import asim.sdk.locker.SDKLocker;

import java.util.ArrayList;
import java.util.List;

public class LockerService {
    private static final int baurate = 9600;
    private static final int relayNumber = 1;
    private static final int deviceId = 1009;
    public boolean closeLockerService(Context context) {
        SDKLocker locker = new SDKLocker();
        boolean clear = false;
        int countClear = 0;
        List<Integer> excludedDevices = new ArrayList<>();
        excludedDevices.add(deviceId);
        while(countClear < 5 && !clear) {
            Log.d("ClearRelay", "Trying to clear relay ... countClear = " + countClear);
            clear = locker.closeLockNewNew(context, excludedDevices, baurate);
            Utils.sleep((long) 1);
            countClear += 1;
        }
        return clear;
    }
    public boolean openLockerService(Context context) {
        SDKLocker locker = new SDKLocker();
        int count = 0;
        List<Integer> excludedDevices = new ArrayList<>();
        excludedDevices.add(deviceId);
        Log.d("==Locker Controller==", "Excluded devices = " + deviceId);
        Log.d("Starting to open lock: ", "............count = " + count);
        return locker.openLockNewNew(context, relayNumber, excludedDevices, baurate);
    }
}
