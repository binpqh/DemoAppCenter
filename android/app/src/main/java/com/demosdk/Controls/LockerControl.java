package com.demosdk.Controls;

import android.content.Context;
import com.demosdk.Services.LockerService;

public class LockerControl {
    LockerService lockerService = new LockerService();
    public boolean openLockerController(Context context){
        return lockerService.openLockerService(context);
    }
    public boolean closeLockerController(Context context){
        return  lockerService.closeLockerService(context);
    }

}
