package com.demosdk.Printer;


import android.annotation.TargetApi;
import android.graphics.Bitmap;
import android.util.Log;
import asim.sdk.common.Utils;
import com.demosdk.Printer.ESCCmd;
import com.lvrenyang.io.ImageProcessing;
import com.lvrenyang.io.base.IO;
import compress_lib_api.CompressLib;
import java.nio.charset.StandardCharsets;

public class POSCustomed {
    private static final String TAG = "Pos";
    private IO IO = new IO();
    private com.demosdk.Printer.ESCCmd Cmd = new com.demosdk.Printer.ESCCmd();

    public POSCustomed() {
    }

    public void Set(IO io) {
        if (null != io) {
            this.IO = io;
        }

    }

    public IO GetIO() {
        return this.IO;
    }

    public boolean POS_PrintPicture(Bitmap mBitmap, int nWidth, int nBinaryAlgorithm, int nCompressMethod) {
        if (!this.IO.IsOpened()) {
            return false;
        } else {
            boolean ret = false;
            this.IO.mMainLocker.lock();

            try {
                int dstw = (nWidth + 7) / 8 * 8;
                int dsth = mBitmap.getHeight() * dstw / mBitmap.getWidth();
                int[] dst = new int[dstw * dsth];
                mBitmap = ImageProcessing.resizeImage(mBitmap, dstw, dsth);
                mBitmap.getPixels(dst, 0, dstw, 0, 0, dstw, dsth);
                byte[] gray = ImageProcessing.GrayImage(dst);
                boolean[] dithered = new boolean[dstw * dsth];
                if (nBinaryAlgorithm == 0) {
                    ImageProcessing.format_K_dither16x16(dstw, dsth, gray, dithered);
                } else {
                    ImageProcessing.format_K_threshold(dstw, dsth, gray, dithered);
                }

                byte[] data = null;
                //byte[] data ;
                switch (nCompressMethod) {
                    case 0:
                    default:
                        data = ImageProcessing.eachLinePixToCmd(dithered, dstw, 0);
                        break;
                    case 1:
                        data = ImageProcessing.eachLinePixToCompressCmd(dithered, dstw);
                        break;
                    case 2:
                        data = CompressLib.RasterImageToCompressCmd(dstw, dsth, ImageProcessing.Image1ToRasterData(dstw, dsth, dithered));
                }

                ret = this.IO.Write(data, 0, data.length) == data.length;
            } catch (Exception var15) {
                Log.i("Pos", var15.toString());
            } finally {
                this.IO.mMainLocker.unlock();
            }

            return ret;
        }
    }

    public boolean POS_S_TextOut(String pszString, int nOrgx, int nWidthTimes, int nHeightTimes, int nFontType, int nFontStyle) {
        if (!this.IO.IsOpened()) {
            return false;
        } else {
            boolean ret = false;
            this.IO.mMainLocker.lock();

            try {
                if (nOrgx > 65535 || nOrgx < 0 || nWidthTimes > 7 || nWidthTimes < 0 || nHeightTimes > 7 || nHeightTimes < 0 || nFontType < 0 || nFontType > 4 || pszString.length() == 0) {
                    throw new Exception("invalid args");
                }

                this.Cmd.ESC_dollors_nL_nH[2] = (byte)(nOrgx % 256);
                this.Cmd.ESC_dollors_nL_nH[3] = (byte)(nOrgx / 256);
                byte[] intToWidth = new byte[]{0, 16, 32, 48, 64, 80, 96, 112};
                byte[] intToHeight = new byte[]{0, 1, 2, 3, 4, 5, 6, 7};
                this.Cmd.GS_exclamationmark_n[2] = (byte)(intToWidth[nWidthTimes] + intToHeight[nHeightTimes]);
                byte[] tmp_ESC_M_n = this.Cmd.ESC_M_n;
                if (nFontType != 0 && nFontType != 1) {
                    tmp_ESC_M_n = new byte[0];
                } else {
                    tmp_ESC_M_n[2] = (byte)nFontType;
                }

                this.Cmd.GS_E_n[2] = (byte)(nFontStyle >> 3 & 1);
                this.Cmd.ESC_line_n[2] = (byte)(nFontStyle >> 7 & 3);
                this.Cmd.FS_line_n[2] = (byte)(nFontStyle >> 7 & 3);
                this.Cmd.ESC_lbracket_n[2] = (byte)(nFontStyle >> 9 & 1);
                this.Cmd.GS_B_n[2] = (byte)(nFontStyle >> 10 & 1);
                this.Cmd.ESC_V_n[2] = (byte)(nFontStyle >> 12 & 1);
                this.Cmd.ESC_9_n[2] = 1;
                byte[] pbString = pszString.getBytes();
                byte[] data = this.byteArraysToBytes(new byte[][]{this.Cmd.ESC_dollors_nL_nH, this.Cmd.GS_exclamationmark_n, tmp_ESC_M_n, this.Cmd.GS_E_n, this.Cmd.ESC_line_n, this.Cmd.FS_line_n, this.Cmd.ESC_lbracket_n, this.Cmd.GS_B_n, this.Cmd.ESC_V_n, this.Cmd.FS_AND, this.Cmd.ESC_9_n, pbString});
                ret = this.IO.Write(data, 0, data.length) == data.length;
            } catch (Exception var16) {
                Log.i("Pos", var16.toString());
            } finally {
                this.IO.mMainLocker.unlock();
            }

            return ret;
        }
    }

    @TargetApi(19)
    public boolean POS_TextOut(String pszString, int nLan, int nOrgx, int nWidthTimes, int nHeightTimes, int nFontType, int nFontStyle) {
        if (!this.IO.IsOpened()) {
            return false;
        } else {
            boolean ret = false;
            this.IO.mMainLocker.lock();

            try {
                if (nOrgx > 65535 || nOrgx < 0 || nWidthTimes > 7 || nWidthTimes < 0 || nHeightTimes > 7 || nHeightTimes < 0 || nFontType < 0 || nFontType > 4 || pszString.length() == 0) {
                    throw new Exception("invalid args");
                }

                this.Cmd.ESC_dollors_nL_nH[2] = (byte)(nOrgx % 256);
                this.Cmd.ESC_dollors_nL_nH[3] = (byte)(nOrgx / 256);
                byte[] intToWidth = new byte[]{0, 16, 32, 48, 64, 80, 96, 112};
                byte[] intToHeight = new byte[]{0, 1, 2, 3, 4, 5, 6, 7};
                this.Cmd.GS_exclamationmark_n[2] = (byte)(intToWidth[nWidthTimes] + intToHeight[nHeightTimes]);
                byte[] tmp_ESC_M_n = this.Cmd.ESC_M_n;
                if (nFontType != 0 && nFontType != 1) {
                    tmp_ESC_M_n = new byte[0];
                } else {
                    tmp_ESC_M_n[2] = (byte)nFontType;
                }

                this.Cmd.GS_E_n[2] = (byte)(nFontStyle >> 3 & 1);
                this.Cmd.ESC_line_n[2] = (byte)(nFontStyle >> 7 & 3);
                this.Cmd.FS_line_n[2] = (byte)(nFontStyle >> 7 & 3);
                this.Cmd.ESC_lbracket_n[2] = (byte)(nFontStyle >> 9 & 1);
                this.Cmd.GS_B_n[2] = (byte)(nFontStyle >> 10 & 1);
                this.Cmd.ESC_V_n[2] = (byte)(nFontStyle >> 12 & 1);
                byte[] pbString = null;
                //byte[] pbString;
                if (nLan == 0) {
                    this.Cmd.ESC_9_n[2] = 0;
                    pbString = pszString.getBytes("GBK");
                } else if (nLan == 3) {
                    this.Cmd.ESC_9_n[2] = 3;
                    pbString = pszString.getBytes("Big5");
                } else if (nLan == 4) {
                    this.Cmd.ESC_9_n[2] = 4;
                    pbString = pszString.getBytes("Shift_JIS");
                } else if (nLan == 5) {
                    this.Cmd.ESC_9_n[2] = 5;
                    pbString = pszString.getBytes("EUC-KR");
                } else {
                    this.Cmd.ESC_9_n[2] = 1;
                    pbString = StandardCharsets.UTF_8.encode(pszString).array();
                }

                String test = Utils.bytesToHex(pbString);
                Log.d("pszString===", pszString);
                Log.d("pbString===", test);
                byte[] data = this.byteArraysToBytes(new byte[][]{this.Cmd.ESC_dollors_nL_nH, this.Cmd.GS_exclamationmark_n, tmp_ESC_M_n, this.Cmd.GS_E_n, this.Cmd.ESC_line_n, this.Cmd.FS_line_n, this.Cmd.ESC_lbracket_n, this.Cmd.GS_B_n, this.Cmd.ESC_V_n, this.Cmd.FS_AND, this.Cmd.ESC_9_n, pbString});
                ret = this.IO.Write(data, 0, data.length) == data.length;
            } catch (Exception var18) {
                Log.i("Pos", var18.toString());
            } finally {
                this.IO.mMainLocker.unlock();
            }

            return ret;
        }
    }

    public boolean POS_FeedLine() {
        if (!this.IO.IsOpened()) {
            return false;
        } else {
            boolean ret = false;
            this.IO.mMainLocker.lock();

            try {
                byte[] data = this.byteArraysToBytes(new byte[][]{this.Cmd.CR, this.Cmd.LF});
                ret = this.IO.Write(data, 0, data.length) == data.length;
            } catch (Exception var6) {
                Log.i("Pos", var6.toString());
            } finally {
                this.IO.mMainLocker.unlock();
            }

            return ret;
        }
    }

    public boolean POS_S_Align(int align) {
        if (!this.IO.IsOpened()) {
            return false;
        } else {
            boolean ret = false;
            this.IO.mMainLocker.lock();

            try {
                if (align < 0 || align > 2) {
                    throw new Exception("invalid args");
                }

                byte[] data = this.Cmd.ESC_a_n;
                data[2] = (byte)align;
                ret = this.IO.Write(data, 0, data.length) == data.length;
            } catch (Exception var7) {
                Log.i("Pos", var7.toString());
            } finally {
                this.IO.mMainLocker.unlock();
            }

            return ret;
        }
    }

    public boolean POS_SetLineHeight(int nHeight) {
        if (!this.IO.IsOpened()) {
            return false;
        } else {
            boolean ret = false;
            this.IO.mMainLocker.lock();

            try {
                if (nHeight < 0 || nHeight > 255) {
                    throw new Exception("invalid args");
                }

                byte[] data = this.Cmd.ESC_3_n;
                data[2] = (byte)nHeight;
                ret = this.IO.Write(data, 0, data.length) == data.length;
            } catch (Exception var7) {
                Log.i("Pos", var7.toString());
            } finally {
                this.IO.mMainLocker.unlock();
            }

            return ret;
        }
    }

    public boolean POS_S_SetBarcode(String strCodedata, int nOrgx, int nType, int nWidthX, int nHeight, int nHriFontType, int nHriFontPosition) {
        if (!this.IO.IsOpened()) {
            return false;
        } else {
            boolean ret = false;
            this.IO.mMainLocker.lock();

            try {
                if (nOrgx < 0 || nOrgx > 65535 || nType < 65 || nType > 73 || nHeight < 1 || nHeight > 255) {
                    throw new Exception("invalid args");
                }

                byte[] bCodeData = strCodedata.getBytes();
                this.Cmd.ESC_dollors_nL_nH[2] = (byte)(nOrgx % 256);
                this.Cmd.ESC_dollors_nL_nH[3] = (byte)(nOrgx / 256);
                this.Cmd.GS_w_n[2] = (byte)nWidthX;
                this.Cmd.GS_h_n[2] = (byte)nHeight;
                this.Cmd.GS_f_n[2] = (byte)(nHriFontType & 1);
                this.Cmd.GS_H_n[2] = (byte)(nHriFontPosition & 3);
                this.Cmd.GS_k_m_n_[2] = (byte)nType;
                this.Cmd.GS_k_m_n_[3] = (byte)bCodeData.length;
                byte[] data = this.byteArraysToBytes(new byte[][]{this.Cmd.ESC_dollors_nL_nH, this.Cmd.GS_w_n, this.Cmd.GS_h_n, this.Cmd.GS_f_n, this.Cmd.GS_H_n, this.Cmd.GS_k_m_n_, bCodeData});
                ret = this.IO.Write(data, 0, data.length) == data.length;
            } catch (Exception var14) {
                Log.i("Pos", var14.toString());
            } finally {
                this.IO.mMainLocker.unlock();
            }

            return ret;
        }
    }

    public boolean POS_S_SetQRcode(String strCodedata, int nWidthX, int nVersion, int nErrorCorrectionLevel) {
        if (!this.IO.IsOpened()) {
            return false;
        } else {
            boolean ret = false;
            this.IO.mMainLocker.lock();

            try {
                if (nWidthX < 1 || nWidthX > 16 || nErrorCorrectionLevel < 1 || nErrorCorrectionLevel > 4 || nVersion < 0 || nVersion > 16) {
                    throw new Exception("invalid args");
                }

                byte[] bCodeData = strCodedata.getBytes();
                this.Cmd.GS_w_n[2] = (byte)nWidthX;
                this.Cmd.GS_k_m_v_r_nL_nH[3] = (byte)nVersion;
                this.Cmd.GS_k_m_v_r_nL_nH[4] = (byte)nErrorCorrectionLevel;
                this.Cmd.GS_k_m_v_r_nL_nH[5] = (byte)(bCodeData.length & 255);
                this.Cmd.GS_k_m_v_r_nL_nH[6] = (byte)((bCodeData.length & '\uff00') >> 8);
                byte[] data = this.byteArraysToBytes(new byte[][]{this.Cmd.GS_w_n, this.Cmd.GS_k_m_v_r_nL_nH, bCodeData});
                ret = this.IO.Write(data, 0, data.length) == data.length;
            } catch (Exception var11) {
                Log.i("Pos", var11.toString());
            } finally {
                this.IO.mMainLocker.unlock();
            }

            return ret;
        }
    }

    public boolean POS_DoubleQRCode(String QR1Data, int QR1Position, int QR1Ecc, int QR1Version, String QR2Data, int QR2Position, int QR2Ecc, int QR2Version, int ModuleSize) {
        if (!this.IO.IsOpened()) {
            return false;
        } else {
            boolean ret = false;
            this.IO.mMainLocker.lock();

            try {
                byte[] head = new byte[]{31, 81, 2, (byte)ModuleSize};
                byte[] qr1code = QR1Data.getBytes();
                int QR1Datalen = qr1code.length;
                byte[] qr1info = new byte[]{(byte)((QR1Position & '\uff00') >> 8), (byte)(QR1Position & 255), (byte)((QR1Datalen & '\uff00') >> 8), (byte)(QR1Datalen & 255), (byte)QR1Ecc, (byte)QR1Version};
                byte[] qr2code = QR2Data.getBytes();
                int QR2Datalen = qr2code.length;
                byte[] qr2info = new byte[]{(byte)((QR2Position & '\uff00') >> 8), (byte)(QR2Position & 255), (byte)((QR2Datalen & '\uff00') >> 8), (byte)(QR2Datalen & 255), (byte)QR2Ecc, (byte)QR2Version};
                byte[] data = this.byteArraysToBytes(new byte[][]{head, qr1info, qr1code, qr2info, qr2code});
                ret = this.IO.Write(data, 0, data.length) == data.length;
            } catch (Exception var22) {
                Log.i("Pos", var22.toString());
            } finally {
                this.IO.mMainLocker.unlock();
            }

            return ret;
        }
    }

    public boolean POS_EPSON_SetQRCode(String strCodedata, int nWidthX, int nErrorCorrectionLevel) {
        if (!this.IO.IsOpened()) {
            return false;
        } else {
            boolean ret = false;
            this.IO.mMainLocker.lock();

            try {
                if (nWidthX < 1 || nWidthX > 16 || nErrorCorrectionLevel < 1 || nErrorCorrectionLevel > 4) {
                    throw new Exception("invalid args");
                }

                byte[] bCodeData = strCodedata.getBytes();
                this.Cmd.GS_leftbracket_k_pL_pH_cn_67_n[7] = (byte)nWidthX;
                this.Cmd.GS_leftbracket_k_pL_pH_cn_69_n[7] = (byte)(47 + nErrorCorrectionLevel);
                this.Cmd.GS_leftbracket_k_pL_pH_cn_80_m__d1dk[3] = (byte)(bCodeData.length + 3 & 255);
                this.Cmd.GS_leftbracket_k_pL_pH_cn_80_m__d1dk[4] = (byte)((bCodeData.length + 3 & '\uff00') >> 8);
                byte[] data = this.byteArraysToBytes(new byte[][]{this.Cmd.GS_leftbracket_k_pL_pH_cn_67_n, this.Cmd.GS_leftbracket_k_pL_pH_cn_69_n, this.Cmd.GS_leftbracket_k_pL_pH_cn_80_m__d1dk, bCodeData, this.Cmd.GS_leftbracket_k_pL_pH_cn_fn_m});
                ret = this.IO.Write(data, 0, data.length) == data.length;
            } catch (Exception var10) {
                Log.i("Pos", var10.toString());
            } finally {
                this.IO.mMainLocker.unlock();
            }

            return ret;
        }
    }

    public boolean POS_Reset() {
        if (!this.IO.IsOpened()) {
            return false;
        } else {
            boolean ret = false;
            this.IO.mMainLocker.lock();

            try {
                byte[] data = this.Cmd.ESC_ALT;
                ret = this.IO.Write(data, 0, data.length) == data.length;
            } catch (Exception var6) {
                Log.i("Pos", var6.toString());
            } finally {
                this.IO.mMainLocker.unlock();
            }

            return ret;
        }
    }

    public boolean POS_SetMotionUnit(int nHorizontalMU, int nVerticalMU) {
        if (!this.IO.IsOpened()) {
            return false;
        } else {
            boolean ret = false;
            this.IO.mMainLocker.lock();

            try {
                if (nHorizontalMU < 0 || nHorizontalMU > 255 || nVerticalMU < 0 || nVerticalMU > 255) {
                    throw new Exception("invalid args");
                }

                byte[] data = this.Cmd.GS_P_x_y;
                data[2] = (byte)nHorizontalMU;
                data[3] = (byte)nVerticalMU;
                ret = this.IO.Write(data, 0, data.length) == data.length;
            } catch (Exception var8) {
                Log.i("Pos", var8.toString());
            } finally {
                this.IO.mMainLocker.unlock();
            }

            return ret;
        }
    }

    protected boolean POS_SetCharSetAndCodePage(int nCharSet, int nCodePage) {
        if (!this.IO.IsOpened()) {
            return false;
        } else {
            boolean ret = false;
            this.IO.mMainLocker.lock();

            try {
                if (nCharSet < 0 || nCharSet > 15 || nCodePage < 0 || nCodePage > 19 || nCodePage > 10 && nCodePage < 16) {
                    throw new Exception("invalid args");
                }

                this.Cmd.ESC_R_n[2] = (byte)nCharSet;
                this.Cmd.ESC_t_n[2] = (byte)nCodePage;
                byte[] data = this.byteArraysToBytes(new byte[][]{this.Cmd.ESC_R_n, this.Cmd.ESC_t_n});
                ret = this.IO.Write(data, 0, data.length) == data.length;
            } catch (Exception var8) {
                Log.i("Pos", var8.toString());
            } finally {
                this.IO.mMainLocker.unlock();
            }

            return ret;
        }
    }

    public boolean POS_SetRightSpacing(int nDistance) {
        if (!this.IO.IsOpened()) {
            return false;
        } else {
            boolean ret = false;
            this.IO.mMainLocker.lock();

            try {
                if (nDistance < 0 || nDistance > 255) {
                    throw new Exception("invalid args");
                }

                this.Cmd.ESC_SP_n[2] = (byte)nDistance;
                byte[] data = this.Cmd.ESC_SP_n;
                ret = this.IO.Write(data, 0, data.length) == data.length;
            } catch (Exception var7) {
                Log.i("Pos", var7.toString());
            } finally {
                this.IO.mMainLocker.unlock();
            }

            return ret;
        }
    }

    public boolean POS_S_SetAreaWidth(int nWidth) {
        if (!this.IO.IsOpened()) {
            return false;
        } else {
            boolean ret = false;
            this.IO.mMainLocker.lock();

            try {
                if (nWidth < 0 || nWidth > 65535) {
                    throw new Exception("invalid args");
                }

                byte nL = (byte)(nWidth % 256);
                byte nH = (byte)(nWidth / 256);
                this.Cmd.GS_W_nL_nH[2] = nL;
                this.Cmd.GS_W_nL_nH[3] = nH;
                byte[] data = this.Cmd.GS_W_nL_nH;
                ret = this.IO.Write(data, 0, data.length) == data.length;
            } catch (Exception var9) {
                Log.i("Pos", var9.toString());
            } finally {
                this.IO.mMainLocker.unlock();
            }

            return ret;
        }
    }

    public boolean POS_SetDoubleByteMode() {
        if (!this.IO.IsOpened()) {
            return false;
        } else {
            boolean ret = false;
            this.IO.mMainLocker.lock();

            try {
                byte[] data = new byte[]{28, 38};
                ret = this.IO.Write(data, 0, data.length) == data.length;
            } catch (Exception var6) {
                Log.i("Pos", var6.toString());
            } finally {
                this.IO.mMainLocker.unlock();
            }

            return ret;
        }
    }

    public boolean POS_SetSingleByteMode() {
        if (!this.IO.IsOpened()) {
            return false;
        } else {
            boolean ret = false;
            this.IO.mMainLocker.lock();

            try {
                byte[] data = new byte[]{28, 46};
                ret = this.IO.Write(data, 0, data.length) == data.length;
            } catch (Exception var6) {
                Log.i("Pos", var6.toString());
            } finally {
                this.IO.mMainLocker.unlock();
            }

            return ret;
        }
    }

    public boolean POS_SetDoubleByteEncoding(int nEncoding) {
        if (!this.IO.IsOpened()) {
            return false;
        } else {
            boolean ret = false;
            this.IO.mMainLocker.lock();

            try {
                byte[] data = new byte[]{27, 57, (byte)nEncoding};
                ret = this.IO.Write(data, 0, data.length) == data.length;
            } catch (Exception var7) {
                Log.i("Pos", var7.toString());
            } finally {
                this.IO.mMainLocker.unlock();
            }

            return ret;
        }
    }

    public boolean POS_FullCutPaper() {
        if (!this.IO.IsOpened()) {
            return false;
        } else {
            boolean ret = false;
            this.IO.mMainLocker.lock();

            try {
                byte[] data = new byte[]{27, 105};
                ret = this.IO.Write(data, 0, data.length) == data.length;
            } catch (Exception var6) {
                Log.i("Pos", var6.toString());
            } finally {
                this.IO.mMainLocker.unlock();
            }

            return ret;
        }
    }

    public boolean POS_HalfCutPaper() {
        if (!this.IO.IsOpened()) {
            return false;
        } else {
            boolean ret = false;
            this.IO.mMainLocker.lock();

            try {
                byte[] data = new byte[]{27, 109};
                ret = this.IO.Write(data, 0, data.length) == data.length;
            } catch (Exception var6) {
                Log.i("Pos", var6.toString());
            } finally {
                this.IO.mMainLocker.unlock();
            }

            return ret;
        }
    }

    public boolean POS_BlackMark() {
        if (!this.IO.IsOpened()) {
            return false;
        } else {
            boolean ret = false;
            this.IO.mMainLocker.lock();

            try {
                byte[] data = new byte[]{12};
                ret = this.IO.Write(data, 0, data.length) == data.length;
            } catch (Exception var6) {
                Log.i("Pos", var6.toString());
            } finally {
                this.IO.mMainLocker.unlock();
            }

            return ret;
        }
    }

    public boolean POS_LableMark() {
        if (!this.IO.IsOpened()) {
            return false;
        } else {
            boolean ret = false;
            this.IO.mMainLocker.lock();

            try {
                byte[] data = new byte[]{14};
                ret = this.IO.Write(data, 0, data.length) == data.length;
            } catch (Exception var6) {
                Log.i("Pos", var6.toString());
            } finally {
                this.IO.mMainLocker.unlock();
            }

            return ret;
        }
    }

    public boolean POS_Beep(int nBeepCount, int nBeepMillis) {
        if (!this.IO.IsOpened()) {
            return false;
        } else {
            boolean ret = false;
            this.IO.mMainLocker.lock();

            try {
                byte[] data = new byte[]{27, 66, (byte)nBeepCount, (byte)nBeepMillis};
                ret = this.IO.Write(data, 0, data.length) == data.length;
            } catch (Exception var8) {
                Log.i("Pos", var8.toString());
            } finally {
                this.IO.mMainLocker.unlock();
            }

            return ret;
        }
    }

    public boolean POS_KickDrawer(int nDrawerIndex, int nPulseTime) {
        if (!this.IO.IsOpened()) {
            return false;
        } else {
            boolean ret = false;
            this.IO.mMainLocker.lock();

            try {
                byte[] data = new byte[]{27, 112, (byte)nDrawerIndex, (byte)nPulseTime, (byte)nPulseTime};
                ret = this.IO.Write(data, 0, data.length) == data.length;
            } catch (Exception var8) {
                Log.i("Pos", var8.toString());
            } finally {
                this.IO.mMainLocker.unlock();
            }

            return ret;
        }
    }

    public boolean POS_SetPrintSpeed(int nSpeed) {
        if (!this.IO.IsOpened()) {
            return false;
        } else {
            boolean ret = false;
            this.IO.mMainLocker.lock();

            try {
                byte[] data = new byte[]{31, 40, 115, 2, 0, (byte)(nSpeed & 255), (byte)((nSpeed & '\uff00') >> 8)};
                ret = this.IO.Write(data, 0, data.length) == data.length;
            } catch (Exception var7) {
                Log.i("Pos", var7.toString());
            } finally {
                this.IO.mMainLocker.unlock();
            }

            return ret;
        }
    }

    public boolean POS_QueryStatus(byte[] status, int timeout, int MaxRetry) {
        if (!this.IO.IsOpened()) {
            return false;
        } else {
            this.IO.mMainLocker.lock();
            boolean result = false;

            try {
                byte[] cmd = new byte[]{29, 114, 1};

                while(MaxRetry-- >= 0) {
                    this.IO.SkipAvailable();
                    if (this.IO.Write(cmd, 0, cmd.length) == cmd.length && this.IO.Read(status, 0, 1, timeout) == 1) {
                        result = true;
                    }
                }
            } catch (Exception var9) {
                Log.i("Pos", var9.toString());
            } finally {
                this.IO.mMainLocker.unlock();
            }

            return result;
        }
    }

    public boolean POS_RTQueryStatus(byte[] status, int type, int timeout, int MaxRetry) {
        if (!this.IO.IsOpened()) {
            return false;
        } else {
            this.IO.mMainLocker.lock();
            boolean result = false;

            try {
                byte[] cmd = new byte[]{16, 4, (byte)type};

                while(MaxRetry-- >= 0) {
                    this.IO.SkipAvailable();
                    if (this.IO.Write(cmd, 0, cmd.length) == cmd.length && this.IO.Read(status, 0, 1, timeout) == 1) {
                        result = true;
                    }
                }
            } catch (Exception var10) {
            } finally {
                this.IO.mMainLocker.unlock();
            }

            return result;
        }
    }

    public int POS_TicketSucceed(int dwSendIndex, int timeout) {
        if (!this.IO.IsOpened()) {
            return -1;
        } else {
            boolean result = false;
            boolean b_conn_closed = false;
            boolean b_write_failed = false;
            boolean b_read_failed = false;
            boolean b_nopaper = false;
            boolean b_offline = false;
            boolean b_unknow_error = false;
            boolean b_timeout = false;
            this.IO.mMainLocker.lock();

            try {
                Log.i("Pos", String.format("Get Ticket %d Result", dwSendIndex));
                byte[] recbuf = new byte[7];
                byte[] rtqueryCmd = new byte[]{16, 4, 1};
                byte[] ticketCmd = new byte[]{29, 40, 72, 6, 0, 48, 48, (byte)dwSendIndex, (byte)(dwSendIndex >> 8), (byte)(dwSendIndex >> 16), (byte)(dwSendIndex >> 24)};
                this.IO.SkipAvailable();
                int nBytesWrited = this.IO.Write(ticketCmd, 0, ticketCmd.length);
                if (nBytesWrited != ticketCmd.length) {
                    b_write_failed = true;
                } else {
                    long beginTime = System.currentTimeMillis();

                    while(true) {
                        if (!this.IO.IsOpened()) {
                            b_conn_closed = true;
                            break;
                        }

                        if (System.currentTimeMillis() - beginTime > (long)timeout) {
                            b_timeout = true;
                            break;
                        }

                        int nBytesReaded = this.IO.Read(recbuf, 0, 1, 1000);
                        if (nBytesReaded < 0) {
                            b_read_failed = true;
                            break;
                        }

                        if (nBytesReaded == 0) {
                            this.IO.Write(rtqueryCmd, 0, rtqueryCmd.length);
                        } else if (nBytesReaded == 1) {
                            if (recbuf[0] == 55) {
                                if (this.IO.Read(recbuf, 1, 1, timeout) == 1 && (recbuf[1] == 34 || recbuf[1] == 51) && this.IO.Read(recbuf, 2, 5, timeout) == 5) {
                                    int dwRecvIndex = recbuf[2] & 255 | (recbuf[3] & 255) << 8 | (recbuf[4] & 255) << 16 | (recbuf[5] & 255) << 24;
                                    if (dwSendIndex == dwRecvIndex) {
                                        switch (recbuf[1]) {
                                            case 34:
                                                result = true;
                                                break;
                                            case 51:
                                                b_nopaper = true;
                                                break;
                                            default:
                                                b_unknow_error = true;
                                        }

                                        Log.i("Pos", String.format("Ticket Result: %02X %02X %02X %02X %02X %02X %02X", recbuf[0], recbuf[1], recbuf[2], recbuf[3], recbuf[4], recbuf[5], recbuf[6]));
                                        break;
                                    }
                                }
                            } else if ((recbuf[0] & 18) == 18) {
                                Log.i("Pos", String.format("Printer RT Status: %02X ", recbuf[0]));
                                if ((recbuf[0] & 8) != 0) {
                                    b_offline = true;
                                    break;
                                }
                            }
                        }
                    }
                }

                Log.i("Pos", String.format("Ticket %d %s", dwSendIndex, result ? "Succeed" : "Failed"));
            } catch (Exception var22) {
                Log.i("Pos", var22.toString());
            } finally {
                this.IO.mMainLocker.unlock();
            }

            if (b_conn_closed) {
                return -1;
            } else if (b_write_failed) {
                return -2;
            } else if (b_read_failed) {
                return -3;
            } else if (b_offline) {
                return -4;
            } else if (b_nopaper) {
                return -5;
            } else if (b_unknow_error) {
                return -6;
            } else {
                return result ? 0 : -6;
            }
        }
    }

    private byte[] byteArraysToBytes(byte[][] data) {
        int length = 0;

        for(int i = 0; i < data.length; ++i) {
            length += data[i].length;
        }

        byte[] send = new byte[length];
        int k = 0;

        for(int i = 0; i < data.length; ++i) {
            for(int j = 0; j < data[i].length; ++j) {
                send[k++] = data[i][j];
            }
        }

        return send;
    }
}

