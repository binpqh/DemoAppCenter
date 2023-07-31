package com.demosdk;

import android.util.Log;

import java.util.HashMap;

public class Helper {
    public static HashMap<String, String> convertToMapString(HashMap<String, Object> hashMapObject){
        HashMap<String,String> retVal =new HashMap<String,String>();
        for (HashMap.Entry<String, Object> entry : hashMapObject.entrySet()) {
            Log.d("Result", String.valueOf(entry.getValue()));
            retVal.put(entry.getKey(), String.valueOf(entry.getValue()));
        }
        return retVal;
    }
}
