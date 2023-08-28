package com.demosdk.UPSModule;

import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class SerialPortExample {
    private static final String SERIAL_PORT_NAME = "/dev/ttyXR6";
    private static final int BAUDRATE = 2400;

    public void open() {
        SerialPortUPS serialPort = null;
        try {
            serialPort = new SerialPortUPS(new File(SERIAL_PORT_NAME), BAUDRATE, 0);

            // Write data to serial port
            OutputStream outputStream = serialPort.getOutputStream();
            byte[] dataToSend = {81, 52, 13};
            outputStream.write(dataToSend);
            outputStream.flush();

            // Read data from serial port
            InputStream inputStream = serialPort.getInputStream();
            byte[] readBuffer = new byte[256];
            int bytesRead = inputStream.read(readBuffer);

            // Process the received data
            if (bytesRead > 0) {
                byte[] receivedData = new byte[bytesRead];
                System.arraycopy(readBuffer, 0, receivedData, 0, bytesRead);
                Log.d("Received data: ", bytesToHex(receivedData));
            } else {
                System.out.println("No data received.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (serialPort != null) {
                serialPort.close();
            }
        }
    }

    // Helper function to convert bytes to hexadecimal string
    public static String bytesToHex(byte[] bytes) {
        StringBuilder result = new StringBuilder();
        for (byte b : bytes) {
            result.append(String.format("%02X", b));
        }
        return result.toString();
    }
}

