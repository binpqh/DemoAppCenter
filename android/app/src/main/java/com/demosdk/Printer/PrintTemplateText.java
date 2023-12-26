package com.demosdk.Printer;

import android.content.Context;
import android.graphics.Bitmap;
import asim.sdk.common.Utils;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


public class PrintTemplateText {
    List<Map<String, String>> commodityList;
    Map<String, String> total;
    String kioskId;
    String receiptNumber;
    String lang = "en";
    String hotline ;
    String email;

    public PrintTemplateText() {
    }

    public PrintTemplateText(List<Map<String, String>> commodityList, Map<String, String> total, String kioskId, String receiptNumber, String lang, String hotline , String email) {
        this.lang = lang;
        this.commodityList = commodityList;
        this.total = total;
        this.kioskId = kioskId;
        this.receiptNumber = receiptNumber;
        this.hotline = hotline;
        this.email = email;
    }

    public int Print(Context ctx, POSCustomed pos, int nPrintWidth, boolean bCutter, boolean bDrawer, boolean bBeeper, int nCount, int nPrintContent, int nCompressMethod) {
        int aPrintResult = -8;
        byte[] status = new byte[1];
        boolean bPrintResult ;
        if (!pos.POS_RTQueryStatus(status, 3, 1000, 2)) {
            bPrintResult = true;
            return -8;
        } else if ((status[0] & 8) == 8) {
            bPrintResult = true;
            return -2;
        } else if ((status[0] & 64) == 64) {
            bPrintResult = true;
            return -3;
        } else if (!pos.POS_RTQueryStatus(status, 2, 1000, 2)) {
            bPrintResult = true;
            return -8;
        } else if ((status[0] & 4) == 4) {
            bPrintResult = true;
            return -6;
        } else if ((status[0] & 32) == 32) {
            bPrintResult = true;
            return -5;
        } else {
            Bitmap headerReceipt = asim.sdk.printer.PrintTemplateImage.getImageFromAssetsFile(ctx, "asimHeadReceipt.png");
            Bitmap tieuDe = asim.sdk.printer.PrintTemplateImage.getImageFromAssetsFile(ctx, "tieuDe.png");
            Bitmap camOn = PrintTemplateImage.getImageFromAssetsFile(ctx, "camOn.png");

            for(int i = 0; i < nCount && pos.GetIO().IsOpened(); ++i) {
                if (nPrintContent >= 1) {
                    pos.POS_Reset();
                    pos.POS_FeedLine();
                    if (headerReceipt != null) {
                        pos.POS_PrintPicture(headerReceipt, nPrintWidth, 1, nCompressMethod);
                    }

                    pos.POS_TextOut("\r\n", 3, 24, 0, 0, 0, 0);
                    pos.POS_TextOut("\r\n", 3, 24, 0, 0, 0, 0);
                    pos.POS_TextOut("Tel: " + Utils.getLeftSpace(17, hotline.length()) + hotline+"| REP.NO: " + Utils.getLeftSpace(23 - " REP.NO: ".length(), this.receiptNumber.length()) + this.receiptNumber + "\r\n", 3, 0, 0, 0, 0, 0);
                    pos.POS_TextOut("Email: " + Utils.getLeftSpace(14, email.length()) +email +"| ID: " + Utils.getLeftSpace(23 - " ID: ".length(), this.kioskId.length()) + this.kioskId + "\r\n", 3, 0, 0, 0, 0, 0);
                    pos.POS_TextOut("----------------------------------------------\r\n", 3, 0, 0, 0, 0, 0);
                    pos.POS_TextOut(Utils.getLeftSpace(11, 0) + Utils.getCurrentTime() + "\r\n", 3, 0, 0, 0, 0, 0);
                    pos.POS_FeedLine();
                    pos.POS_FeedLine();
                    pos.POS_TextOut("----------------------------------------------\r\n", 3, 0, 0, 0, 0, 0);
                    if (this.lang.equals("en")) {
                        pos.POS_TextOut("Description          Qty           Amount (VND)\r\n", 3, 0, 0, 0, 0, 0);
                    } else if (tieuDe != null) {
                        pos.POS_PrintPicture(tieuDe, nPrintWidth, 1, nCompressMethod);
                    }

                    pos.POS_TextOut("----------------------------------------------\r\n", 3, 0, 0, 0, 0, 0);
                    Iterator var16 = this.commodityList.iterator();

                    while(var16.hasNext()) {
                        Map each = (Map)var16.next();
                        pos.POS_TextOut(each.get("description") + Utils.getLeftSpace(23, String.valueOf(each.get("description")).length()) + each.get("quantity") + Utils.getLeftSpace(22, String.valueOf(each.get("price")).length()) + each.get("price") + "\r\n", 3, 0, 0, 0, 0, 0);
                    }

                    pos.POS_TextOut("----------------------------------------------\r\n", 3, 0, 0, 0, 0, 0);
                    if (this.lang.equals("en")) {
                        pos.POS_TextOut("Total" + Utils.getLeftSpace(23, "Total".length()) + (String)this.total.get("quantity") + Utils.getLeftSpace(22, String.valueOf(this.total.get("price")).length()) + (String)this.total.get("price") + "\r\n", 3, 0, 0, 0, 0, 0);
                    } else {
                        pos.POS_TextOut("Total" + Utils.getLeftSpace(23, "Total".length()) + (String)this.total.get("quantity") + Utils.getLeftSpace(22, String.valueOf(this.total.get("price")).length()) + (String)this.total.get("price") + "\r\n", 3, 0, 0, 0, 0, 0);
                    }

                    pos.POS_FeedLine();
                    if (this.lang.equals("en")) {
                        pos.POS_TextOut(Utils.makeStringMiddleSpaces(46, "Thank you !"), 3, 0, 0, 0, 0, 0);
                    } else if (camOn != null) {
                        pos.POS_PrintPicture(camOn, nPrintWidth, 1, nCompressMethod);
                    }

                    pos.POS_TextOut("---------------------------------------------\r\n", 3, 0, 0, 0, 0, 0);
                    pos.POS_TextOut("\r\n", 3, 24, 0, 0, 0, 0);
                    pos.POS_TextOut("\r\n", 3, 24, 0, 0, 0, 0);
                    pos.POS_FeedLine();
                    pos.POS_FeedLine();
                    if (nPrintContent == 1 && nCount > 1) {
                        pos.POS_HalfCutPaper();

                        try {
                            Thread.currentThread();
                            Thread.sleep(4000L);
                        } catch (InterruptedException var29) {
                            var29.printStackTrace();
                        }
                    }
                }

                if (nPrintContent >= 2) {
                    if (nPrintContent == 2 && nCount > 1) {
                        pos.POS_HalfCutPaper();

                        try {
                            Thread.currentThread();
                            Thread.sleep(4500L);
                        } catch (InterruptedException var28) {
                            var28.printStackTrace();
                        }
                    }

                    if (nPrintContent == 2 && nCount == 1) {
                        if (bBeeper) {
                            pos.POS_Beep(1, 5);
                        }

                        if (bCutter) {
                            pos.POS_FullCutPaper();
                        }

                        if (bDrawer) {
                            pos.POS_KickDrawer(0, 100);
                        }

                        if (!pos.POS_RTQueryStatus(status, 1, 1000, 2)) {
                            bPrintResult = true;
                            return -11;
                        }

                        if ((status[0] & 128) != 128) {
                            try {
                                Thread.currentThread();
                                Thread.sleep(3000L);
                            } catch (InterruptedException var18) {
                                var18.printStackTrace();
                            }

                            if (pos.POS_RTQueryStatus(status, 2, 1000, 2)) {
                                if ((status[0] & 32) == 32) {
                                    bPrintResult = true;
                                    return -9;
                                }

                                if ((status[0] & 4) == 4) {
                                    bPrintResult = true;
                                    return -10;
                                }

                                bPrintResult = true;
                                return -1;
                            }

                            bPrintResult = true;
                            return -11;
                        }

                        try {
                            Thread.currentThread();
                            Thread.sleep(3000L);
                        } catch (InterruptedException var27) {
                            var27.printStackTrace();
                        }

                        if (!pos.POS_RTQueryStatus(status, 2, 1000, 2)) {
                            bPrintResult = true;
                            return -11;
                        }

                        if ((status[0] & 32) == 32) {
                            bPrintResult = true;
                            return -9;
                        }

                        if ((status[0] & 4) == 4) {
                            bPrintResult = true;
                            return -10;
                        }

                        if (!pos.POS_RTQueryStatus(status, 4, 1000, 2)) {
                            bPrintResult = true;
                            return -11;
                        }

                        if ((status[0] & 8) != 8) {
                            if (pos.POS_RTQueryStatus(status, 1, 1000, 2)) {
                                if ((status[0] & 128) == 128) {
                                    bPrintResult = true;
                                    return 3;
                                }

                                bPrintResult = false;
                                return 0;
                            }

                            bPrintResult = true;
                            return -11;
                        }

                        if (pos.POS_RTQueryStatus(status, 1, 1000, 2)) {
                            if ((status[0] & 128) == 128) {
                                bPrintResult = true;
                                return 2;
                            }

                            bPrintResult = true;
                            return 1;
                        }
                    }
                }

                if (nPrintContent >= 3) {
                    if (nPrintContent == 3 && nCount > 1) {
                        pos.POS_HalfCutPaper();

                        try {
                            Thread.currentThread();
                            Thread.sleep(6000L);
                        } catch (InterruptedException var26) {
                            var26.printStackTrace();
                        }
                    }

                    if (nPrintContent == 3 && nCount == 1) {
                        if (bBeeper) {
                            pos.POS_Beep(1, 5);
                        }

                        if (bCutter) {
                            pos.POS_FullCutPaper();
                        }

                        if (bDrawer) {
                            pos.POS_KickDrawer(0, 100);
                        }

                        if (!pos.POS_RTQueryStatus(status, 1, 1000, 2)) {
                            bPrintResult = true;
                            return -11;
                        }

                        if ((status[0] & 128) != 128) {
                            try {
                                Thread.currentThread();
                                Thread.sleep(3000L);
                            } catch (InterruptedException var19) {
                                var19.printStackTrace();
                            }

                            if (pos.POS_RTQueryStatus(status, 2, 1000, 2)) {
                                if ((status[0] & 32) == 32) {
                                    bPrintResult = true;
                                    return -9;
                                }

                                if ((status[0] & 4) == 4) {
                                    bPrintResult = true;
                                    return -10;
                                }

                                bPrintResult = true;
                                return -1;
                            }

                            bPrintResult = true;
                            return -11;
                        }

                        try {
                            Thread.currentThread();
                            Thread.sleep(3000L);
                        } catch (InterruptedException var25) {
                            var25.printStackTrace();
                        }

                        if (!pos.POS_RTQueryStatus(status, 2, 1000, 2)) {
                            bPrintResult = true;
                            return -11;
                        }

                        if ((status[0] & 32) == 32) {
                            bPrintResult = true;
                            return -9;
                        }

                        if ((status[0] & 4) == 4) {
                            bPrintResult = true;
                            return -10;
                        }

                        if (!pos.POS_RTQueryStatus(status, 4, 1000, 2)) {
                            bPrintResult = true;
                            return -11;
                        }

                        if ((status[0] & 8) != 8) {
                            if (pos.POS_RTQueryStatus(status, 1, 1000, 2)) {
                                if ((status[0] & 128) == 128) {
                                    bPrintResult = true;
                                    return 3;
                                }

                                bPrintResult = false;
                                return 0;
                            }

                            bPrintResult = true;
                            return -11;
                        }

                        if (pos.POS_RTQueryStatus(status, 1, 1000, 2)) {
                            if ((status[0] & 128) == 128) {
                                bPrintResult = true;
                                return 2;
                            }

                            bPrintResult = true;
                            return 1;
                        }
                    }
                }
            }

            if (bBeeper) {
                pos.POS_Beep(1, 5);
            }

            if (bCutter && nCount == 1) {
                pos.POS_HalfCutPaper();
            }

            if (bDrawer) {
                pos.POS_KickDrawer(0, 100);
            }

            if (nCount == 1) {
                try {
                    Thread.currentThread();
                    Thread.sleep(500L);
                } catch (InterruptedException var22) {
                    var22.printStackTrace();
                }

                if (!pos.POS_RTQueryStatus(status, 1, 1000, 2)) {
                    bPrintResult = true;
                    return -11;
                }

                if ((status[0] & 128) != 128) {
                    try {
                        Thread.currentThread();
                        Thread.sleep(3000L);
                    } catch (InterruptedException var20) {
                        var20.printStackTrace();
                    }

                    if (pos.POS_RTQueryStatus(status, 2, 1000, 2)) {
                        if ((status[0] & 32) == 32) {
                            bPrintResult = true;
                            return -9;
                        }

                        if ((status[0] & 4) == 4) {
                            bPrintResult = true;
                            return -10;
                        }

                        bPrintResult = true;
                        return -1;
                    }

                    bPrintResult = true;
                    return -11;
                }

                try {
                    Thread.currentThread();
                    Thread.sleep(3000L);
                } catch (InterruptedException var21) {
                    var21.printStackTrace();
                }

                if (!pos.POS_RTQueryStatus(status, 2, 1000, 2)) {
                    bPrintResult = true;
                    return -11;
                }

                if ((status[0] & 32) == 32) {
                    bPrintResult = true;
                    return -9;
                }

                if ((status[0] & 4) == 4) {
                    bPrintResult = true;
                    return -10;
                }

                if (!pos.POS_RTQueryStatus(status, 4, 1000, 2)) {
                    bPrintResult = true;
                    return -11;
                }

                if ((status[0] & 8) != 8) {
                    if (pos.POS_RTQueryStatus(status, 1, 1000, 2)) {
                        if ((status[0] & 128) == 128) {
                            bPrintResult = true;
                            return 3;
                        }

                        bPrintResult = false;
                        return 0;
                    }

                    bPrintResult = true;
                    return -11;
                }

                if (pos.POS_RTQueryStatus(status, 1, 1000, 2)) {
                    if ((status[0] & 128) == 128) {
                        bPrintResult = true;
                        return 2;
                    }

                    bPrintResult = true;
                    return 1;
                }
            } else {
                if (!pos.POS_RTQueryStatus(status, 1, 1000, 2)) {
                    bPrintResult = true;
                    return -11;
                }

                if ((status[0] & 128) != 128) {
                    try {
                        Thread.currentThread();
                        Thread.sleep(3000L);
                    } catch (InterruptedException var24) {
                        var24.printStackTrace();
                    }

                    if (pos.POS_RTQueryStatus(status, 2, 1000, 2)) {
                        if ((status[0] & 32) == 32) {
                            bPrintResult = true;
                            return -9;
                        }

                        if ((status[0] & 4) == 4) {
                            bPrintResult = true;
                            return -10;
                        }

                        bPrintResult = true;
                        return -1;
                    }

                    bPrintResult = true;
                    return -11;
                }

                try {
                    Thread.currentThread();
                    Thread.sleep(3000L);
                } catch (InterruptedException var23) {
                    var23.printStackTrace();
                }

                if (!pos.POS_RTQueryStatus(status, 2, 1000, 2)) {
                    bPrintResult = true;
                    return -11;
                }

                if ((status[0] & 32) == 32) {
                    bPrintResult = true;
                    return -9;
                }

                if ((status[0] & 4) == 4) {
                    bPrintResult = true;
                    return -10;
                }

                if (!pos.POS_RTQueryStatus(status, 4, 1000, 2)) {
                    bPrintResult = true;
                    return -11;
                }

                if ((status[0] & 8) != 8) {
                    if (pos.POS_RTQueryStatus(status, 1, 1000, 2)) {
                        if ((status[0] & 128) == 128) {
                            bPrintResult = true;
                            return 3;
                        }

                        bPrintResult = false;
                        return 0;
                    }

                    bPrintResult = true;
                    return -11;
                }

                if (pos.POS_RTQueryStatus(status, 1, 1000, 2)) {
                    if ((status[0] & 128) == 128) {
                        bPrintResult = true;
                        return 2;
                    }

                    bPrintResult = true;
                    return 1;
                }
            }

            return aPrintResult;
        }
    }
}
