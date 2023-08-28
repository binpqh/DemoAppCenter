package com.demosdk.UPSModule;

import android.util.Log;
import com.lvrenyang.io.base.IO;
import com.lvrenyang.io.base.IOCallBack;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Vector;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

public class COMIO extends IO {
    private static final String TAG = "COMIO";
    private static final SerialPortFinder spFinder = new SerialPortFinder();
    private SerialPortUPS sp = null;
    private InputStream is = null;
    private OutputStream os = null;
    private AtomicBoolean isOpened = new AtomicBoolean(false);
    private AtomicBoolean isReadyRW = new AtomicBoolean(false);
    private IOCallBack cb = null;
    private Vector<Byte> rxBuffer = new Vector();
    private AtomicLong nIdleTime = new AtomicLong(0L);

    public COMIO() {
    }

    public boolean Open(String name, int baudrate, int flags) {
        this.mMainLocker.lock();

        try {
            if (this.isOpened.get()) {
                throw new Exception("Already open");
            }

            this.isReadyRW.set(false);
            File device = null;

            try {
                device = new File(name);
                this.sp = new SerialPortUPS(device, baudrate, flags);
                this.os = this.sp.getOutputStream();
                this.is = this.sp.getInputStream();
                this.isReadyRW.set(true);
            } catch (Exception var10) {
                Log.e("COMIO", var10.toString());
                this.sp = null;
                this.os = null;
                this.is = null;
            }

            if (this.isReadyRW.get()) {
                Log.v("COMIO", "Connected to " + device.getAbsolutePath() + " " + baudrate);
                this.rxBuffer.clear();
            }

            this.isOpened.set(this.isReadyRW.get());
            if (null != this.cb) {
                if (this.isOpened.get()) {
                    this.cb.OnOpen();
                } else {
                    this.cb.OnOpenFailed();
                }
            }
        } catch (Exception var11) {
            Log.i("COMIO", var11.toString());
        } finally {
            this.mMainLocker.unlock();
        }

        return this.isOpened.get();
    }

    public void Close() {
        this.mCloseLocker.lock();

        try {
            try {
                if (null != this.sp) {
                    this.sp.close();
                }
            } catch (Exception var12) {
                Log.i("COMIO", var12.toString());
            } finally {
                ;
            }

            if (!this.isReadyRW.get()) {
                throw new Exception();
            }

            this.sp = null;
            this.is = null;
            this.os = null;
            this.isReadyRW.set(false);
            if (!this.isOpened.get()) {
                throw new Exception();
            }

            this.isOpened.set(false);
            if (null != this.cb) {
                this.cb.OnClose();
            }
        } catch (Exception var14) {
            Log.i("COMIO", var14.toString());
        } finally {
            this.mCloseLocker.unlock();
        }

    }

    public int Write(byte[] buffer, int offset, int count) {
        if (!this.isReadyRW.get()) {
            return -1;
        } else {
            this.mMainLocker.lock();
            int nBytesWritten;
            try {
                this.nIdleTime.set(0L);
                this.os.write(buffer, offset, count);
                this.os.flush();
                nBytesWritten = count;
                this.nIdleTime.set(System.currentTimeMillis());
            } catch (Exception var9) {
                Log.e("COMIO", var9.toString());
                this.Close();
                nBytesWritten = -1;
            } finally {
                this.mMainLocker.unlock();
            }

            return nBytesWritten;
        }
    }

    public int Read(byte[] buffer, int offset, int count, int timeout) {
        if (!this.isReadyRW.get()) {
            return -1;
        } else {
            this.mMainLocker.lock();
            int nBytesReaded = 0;

            try {
                this.nIdleTime.set(0L);
                long time = System.currentTimeMillis();

                label109:
                while(true) {
                    while(true) {
                        if (System.currentTimeMillis() - time >= (long)timeout) {
                            break label109;
                        }

                        if (!this.isReadyRW.get()) {
                            throw new Exception("Not Ready For Read Write");
                        }

                        if (nBytesReaded == count) {
                            break label109;
                        }

                        if (this.rxBuffer.size() > 0) {
                            buffer[offset + nBytesReaded] = (Byte)this.rxBuffer.get(0);
                            this.rxBuffer.remove(0);
                            ++nBytesReaded;
                        } else {
                            int available = this.is.available();
                            if (available > 0) {
                                byte[] receive = new byte[available];
                                int nReceived = this.is.read(receive);
                                if (nReceived > 0) {
                                    for(int i = 0; i < nReceived; ++i) {
                                        this.rxBuffer.add(receive[i]);
                                    }
                                }
                            } else {
                                Thread.sleep(1L);
                            }
                        }
                    }
                }

                this.nIdleTime.set(System.currentTimeMillis());
            } catch (Exception var15) {
                Log.e("COMIO", var15.toString());
                this.Close();
                nBytesReaded = -1;
            } finally {
                this.mMainLocker.unlock();
            }

            return nBytesReaded;
        }
    }

    public void SkipAvailable() {
        this.mMainLocker.lock();

        try {
            this.rxBuffer.clear();
            this.is.skip((long)this.is.available());
        } catch (Exception var5) {
            Log.i("COMIO", var5.toString());
        } finally {
            this.mMainLocker.unlock();
        }

    }

    public boolean IsOpened() {
        return this.isOpened.get();
    }

    public void SetCallBack(IOCallBack callBack) {
        this.mMainLocker.lock();

        try {
            this.cb = callBack;
        } catch (Exception var6) {
            Log.i("COMIO", var6.toString());
        } finally {
            this.mMainLocker.unlock();
        }

    }

    public static String[] enumPorts() {
        return spFinder.getAllDevicesPath();
    }
}
