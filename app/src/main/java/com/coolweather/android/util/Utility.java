package com.coolweather.android.util;

import android.text.TextUtils;
import android.util.Log;

import com.coolweather.android.db.City;
import com.coolweather.android.db.County;
import com.coolweather.android.db.Province;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Utility {

    /**
     * 解析和处理服务器返回的省级数据
     */
    public static boolean handleProvinceResponse(String response) {
        if (!TextUtils.isEmpty(response)) {
            try {
                JSONObject allMessage = new JSONObject(response);
                JSONObject chinaMessage = allMessage.getJSONArray("districts").getJSONObject(0);
                JSONArray chinaDistricts = chinaMessage.getJSONArray("districts");
                for (int i = 0; i < chinaDistricts.length(); i++) {
                    JSONObject provinceObject = chinaDistricts.getJSONObject(i);
                    Province province = new Province();
                    province.setProvinceName(provinceObject.getString("name"));
                    province.setProvinceCode(provinceObject.getInt("adcode"));
                    province.save(); // 存入数据库
                }
                return true;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    /**
     * 解析和处理服务器返回的市级数据
     */
    public static boolean handleCityResponse(String response, int provinceId) {
        if (!TextUtils.isEmpty(response)) {
            try {
                JSONObject allMessage = new JSONObject(response);
                JSONObject cityMessage = allMessage.getJSONArray("districts").getJSONObject(0);
                JSONArray allCities = cityMessage.getJSONArray("districts");
                for (int i = 0; i < allCities.length(); i++) {
                    JSONObject cityObject = allCities.getJSONObject(i);
                    City city = new City();
                    city.setCityName(cityObject.getString("name"));
                    city.setCityCode(cityObject.getInt("adcode"));
                    city.setProvinceId(provinceId);
                    city.save(); // 存入数据库
                }
                return true;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    /**
     * 解析和处理服务器返回的省级数据
     */
    public static boolean handleCountyResponse(String response, int cityId) {
        if (!TextUtils.isEmpty(response)) {
            try {
                JSONObject allMessage = new JSONObject(response);
                JSONObject countyMessage = allMessage.getJSONArray("districts").getJSONObject(0);
                JSONArray allCounties = countyMessage.getJSONArray("districts");
                for (int i = 0; i < allCounties.length(); i++) {
                    JSONObject countyObject = allCounties.getJSONObject(i);
                    County county = new County();
                    county.setCountyName(countyObject.getString("name"));
                    county.setWeatherId(countyObject.getString("adcode"));
                    county.setCityId(cityId);
                    county.save(); // 存入数据库
                }
                return true;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return false;
    }
}
