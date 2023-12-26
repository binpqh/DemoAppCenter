package com.demosdk.Printer;

import android.annotation.TargetApi;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.util.Log;
import android.widget.Toast;
import asim.sdk.common.Utils;
import com.demosdk.Printer.POSCustomed;
import com.demosdk.Printer.PrintTemplateText;
import com.demosdk.Printer.PrintsConfigs;
import com.lvrenyang.io.USBPrinting;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@TargetApi(12)
public class SDKPrints {
    static ExecutorService es = Executors.newScheduledThreadPool(30);
    public static int bPrintResult;

    public SDKPrints() {
    }

    public static List<Map<String, String>> getListUsbDevices(Context context) {
        UsbManager mUsbManager = (UsbManager)context.getSystemService("usb");
        List<Map<String, String>> result = new ArrayList();
        HashMap<String, UsbDevice> deviceList = mUsbManager.getDeviceList();
        Iterator<UsbDevice> deviceIterator = deviceList.values().iterator();

        while(deviceIterator.hasNext()) {
            final UsbDevice device = (UsbDevice)deviceIterator.next();
            Map<String, String> temp = new HashMap<String, String>() {
                {
                    this.put("vendorId", String.valueOf(device.getVendorId()));
                    this.put("productId", String.valueOf(device.getProductId()));
                    this.put("deviceId", String.valueOf(device.getDeviceId()));
                    this.put("getDeviceName", device.getDeviceName());
                }
            };
            result.add(temp);
        }

        return result;
    }

    public com.demosdk.Printer.SDKPrints.PrintStatus getStatus(int deviceId, Context context,POSCustomed pos, USBPrinting usb) {
        UsbManager mUsbManager = (UsbManager)context.getSystemService("usb");
        com.demosdk.Printer.SDKPrints.PrintStatus status = new com.demosdk.Printer.SDKPrints.PrintStatus();
        HashMap<String, UsbDevice> deviceList = mUsbManager.getDeviceList();
        Iterator<UsbDevice> deviceIterator = deviceList.values().iterator();

        while(deviceIterator.hasNext()) {
            UsbDevice device = (UsbDevice)deviceIterator.next();
            if (Utils.compareTwoDeviceId(device.getDeviceId(), deviceId) && usb.Open(mUsbManager, device, context)) {
                byte[] mStatusType1 = new byte[1];
                byte[] mStatusType2 = new byte[1];
                byte[] mStatusType3 = new byte[1];
                byte[] mStatusType4 = new byte[1];
                if (pos.POS_RTQueryStatus(mStatusType2, 2, 1000, 2)) {
                    if ((mStatusType2[0] & 4) == 4) {
                        Log.d("====type2====", com.demosdk.Printer.PrintsConfigs.ResultCodeToString(-6));
                        status.setIsCoverOpen(true);
                        status.setCoverOpenMsg(com.demosdk.Printer.PrintsConfigs.ResultCodeToString(-6));
                    } else {
                        Log.d("====type2====", "Cover is in correct place, good!");
                        status.setIsCoverOpen(false);
                        status.setCoverOpenMsg("Cover is in correct place, good!");
                    }

                    if ((mStatusType2[0] & 32) == 32) {
                        Log.d("====type2====", com.demosdk.Printer.PrintsConfigs.ResultCodeToString(-5));
                        status.setIsOutOfPaper(true);
                        status.setOutOfPaperMsg(com.demosdk.Printer.PrintsConfigs.ResultCodeToString(-5));
                    } else {
                        Log.d("====type2====", "The printer still has paper, good !");
                        status.setIsOutOfPaper(false);
                        status.setOutOfPaperMsg("The printer still has paper, good !");
                    }
                } else {
                    status.setConnectionFailed(true);
                    status.setConnectionFailedMsg(com.demosdk.Printer.PrintsConfigs.ResultCodeToString(-11));
                }

                if (pos.POS_RTQueryStatus(mStatusType3, 3, 1000, 2)) {
                    if ((mStatusType3[0] & 8) == 8) {
                        Log.d("====type3====", com.demosdk.Printer.PrintsConfigs.ResultCodeToString(-2));
                        status.setIsCutterAbnormal(true);
                        status.setCutterAbnormalMsg(com.demosdk.Printer.PrintsConfigs.ResultCodeToString(-2));
                    } else {
                        Log.d("====type3====", "The cutter is normal, good !");
                        status.setIsCutterAbnormal(false);
                        status.setCutterAbnormalMsg("The cutter is normal, good !");
                    }

                    if ((mStatusType3[0] & 64) == 64) {
                        Log.d("====type3====", com.demosdk.Printer.PrintsConfigs.ResultCodeToString(-3));
                        status.setIsPrinterOverHeated(true);
                        status.setPrinterOverHeatedMsg(com.demosdk.Printer.PrintsConfigs.ResultCodeToString(-3));
                    } else {
                        Log.d("====type3====", "The print head is not overheated, good !");
                        status.setIsPrinterOverHeated(false);
                        status.setPrinterOverHeatedMsg("The print head is not overheated, good !");
                    }
                } else {
                    status.setConnectionFailed(true);
                    status.setConnectionFailedMsg(com.demosdk.Printer.PrintsConfigs.ResultCodeToString(-11));
                }

                if (pos.POS_RTQueryStatus(mStatusType4, 4, 1000, 2)) {
                    if ((mStatusType4[0] & 8) == 8) {
                        if (pos.POS_RTQueryStatus(mStatusType1, 1, 1000, 2)) {
                            if ((mStatusType1[0] & 128) == 128) {
                                Log.d("====type41====", com.demosdk.Printer.PrintsConfigs.ResultCodeToString(2));
                                status.setIsWarningAboutPaper(true);
                                status.setWarningAboutPaperMsg(com.demosdk.Printer.PrintsConfigs.ResultCodeToString(2));
                            } else {
                                Log.d("====type41====", com.demosdk.Printer.PrintsConfigs.ResultCodeToString(1));
                                status.setIsWarningAboutPaper(true);
                                status.setWarningAboutPaperMsg(com.demosdk.Printer.PrintsConfigs.ResultCodeToString(1));
                            }
                        }
                    } else if (pos.POS_RTQueryStatus(mStatusType1, 1, 1000, 2)) {
                        if ((mStatusType1[0] & 128) == 128) {
                            Log.d("====type41====", com.demosdk.Printer.PrintsConfigs.ResultCodeToString(3));
                            status.setIsWarningAboutPaper(true);
                            status.setWarningAboutPaperMsg(com.demosdk.Printer.PrintsConfigs.ResultCodeToString(3));
                        } else {
                            Log.d("====type41====", com.demosdk.Printer.PrintsConfigs.ResultCodeToString(0));
                        }

                        status.setIsWarningAboutPaper(false);
                        status.setWarningAboutPaperMsg(com.demosdk.Printer.PrintsConfigs.ResultCodeToString(0));
                    } else {
                        Log.d("====type41====", com.demosdk.Printer.PrintsConfigs.ResultCodeToString(-11));
                        status.setConnectionFailed(true);
                        status.setConnectionFailedMsg(com.demosdk.Printer.PrintsConfigs.ResultCodeToString(-11));
                    }
                } else {
                    Log.d("====type41====", com.demosdk.Printer.PrintsConfigs.ResultCodeToString(-11));
                    status.setConnectionFailed(true);
                    status.setConnectionFailedMsg(com.demosdk.Printer.PrintsConfigs.ResultCodeToString(-11));
                }
            }
        }

        return status;
    }

    public com.demosdk.Printer.SDKPrints.PrintStatus getStatus(int vendorId, int productId, Context context, com.demosdk.Printer.POSCustomed pos, USBPrinting usb) {
        UsbManager mUsbManager = (UsbManager)context.getSystemService("usb");
        com.demosdk.Printer.SDKPrints.PrintStatus status = new com.demosdk.Printer.SDKPrints.PrintStatus();
        HashMap<String, UsbDevice> deviceList = mUsbManager.getDeviceList();
        Iterator<UsbDevice> deviceIterator = deviceList.values().iterator();

        while(deviceIterator.hasNext()) {
            UsbDevice device = (UsbDevice)deviceIterator.next();
            if (device.getVendorId() == vendorId && device.getProductId() == productId && usb.Open(mUsbManager, device, context)) {
                byte[] mStatusType1 = new byte[1];
                byte[] mStatusType2 = new byte[1];
                byte[] mStatusType3 = new byte[1];
                byte[] mStatusType4 = new byte[1];
                if (pos.POS_RTQueryStatus(mStatusType2, 2, 1000, 2)) {
                    if ((mStatusType2[0] & 4) == 4) {
                        Log.d("====type2====", com.demosdk.Printer.PrintsConfigs.ResultCodeToString(-6));
                        status.setIsCoverOpen(true);
                        status.setCoverOpenMsg(com.demosdk.Printer.PrintsConfigs.ResultCodeToString(-6));
                    } else {
                        Log.d("====type2====", "Cover is in correct place, good!");
                        status.setIsCoverOpen(false);
                        status.setCoverOpenMsg("Cover is in correct place, good!");
                    }

                    if ((mStatusType2[0] & 32) == 32) {
                        Log.d("====type2====", com.demosdk.Printer.PrintsConfigs.ResultCodeToString(-5));
                        status.setIsOutOfPaper(true);
                        status.setOutOfPaperMsg(com.demosdk.Printer.PrintsConfigs.ResultCodeToString(-5));
                    } else {
                        Log.d("====type2====", "The printer still has paper, good !");
                        status.setIsOutOfPaper(false);
                        status.setOutOfPaperMsg("The printer still has paper, good !");
                    }
                } else {
                    status.setConnectionFailed(true);
                    status.setConnectionFailedMsg(com.demosdk.Printer.PrintsConfigs.ResultCodeToString(-11));
                }

                if (pos.POS_RTQueryStatus(mStatusType3, 3, 1000, 2)) {
                    if ((mStatusType3[0] & 8) == 8) {
                        Log.d("====type3====", com.demosdk.Printer.PrintsConfigs.ResultCodeToString(-2));
                        status.setIsCutterAbnormal(true);
                        status.setCutterAbnormalMsg(com.demosdk.Printer.PrintsConfigs.ResultCodeToString(-2));
                    } else {
                        Log.d("====type3====", "The cutter is normal, good !");
                        status.setIsCutterAbnormal(false);
                        status.setCutterAbnormalMsg("The cutter is normal, good !");
                    }

                    if ((mStatusType3[0] & 64) == 64) {
                        Log.d("====type3====", com.demosdk.Printer.PrintsConfigs.ResultCodeToString(-3));
                        status.setIsPrinterOverHeated(true);
                        status.setPrinterOverHeatedMsg(com.demosdk.Printer.PrintsConfigs.ResultCodeToString(-3));
                    } else {
                        Log.d("====type3====", "The print head is not overheated, good !");
                        status.setIsPrinterOverHeated(false);
                        status.setPrinterOverHeatedMsg("The print head is not overheated, good !");
                    }
                } else {
                    status.setConnectionFailed(true);
                    status.setConnectionFailedMsg(com.demosdk.Printer.PrintsConfigs.ResultCodeToString(-11));
                }

                if (pos.POS_RTQueryStatus(mStatusType4, 4, 1000, 2)) {
                    if ((mStatusType4[0] & 8) == 8) {
                        if (pos.POS_RTQueryStatus(mStatusType1, 1, 1000, 2)) {
                            if ((mStatusType1[0] & 128) == 128) {
                                Log.d("====type41====", com.demosdk.Printer.PrintsConfigs.ResultCodeToString(2));
                                status.setIsWarningAboutPaper(true);
                                status.setWarningAboutPaperMsg(com.demosdk.Printer.PrintsConfigs.ResultCodeToString(2));
                            } else {
                                Log.d("====type41====", com.demosdk.Printer.PrintsConfigs.ResultCodeToString(1));
                                status.setIsWarningAboutPaper(true);
                                status.setWarningAboutPaperMsg(com.demosdk.Printer.PrintsConfigs.ResultCodeToString(1));
                            }
                        }
                    } else if (pos.POS_RTQueryStatus(mStatusType1, 1, 1000, 2)) {
                        if ((mStatusType1[0] & 128) == 128) {
                            Log.d("====type41====", com.demosdk.Printer.PrintsConfigs.ResultCodeToString(3));
                            status.setIsWarningAboutPaper(true);
                            status.setWarningAboutPaperMsg(com.demosdk.Printer.PrintsConfigs.ResultCodeToString(3));
                        } else {
                            Log.d("====type41====", com.demosdk.Printer.PrintsConfigs.ResultCodeToString(0));
                        }

                        status.setIsWarningAboutPaper(false);
                        status.setWarningAboutPaperMsg(com.demosdk.Printer.PrintsConfigs.ResultCodeToString(0));
                    } else {
                        Log.d("====type41====", com.demosdk.Printer.PrintsConfigs.ResultCodeToString(-11));
                        status.setConnectionFailed(true);
                        status.setConnectionFailedMsg(com.demosdk.Printer.PrintsConfigs.ResultCodeToString(-11));
                    }
                } else {
                    Log.d("====type41====", com.demosdk.Printer.PrintsConfigs.ResultCodeToString(-11));
                    status.setConnectionFailed(true);
                    status.setConnectionFailedMsg(com.demosdk.Printer.PrintsConfigs.ResultCodeToString(-11));
                }
            }
        }

        return status;
    }

    public com.demosdk.Printer.SDKPrints.PrintStatus getStatus(int vendorId, int productId, int deviceId, Context context, com.demosdk.Printer.POSCustomed pos, USBPrinting usb) {
        UsbManager mUsbManager = (UsbManager)context.getSystemService("usb");
        com.demosdk.Printer.SDKPrints.PrintStatus status = new com.demosdk.Printer.SDKPrints.PrintStatus();
        HashMap<String, UsbDevice> deviceList = mUsbManager.getDeviceList();
        Iterator<UsbDevice> deviceIterator = deviceList.values().iterator();

        while(deviceIterator.hasNext()) {
            UsbDevice device = (UsbDevice)deviceIterator.next();
            if (device.getVendorId() == vendorId && device.getProductId() == productId && Utils.compareTwoDeviceId(device.getDeviceId(), deviceId) && usb.Open(mUsbManager, device, context)) {
                byte[] mStatusType1 = new byte[1];
                byte[] mStatusType2 = new byte[1];
                byte[] mStatusType3 = new byte[1];
                byte[] mStatusType4 = new byte[1];
                if (pos.POS_RTQueryStatus(mStatusType2, 2, 1000, 2)) {
                    if ((mStatusType2[0] & 4) == 4) {
                        Log.d("====type2====", com.demosdk.Printer.PrintsConfigs.ResultCodeToString(-6));
                        status.setIsCoverOpen(true);
                        status.setCoverOpenMsg(com.demosdk.Printer.PrintsConfigs.ResultCodeToString(-6));
                    } else {
                        Log.d("====type2====", "Cover is in correct place, good!");
                        status.setIsCoverOpen(false);
                        status.setCoverOpenMsg("Cover is in correct place, good!");
                    }

                    if ((mStatusType2[0] & 32) == 32) {
                        Log.d("====type2====", com.demosdk.Printer.PrintsConfigs.ResultCodeToString(-5));
                        status.setIsOutOfPaper(true);
                        status.setOutOfPaperMsg(com.demosdk.Printer.PrintsConfigs.ResultCodeToString(-5));
                    } else {
                        Log.d("====type2====", "The printer still has paper, good !");
                        status.setIsOutOfPaper(false);
                        status.setOutOfPaperMsg("The printer still has paper, good !");
                    }
                } else {
                    status.setConnectionFailed(true);
                    status.setConnectionFailedMsg(com.demosdk.Printer.PrintsConfigs.ResultCodeToString(-11));
                }

                if (pos.POS_RTQueryStatus(mStatusType3, 3, 1000, 2)) {
                    if ((mStatusType3[0] & 8) == 8) {
                        Log.d("====type3====", com.demosdk.Printer.PrintsConfigs.ResultCodeToString(-2));
                        status.setIsCutterAbnormal(true);
                        status.setCutterAbnormalMsg(com.demosdk.Printer.PrintsConfigs.ResultCodeToString(-2));
                    } else {
                        Log.d("====type3====", "The cutter is normal, good !");
                        status.setIsCutterAbnormal(false);
                        status.setCutterAbnormalMsg("The cutter is normal, good !");
                    }

                    if ((mStatusType3[0] & 64) == 64) {
                        Log.d("====type3====", com.demosdk.Printer.PrintsConfigs.ResultCodeToString(-3));
                        status.setIsPrinterOverHeated(true);
                        status.setPrinterOverHeatedMsg(com.demosdk.Printer.PrintsConfigs.ResultCodeToString(-3));
                    } else {
                        Log.d("====type3====", "The print head is not overheated, good !");
                        status.setIsPrinterOverHeated(false);
                        status.setPrinterOverHeatedMsg("The print head is not overheated, good !");
                    }
                } else {
                    status.setConnectionFailed(true);
                    status.setConnectionFailedMsg(com.demosdk.Printer.PrintsConfigs.ResultCodeToString(-11));
                }

                if (pos.POS_RTQueryStatus(mStatusType4, 4, 1000, 2)) {
                    if ((mStatusType4[0] & 8) == 8) {
                        if (pos.POS_RTQueryStatus(mStatusType1, 1, 1000, 2)) {
                            if ((mStatusType1[0] & 128) == 128) {
                                Log.d("====type41====", com.demosdk.Printer.PrintsConfigs.ResultCodeToString(2));
                                status.setIsWarningAboutPaper(true);
                                status.setWarningAboutPaperMsg(com.demosdk.Printer.PrintsConfigs.ResultCodeToString(2));
                            } else {
                                Log.d("====type41====", com.demosdk.Printer.PrintsConfigs.ResultCodeToString(1));
                                status.setIsWarningAboutPaper(true);
                                status.setWarningAboutPaperMsg(com.demosdk.Printer.PrintsConfigs.ResultCodeToString(1));
                            }
                        }
                    } else if (pos.POS_RTQueryStatus(mStatusType1, 1, 1000, 2)) {
                        if ((mStatusType1[0] & 128) == 128) {
                            Log.d("====type41====", com.demosdk.Printer.PrintsConfigs.ResultCodeToString(3));
                            status.setIsWarningAboutPaper(true);
                            status.setWarningAboutPaperMsg(com.demosdk.Printer.PrintsConfigs.ResultCodeToString(3));
                        } else {
                            Log.d("====type41====", com.demosdk.Printer.PrintsConfigs.ResultCodeToString(0));
                        }

                        status.setIsWarningAboutPaper(false);
                        status.setWarningAboutPaperMsg(com.demosdk.Printer.PrintsConfigs.ResultCodeToString(0));
                    } else {
                        Log.d("====type41====", com.demosdk.Printer.PrintsConfigs.ResultCodeToString(-11));
                        status.setConnectionFailed(true);
                        status.setConnectionFailedMsg(com.demosdk.Printer.PrintsConfigs.ResultCodeToString(-11));
                    }
                } else {
                    Log.d("====type41====", com.demosdk.Printer.PrintsConfigs.ResultCodeToString(-11));
                    status.setConnectionFailed(true);
                    status.setConnectionFailedMsg(com.demosdk.Printer.PrintsConfigs.ResultCodeToString(-11));
                }
            }
        }

        return status;
    }

    public static void print(int vendorId, int productId, int deviceId, com.demosdk.Printer.PrintsConfigs printsConfigs, List<Map<String, String>> commodityList, Map<String, String> total, String kioskId, String receiptNumber, Context context,String hotline , String email, com.demosdk.Printer.POSCustomed pos, USBPrinting usb, String lang) {
        UsbManager mUsbManager = (UsbManager)context.getSystemService("usb");
        HashMap<String, UsbDevice> deviceList = mUsbManager.getDeviceList();
        Iterator<UsbDevice> deviceIterator = deviceList.values().iterator();
        PendingIntent mPermissionIntent = PendingIntent.getBroadcast(context, 0, new Intent(context.getApplicationInfo().packageName), 0);

        while(deviceIterator.hasNext()) {
            UsbDevice device = (UsbDevice)deviceIterator.next();
            if (device.getVendorId() == vendorId && device.getProductId() == productId && Utils.compareTwoDeviceId(device.getDeviceId(), deviceId)) {
                if (!mUsbManager.hasPermission(device)) {
                    mUsbManager.requestPermission(device, mPermissionIntent);
                    Toast.makeText(context.getApplicationContext(), "Permission denied", 1).show();
                } else {
                    com.demosdk.Printer.SDKPrints.PrintOps printOps = new com.demosdk.Printer.SDKPrints.PrintOps(pos, usb, mUsbManager, device, context, printsConfigs, commodityList, total, kioskId, receiptNumber, lang,hotline,email);
                    printOps.run();
                }
            }
        }

    }

    public static void print(int vendorId, int productId, com.demosdk.Printer.PrintsConfigs printsConfigs, List<Map<String, String>> commodityList, Map<String, String> total, String kioskId, String receiptNumber, Context context,String hotline , String email, com.demosdk.Printer.POSCustomed pos, USBPrinting usb, String lang) {
        UsbManager mUsbManager = (UsbManager)context.getSystemService("usb");
        HashMap<String, UsbDevice> deviceList = mUsbManager.getDeviceList();
        Iterator<UsbDevice> deviceIterator = deviceList.values().iterator();
        PendingIntent mPermissionIntent = PendingIntent.getBroadcast(context, 0, new Intent(context.getApplicationInfo().packageName), 0);

        while(deviceIterator.hasNext()) {
            UsbDevice device = (UsbDevice)deviceIterator.next();
            if (device.getVendorId() == vendorId && device.getProductId() == productId) {
                if (!mUsbManager.hasPermission(device)) {
                    mUsbManager.requestPermission(device, mPermissionIntent);
                    Toast.makeText(context.getApplicationContext(), "Permission denied", 1).show();
                } else {
                    com.demosdk.Printer.SDKPrints.PrintOps printOps = new com.demosdk.Printer.SDKPrints.PrintOps(pos, usb, mUsbManager, device, context, printsConfigs, commodityList, total, kioskId, receiptNumber, lang,hotline,email);
                    printOps.run();
                }
            }
        }

    }

    public void print(int deviceId, com.demosdk.Printer.PrintsConfigs printsConfigs, List<Map<String, String>> commodityList, Map<String, String> total, String kioskId, String receiptNumber, Context context, com.demosdk.Printer.POSCustomed pos, USBPrinting usb, String lang,String hotline,String email) {
        UsbManager mUsbManager = (UsbManager)context.getSystemService("usb");
        HashMap<String, UsbDevice> deviceList = mUsbManager.getDeviceList();
        Iterator<UsbDevice> deviceIterator = deviceList.values().iterator();
        PendingIntent mPermissionIntent = PendingIntent.getBroadcast(context, 0, new Intent(context.getApplicationInfo().packageName), 0);

        while(deviceIterator.hasNext()) {
            UsbDevice device = (UsbDevice)deviceIterator.next();
            if (Utils.compareTwoDeviceId(device.getDeviceId(), deviceId)) {
                if (!mUsbManager.hasPermission(device)) {
                    mUsbManager.requestPermission(device, mPermissionIntent);
                    Toast.makeText(context.getApplicationContext(), "Permission denied", 1).show();
                } else {
                    com.demosdk.Printer.SDKPrints.PrintOps printOps = new com.demosdk.Printer.SDKPrints.PrintOps(pos, usb, mUsbManager, device, context, printsConfigs, commodityList, total, kioskId, receiptNumber, lang,hotline,email);
                    printOps.run();
                }
            }
        }

    }

    public static class PrintOps {
        com.demosdk.Printer.POSCustomed pos = null;
        USBPrinting usb = null;
        UsbManager usbManager = null;
        UsbDevice usbDevice = null;
        Context context = null;
        com.demosdk.Printer.PrintsConfigs printConfigs = new com.demosdk.Printer.PrintsConfigs();
        List<Map<String, String>> commodityList ;
        Map<String, String> total ;
        String kioskId ;
        String receiptNumber ;
        String lang ;
        String hotline;
        String email;

        public PrintOps(com.demosdk.Printer.POSCustomed pos, USBPrinting usb, UsbManager usbManager, UsbDevice usbDevice, Context context, com.demosdk.Printer.PrintsConfigs printConfigs, List<Map<String, String>> commodityList, Map<String, String> total, String kioskId, String receiptNumber, String lang,String hotline , String email) {
            this.pos = pos;
            this.usb = usb;
            this.usbManager = usbManager;
            this.usbDevice = usbDevice;
            this.context = context;
            this.lang = lang;
            this.hotline = hotline ;
            this.email = email;
            if (commodityList != null) {
                this.commodityList = commodityList;
            }

            if (total != null) {
                this.total = total;
            }

            if (kioskId != null) {
                this.kioskId = kioskId;
            }

            if (receiptNumber != null) {
                this.receiptNumber = receiptNumber;
            }

            if (printConfigs != null) {
                this.printConfigs = printConfigs;
            }

            pos.Set(usb);
        }

        public void run() {
            if (this.usb.Open(this.usbManager, this.usbDevice, this.context)) {
                if (this.printConfigs.printTemplate.equals("text")) {
                    com.demosdk.Printer.SDKPrints.bPrintResult = (new com.demosdk.Printer.PrintTemplateText(this.commodityList, this.total, this.kioskId, this.receiptNumber, this.lang,this.hotline,this.email)).Print(this.context.getApplicationContext(), this.pos, this.printConfigs.nPrintWidth, this.printConfigs.bCutter, this.printConfigs.bDrawer, this.printConfigs.bBeeper, this.printConfigs.nPrintCount, this.printConfigs.nPrintContent, this.printConfigs.nCompressMethod);
                }

                boolean bIsOpened = this.pos.GetIO().IsOpened();
                if (bIsOpened) {
                    SDKPrints.es.submit(new com.demosdk.Printer.SDKPrints.TaskClose(this.usb));
                }
            } else {
                com.demosdk.Printer.SDKPrints.bPrintResult = -14;
            }

        }

        public void close() {
            this.usb.Close();
        }
    }

    public static class TaskClose implements Runnable {
        USBPrinting usb = null;

        public TaskClose(USBPrinting usb) {
            this.usb = usb;
        }

        public void run() {
            this.usb.Close();
        }
    }

    public static class TaskPrint implements Runnable {
        com.demosdk.Printer.POSCustomed pos = null;
        USBPrinting usb = null;
        UsbManager usbManager = null;
        UsbDevice usbDevice = null;
        Context context = null;
        com.demosdk.Printer.PrintsConfigs printConfigs = new com.demosdk.Printer.PrintsConfigs();
        List<Map<String, String>> commodityList = Arrays.asList(com.demosdk.Printer.PrintsConfigs.getCommodityTest("Sim 4G", "1", "69,000"), com.demosdk.Printer.PrintsConfigs.getCommodityTest("Sim 4G", "1", "69,000"), com.demosdk.Printer.PrintsConfigs.getCommodityTest("Sim Roaming", "1", "69,000"), com.demosdk.Printer.PrintsConfigs.getCommodityTest("Sim Roaming", "1", "69,000"), com.demosdk.Printer.PrintsConfigs.getCommodityTest("Sim 4G", "1", "69,000"));
        Map<String, String> total = new HashMap<String, String>() {
            {
                this.put("price", "276,000");
                this.put("quantity", "4");
                this.put("serials", "1232131231233;123213123");
            }
        };
        String kioskId = "12345678910";
        String receiptNumber = "22211122121";
        String lang = "vi";
        String hotline ;
        String email;

        public TaskPrint(POSCustomed pos, USBPrinting usb, UsbManager usbManager, UsbDevice usbDevice, Context context, PrintsConfigs printConfigs, List<Map<String, String>> commodityList, Map<String, String> total, String kioskId, String receiptNumber, String lang,String hotline , String email) {
            this.pos = pos;
            this.usb = usb;
            this.usbManager = usbManager;
            this.usbDevice = usbDevice;
            this.context = context;
            this.lang = lang;
            this.email = email;
            this.hotline = hotline;
            if (commodityList != null) {
                this.commodityList = commodityList;
            }

            if (total != null) {
                this.total = total;
            }

            if (kioskId != null) {
                this.kioskId = kioskId;
            }

            if (receiptNumber != null) {
                this.receiptNumber = receiptNumber;
            }

            if (printConfigs != null) {
                this.printConfigs = printConfigs;
            }

            pos.Set(usb);
        }

        public void run() {
            if (this.usb.Open(this.usbManager, this.usbDevice, this.context)) {
                if (this.printConfigs.printTemplate.equals("text")) {
                    com.demosdk.Printer.SDKPrints.bPrintResult = (new PrintTemplateText(this.commodityList, this.total, this.kioskId, this.receiptNumber, this.lang,this.hotline, this.email)).Print(this.context.getApplicationContext(), this.pos, this.printConfigs.nPrintWidth, this.printConfigs.bCutter, this.printConfigs.bDrawer, this.printConfigs.bBeeper, this.printConfigs.nPrintCount, this.printConfigs.nPrintContent, this.printConfigs.nCompressMethod);
                }

                boolean bIsOpened = this.pos.GetIO().IsOpened();
                if (bIsOpened) {
                    com.demosdk.Printer.SDKPrints.es.submit(new SDKPrints.TaskClose(this.usb));
                }
            } else {
                com.demosdk.Printer.SDKPrints.bPrintResult = -14;
            }

        }
    }

    public class PrintStatus {
        boolean isOutOfPaper = false;
        boolean isCoverOpen = false;
        boolean isCutterAbnormal = false;
        boolean isPrinterOverHeated = false;
        boolean isWarningAboutPaper = false;
        boolean isConnectionFailed = false;
        String OutOfPaperMsg = "";
        String CoverOpenMsg = "";
        String CutterAbnormalMsg = "";
        String PrinterOverHeatedMsg = "";
        String WarningAboutPaperMsg = "";
        String ConnectionFailedMsg = "";

        public boolean isIsOutOfPaper() {
            return this.isOutOfPaper;
        }

        public void setIsOutOfPaper(boolean isOutOfPaper) {
            this.isOutOfPaper = isOutOfPaper;
        }

        public boolean isIsCoverOpen() {
            return this.isCoverOpen;
        }

        public void setIsCoverOpen(boolean isCoverOpen) {
            this.isCoverOpen = isCoverOpen;
        }

        public boolean isIsCutterAbnormal() {
            return this.isCutterAbnormal;
        }

        public void setIsCutterAbnormal(boolean isCutterAbnormal) {
            this.isCutterAbnormal = isCutterAbnormal;
        }

        public boolean isIsPrinterOverHeated() {
            return this.isPrinterOverHeated;
        }

        public void setIsPrinterOverHeated(boolean isPrinterOverHeated) {
            this.isPrinterOverHeated = isPrinterOverHeated;
        }

        public boolean isIsWarningAboutPaper() {
            return this.isWarningAboutPaper;
        }

        public void setIsWarningAboutPaper(boolean isWarningAboutPaper) {
            this.isWarningAboutPaper = isWarningAboutPaper;
        }

        public String getOutOfPaperMsg() {
            return this.OutOfPaperMsg;
        }

        public void setOutOfPaperMsg(String outOfPaperMsg) {
            this.OutOfPaperMsg = outOfPaperMsg;
        }

        public String getCoverOpenMsg() {
            return this.CoverOpenMsg;
        }

        public void setCoverOpenMsg(String coverOpenMsg) {
            this.CoverOpenMsg = coverOpenMsg;
        }

        public String getCutterAbnormalMsg() {
            return this.CutterAbnormalMsg;
        }

        public void setCutterAbnormalMsg(String cutterAbnormalMsg) {
            this.CutterAbnormalMsg = cutterAbnormalMsg;
        }

        public String getPrinterOverHeatedMsg() {
            return this.PrinterOverHeatedMsg;
        }

        public void setPrinterOverHeatedMsg(String printerOverHeatedMsg) {
            this.PrinterOverHeatedMsg = printerOverHeatedMsg;
        }

        public String getWarningAboutPaperMsg() {
            return this.WarningAboutPaperMsg;
        }

        public void setWarningAboutPaperMsg(String warningAboutPaperMsg) {
            this.WarningAboutPaperMsg = warningAboutPaperMsg;
        }

        public boolean isConnectionFailed() {
            return this.isConnectionFailed;
        }

        public void setConnectionFailed(boolean connectionFailed) {
            this.isConnectionFailed = connectionFailed;
        }

        public String getConnectionFailedMsg() {
            return this.ConnectionFailedMsg;
        }

        public void setConnectionFailedMsg(String connectionFailedMsg) {
            this.ConnectionFailedMsg = connectionFailedMsg;
        }

        public PrintStatus() {
        }
    }
}
