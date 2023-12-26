
package com.demosdk.Printer;

import java.util.HashMap;
import java.util.Map;

public class PrintsConfigs {
    public int nPrintWidth = 550;
    public boolean bCutter = true;
    public boolean bDrawer = true;
    public boolean bBeeper = true;
    public int nPrintCount = 1;
    public int nCompressMethod = 0;
    public boolean bAutoPrint = false;
    public int nPrintContent = 1;
    String printTemplate = "text";

    public PrintsConfigs() {
    }

    public PrintsConfigs(int nPrintWidth, boolean bCutter, boolean bDrawer, boolean bBeeper, int nPrintCount, int nCompressMethod, boolean bAutoPrint, int nPrintContent, String printTemplate) {
        this.nPrintWidth = nPrintWidth;
        this.bCutter = bCutter;
        this.bDrawer = bDrawer;
        this.bBeeper = bBeeper;
        this.nPrintCount = nPrintCount;
        this.nCompressMethod = nCompressMethod;
        this.bAutoPrint = bAutoPrint;
        this.nPrintContent = nPrintContent;
        this.printTemplate = printTemplate;
    }

    public static Map<String, String> getCommodityTest(final String des, final String qty, final String price) {
        return new HashMap<String, String>() {
            {
                this.put("description", des);
                this.put("quantity", qty);
                this.put("price", price);
            }
        };
    }

    public static String ResultCodeToString(int code) {
        switch (code) {
            case -13:
            default:
                return "unknown mistake";
            case -12:
                return "Please take away the printed receipt before printing!";
            case -11:
                return "The connection is interrupted, please confirm whether the printer is connected";
            case -10:
                return "The top cover was opened during printing, please reprint";
            case -9:
                return "There is no paper during printing, please check the completeness of the documents";
            case -8:
                return "Failed to check the status, please check whether the communication port is connected normally";
            case -7:
                return "Real-time status query failed";
            case -6:
                return "Cover open";
            case -5:
                return "Printer is out of paper";
            case -4:
                return "Printer is offline";
            case -3:
                return "The print head is overheated, please wait for the printer to cool down";
            case -2:
                return "The cutter is abnormal, please remove it manually";
            case -1:
                return "The receipt is not printed, please check if there is a paper jam";
            case 0:
                return " ";
            case 1:
                return "The paper is almost out, please pay attention to replace the paper roll";
            case 2:
                return "The paper is almost out and there is an uncollected receipt at the paper exit, please pay attention to replace the paper roll and take away the receipt in time";
            case 3:
                return "There is an uncollected receipt at the paper exit, please pay attention to take it away in time";
        }
    }
}
