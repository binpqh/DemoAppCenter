package com.demosdk.Services;

import android.content.Context;
import android.util.Log;
import com.demosdk.Printer.POSCustomed;
import com.demosdk.Printer.SDKPrints;
import com.lvrenyang.io.USBPrinting;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PrinterService {
    private static final Integer DevicePrinterId = 1005;
    private static final Integer VendorId = 4070;
    private static final Integer ProductID = 33054;
    private static final POSCustomed mPos = new POSCustomed();
    private static final USBPrinting mUSB = new USBPrinting();
    private static final SDKPrints  printController = new SDKPrints();


    public Boolean InitPrinter(){

        return false;
    }
    public SDKPrints.PrintStatus GetStatusPrinterService(Context context){
           return printController.getStatus(VendorId, ProductID, DevicePrinterId, context, mPos, mUSB);
    }

    public boolean printReceiptService(String kioskId, String receiptNumber, Map<String,String> total , List<Map<String,String>> commodityList, String language , Context context ,String hotline ,String email) {
            if (language == null) language = "en";
            mPos.Set(mUSB);
            SDKPrints.PrintStatus status = printController.getStatus(VendorId, ProductID, DevicePrinterId, context, mPos, mUSB);
            mUSB.Close();
            Log.d("Status papers", String.valueOf(status.isIsOutOfPaper()));
            if (!status.isIsOutOfPaper()) {
                SDKPrints.print(VendorId, ProductID, DevicePrinterId, null,commodityList, total, kioskId, receiptNumber, context,hotline,email, mPos, mUSB, language);
                return true;
            }
            return false;
        }
}

