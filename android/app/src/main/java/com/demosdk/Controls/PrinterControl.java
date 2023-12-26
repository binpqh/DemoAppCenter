package com.demosdk.Controls;

import android.content.Context;
import com.demosdk.Services.PrinterService;

import java.util.List;
import java.util.Map;

public class PrinterControl {
    private static final PrinterService printerService = new PrinterService();
    public boolean printerbillController(String kioskId, String receiptNumber, Map<String,String> total , List<Map<String,String>> commodityList, String language, Context context,String hotline , String email){
        return  printerService.printReceiptService(kioskId,receiptNumber,total,commodityList,language,context,hotline,email);
    }
    public boolean getStatusWarningPaperController(Context context){
        return printerService.GetStatusPrinterService(context).isIsWarningAboutPaper();
    }
    public boolean getStatusOutOfPaperController(Context context){
        return  printerService.GetStatusPrinterService(context).isIsOutOfPaper();

    }
}
