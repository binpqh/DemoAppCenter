package com.demosdk;

import android.util.Log;
import org.json.JSONObject;

import java.util.HashMap;

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
}
