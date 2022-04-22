package com.example.fstr.util;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;

public class JsonParser {

    private static JSONObject passJson;

    private JsonParser() {
    }

    public static String getLatitude(String passJsonString) {
        passJson = new JSONObject(passJsonString);
        return passJson.getJSONObject("coords").getString("latitude");
    }

    public static String getLongitude(String passJsonString) {
        passJson = new JSONObject(passJsonString);
        return passJson.getJSONObject("coords").getString("longitude");
    }

    public static String getHeight(String passJsonString) {
        passJson = new JSONObject(passJsonString);
        return passJson.getJSONObject("coords").getString("height");
    }

    public static String getName(String passJsonString) {
        passJson = new JSONObject(passJsonString);
        return passJson.getJSONObject("user").getString("name");
    }

    public static String getSurname(String passJsonString) {
        passJson = new JSONObject(passJsonString);
        return passJson.getJSONObject("user").getString("surname");
    }

    public static String getEmail(String passJsonString) {
        passJson = new JSONObject(passJsonString);
        return passJson.getJSONObject("user").getString("email");
    }

    public static String getPhone(String passJsonString) {
        passJson = new JSONObject(passJsonString);
        return passJson.getJSONObject("user").getString("phone");
    }

    public static String getRawData(String passJsonString) {
        int indexOfImagesData = passJsonString.indexOf("\"images\":");
        StringBuilder builder = new StringBuilder(passJsonString.substring(0, indexOfImagesData - 1).trim());
        return builder.replace(builder.length() - 1, builder.length() - 1, "}").toString();
    }

    public static HashMap<String, String> getUrlMap(String passJsonString) {
        JSONArray images = new JSONObject(passJsonString).getJSONArray("images");
        HashMap<String, String> urlMap = new HashMap<>();
        for (int i = 0; i < images.toList().size(); i++) {
            JSONObject jsonObject = images.getJSONObject(i);
            if (!jsonObject.getString("url").equals("")) {
                urlMap.put(jsonObject.getString("title"), jsonObject.getString("url"));
            }
        }
        return urlMap;
    }
}
