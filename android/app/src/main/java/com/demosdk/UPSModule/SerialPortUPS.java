package com.demosdk.UPSModule;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class SerialPortUPS {
    private static final String TAG = "SerialPort";
    private File mDevice;
    private int mBaudrate;
    private int mFlags;
    private FileInputStream mFileInputStream;
    private FileOutputStream mFileOutputStream;

    public SerialPortUPS(File device, int baudrate, int flags) throws SecurityException, IOException {
        if (!device.canRead() || !device.canWrite()) {
            try {
                Process su = Runtime.getRuntime().exec("/system/bin/su");
                String cmd = "chmod 666 " + device.getAbsolutePath() + "\nexit\n";
                su.getOutputStream().write(cmd.getBytes());
                if (su.waitFor() != 0 || !device.canRead() || !device.canWrite()) {
                    throw new SecurityException();
                }
            } catch (Exception var6) {
                var6.printStackTrace();
                throw new SecurityException();
            }
        }

        this.mDevice = device;
        this.mBaudrate = baudrate;
        this.mFlags = flags;

        // Open the input and output streams
        this.mFileInputStream = new FileInputStream(device);
        this.mFileOutputStream = new FileOutputStream(device);
    }

    public InputStream getInputStream() {
        return this.mFileInputStream;
    }

    public OutputStream getOutputStream() {
        return this.mFileOutputStream;
    }

    public void close() {
        try {
            mFileInputStream.close();
            mFileOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

