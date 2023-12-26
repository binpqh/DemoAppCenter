package com.demosdk.Printer;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Bitmap.Config;
import com.lvrenyang.io.Pos;
import java.io.IOException;
import java.io.InputStream;

public class PrintTemplateImage {
    public PrintTemplateImage() {
    }

    public static int PrintTicket(Context ctx, Pos pos, int nPrintWidth, boolean bCutter, boolean bDrawer, boolean bBeeper, int nCount, int nPrintContent, int nCompressMethod) {
        int aPrintResult = -8;
        byte[] status = new byte[1];
        boolean bPrintResult;
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
            Bitmap bm1 = getTestImage1(nPrintWidth, nPrintWidth);
            Bitmap bm2 = getTestImage2(nPrintWidth, nPrintWidth);
            Bitmap bmBlackWhite = getImageFromAssetsFile(ctx, "blackwhite.png");
            Bitmap bmIu = getImageFromAssetsFile(ctx, "iu.jpeg");
            Bitmap bmYellowmen = getImageFromAssetsFile(ctx, "yellowmen.png");

            for(int i = 0; i < nCount && pos.GetIO().IsOpened(); ++i) {
                if (nPrintContent >= 1) {
                    pos.POS_Reset();
                    pos.POS_FeedLine();
                    pos.POS_TextOut("    ASIM Telecom JSC\r\n", 3, 0, 1, 0, 0, 0);
                    pos.POS_TextOut("\r\n", 3, 24, 0, 0, 0, 0);
                    pos.POS_TextOut("VPBank Building, 05 Dien Bien Phu Street,\r\n", 3, 0, 0, 0, 0, 0);
                    pos.POS_TextOut("Ba Dinh District, Hanoi.\r\n", 3, 0, 0, 0, 0, 0);
                    pos.POS_TextOut("Tel: 1900 1900 (ext 01)\r\n", 3, 0, 0, 0, 0, 0);
                    pos.POS_TextOut("Email: cskh@myLocal.vn\r\n", 3, 0, 0, 0, 0, 0);
                    pos.POS_TextOut("----------------------------------------------\r\n", 3, 0, 0, 0, 0, 0);
                    pos.POS_TextOut("Receipt No: 9874563210, KioskId: 12345678\r\n", 3, 0, 0, 0, 0, 0);
                    pos.POS_TextOut("Date: 18-Jan-2022 09:15:00\r\n", 3, 0, 0, 0, 0, 0);
                    pos.POS_TextOut("----------------------------------------------\r\n", 3, 0, 0, 0, 0, 0);
                    pos.POS_TextOut("Description         Qty           Amount (VND)\r\n", 3, 0, 0, 0, 0, 0);
                    pos.POS_TextOut("----------------------------------------------\r\n", 3, 0, 0, 0, 0, 0);
                    pos.POS_TextOut("Sim 4G               1                  69,000\r\n", 3, 0, 0, 0, 0, 0);
                    pos.POS_TextOut("Sim 4G               1                  69,000\r\n", 3, 0, 0, 0, 0, 0);
                    pos.POS_TextOut("Sim Roaming          1                  69,000\r\n", 3, 0, 0, 0, 0, 0);
                    pos.POS_TextOut("Sim Roaming          1                  69,000\r\n", 3, 0, 0, 0, 0, 0);
                    pos.POS_TextOut("----------------------------------------------\r\n", 3, 0, 0, 0, 0, 0);
                    pos.POS_TextOut("Total                4                 280,000\r\n", 3, 0, 0, 0, 0, 0);
                    pos.POS_FeedLine();
                    pos.POS_TextOut("                 Thank you !\r\n", 3, 0, 0, 0, 0, 0);
                    pos.POS_FeedLine();
                    pos.POS_S_SetQRcode("1234567890", 20, 4, 5);
                    pos.POS_FeedLine();
                    pos.POS_DoubleQRCode("dasdfsdfadf;asdfasdfasdf;asdfsdfadf", 20, 4, 5, "Mask: 1:210: Milk: 1:25", 230, 4, 5, 4);
                    pos.POS_FeedLine();
                    pos.POS_TextOut("-------------------------------------------------\r\n", 3, 24, 0, 0, 0, 0);
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
                    if (bm1 != null) {
                        pos.POS_PrintPicture(bm1, nPrintWidth, 1, nCompressMethod);
                    }

                    if (bm2 != null) {
                        pos.POS_PrintPicture(bm2, nPrintWidth, 1, nCompressMethod);
                    }

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
                    if (bmBlackWhite != null) {
                        pos.POS_PrintPicture(bmBlackWhite, nPrintWidth, 1, nCompressMethod);
                    }

                    if (bmIu != null) {
                        pos.POS_PrintPicture(bmIu, nPrintWidth, 0, nCompressMethod);
                    }

                    if (bmYellowmen != null) {
                        pos.POS_PrintPicture(bmYellowmen, nPrintWidth, 0, nCompressMethod);
                    }

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
                pos.POS_FullCutPaper();
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

    public static Bitmap getImageFromAssetsFile(Context ctx, String fileName) {
        Bitmap image = null;
        AssetManager am = ctx.getResources().getAssets();

        try {
            InputStream is = am.open(fileName);
            image = BitmapFactory.decodeStream(is);
            is.close();
        } catch (IOException var5) {
            var5.printStackTrace();
        }

        return image;
    }

    public static Bitmap resizeImage(Bitmap bitmap, int w, int h) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        float scaleWidth = (float)w / (float)width;
        float scaleHeight = (float)h / (float)height;
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        Bitmap resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true);
        return resizedBitmap;
    }

    public static Bitmap getTestImage1(int width, int height) {
        Bitmap bitmap = Bitmap.createBitmap(width, height, Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint();
        paint.setColor(-1);
        canvas.drawRect(0.0F, 0.0F, (float)width, (float)height, paint);
        paint.setColor(-16777216);

        for(int i = 0; i < 8; ++i) {
            for(int x = i; x < width; x += 8) {
                for(int y = i; y < height; y += 8) {
                    canvas.drawPoint((float)x, (float)y, paint);
                }
            }
        }

        return bitmap;
    }

    public static Bitmap getTestImage2(int width, int height) {
        Bitmap bitmap = Bitmap.createBitmap(width, height, Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint();
        paint.setColor(-1);
        canvas.drawRect(0.0F, 0.0F, (float)width, (float)height, paint);
        paint.setColor(-16777216);

        for(int y = 0; y < height; y += 4) {
            for(int x = y % 32; x < width; x += 32) {
                canvas.drawRect((float)x, (float)y, (float)(x + 4), (float)(y + 4), paint);
            }
        }

        return bitmap;
    }
}
