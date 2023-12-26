package com.demosdk.DeviceModule;

import android.content.Context;
import android.util.Log;
import com.demosdk.Controls.PrinterControl;
import com.demosdk.Helper;
import com.facebook.react.bridge.*;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class Printer extends ReactContextBaseJavaModule {
    private static final PrinterControl priterController = new PrinterControl();
    public final Context context;
    public Printer(ReactApplicationContext reactContext) {
        super(reactContext);
        this.context = reactContext.getApplicationContext();
    }

    /**
     *
     * @param kioskId String id kios
     * @param receiptNumber
     * @param language language bill print
     * @param hotline hotline in bill if = null default "1900 1900"
     * @param email email in bill if = null default "cskh@myLocal.vn"
     * @param totalbill total sim of card return in bill
     * @param commodityListBill list sim of card in bill
     * @return
     */
    @ReactMethod
    public void PrinterBill(String kioskId,
                               String receiptNumber,
                               String language,
                               String hotline ,
                               String email,
                               ReadableMap totalbill,
                               ReadableArray commodityListBill,
                               Promise promise
                               ){
        if(Objects.equals(hotline, "")) hotline = " 1900 1900";
        if(Objects.equals(email, "")) email = "cskh@myLocal.vn";
        Map<String,String> total = Helper.convertReadableMapToMap(totalbill);
        List<Map<String,String>> commodityList = Helper.convertReadableArrayToList(commodityListBill);

        boolean status = priterController.printerbillController(kioskId,receiptNumber,total,commodityList,language,context,hotline,email);
        promise.resolve(status);
    }

    /**
     * warning :  call function after print bill
     * @return  true if in machine printer out of paper and else
     */
    @ReactMethod
    public void StatusOutOfPaper(Promise promise) {
        boolean status =  priterController.getStatusOutOfPaperController(context);
        promise.resolve(status);
    }

    /**
     *  warning :  call function after print bill
     * @return true if machine printer almost out of paper and else
     */
    @ReactMethod
    public void CheckPaper(Promise promise){
        boolean status =  priterController.getStatusWarningPaperController(context);
        promise.resolve(status);
    }
    @NotNull
    @Override
    public String getName() {
        return "Printer";
    }
}
