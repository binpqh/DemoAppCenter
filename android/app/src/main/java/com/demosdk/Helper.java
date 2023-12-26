package com.demosdk;

import android.content.Context;
import android.util.Log;
import asim.sdk.locker.DeviceInfo;
import asim.sdk.locker.SDKLocker;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.ReadableMapKeySetIterator;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Helper {
    public static String convertToJsonString(HashMap<String, Object> hashMapObject) {
        try {
            JSONObject jsonObject = new JSONObject();
            for (HashMap.Entry<String, Object> entry : hashMapObject.entrySet()) {
                jsonObject.put(entry.getKey(), entry.getValue());
            }
            return jsonObject.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }
    public static Map<String, String> convertReadableMapToMap(ReadableMap readableMap) {

        Map<String, String> result = new HashMap<>();
        ReadableMapKeySetIterator iterator = readableMap.keySetIterator();
        while (iterator.hasNextKey()) {
            String key = iterator.nextKey();
            result.put(key, readableMap.getString(key));
        }
        return result;
    }
    public static List<Map<String, String>> convertReadableArrayToList(ReadableArray readableArray) {
        List<Map<String, String>> resultList = new ArrayList<>();
        for (int i = 0; i < readableArray.size(); i++) {
            ReadableMap map = readableArray.getMap(i);
            resultList.add(convertReadableMapToMap(map));
        }
        return resultList;
    }
    public static List<DeviceInfo> getPortDevice(Context context , int VendorId , int ProductId , int DeviceId){
        SDKLocker locker = new SDKLocker();
        return locker.getAllUsbDeviceHasDriverByVendorIdProductIdAndDeviceId(context,VendorId,ProductId,DeviceId);
    }
}
