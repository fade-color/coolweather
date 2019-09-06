package com.coolweather.android.util;

import android.text.TextUtils;

import com.coolweather.android.db.City;
import com.coolweather.android.db.County;
import com.coolweather.android.db.Province;
import com.coolweather.android.gson.ForecastWeather;
import com.coolweather.android.gson.LifestyleWeather;
import com.coolweather.android.gson.NowWeather;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class Utility {

    /**
     * 高德key
     */
    public static final String AMAP_KEY = "a20cc695f185ab006ac08770755bdf2e";

    /**
     * 和风天气key
     */
    public static final String HEWEATHER_KEY = "d688f8fef82a465da36359d904b85d6e";

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
                    String countyCenter = countyObject.getString("center");
                    String weatherId = null;
                    try {
                        OkHttpClient client = new OkHttpClient();
                        Request request = new Request.Builder().url("https://free-api.heweather.net/s6/weather/now?key=d688f8fef82a465da36359d904b85d6e&location=" + countyCenter).build();
                        Response executeResponse = client.newCall(request).execute();
                        weatherId = new JSONObject(executeResponse.body().string()).getJSONArray("HeWeather6").getJSONObject(0).getJSONObject("basic").getString("cid");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    county.setWeatherId(weatherId);
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

    public static NowWeather handleNowWeatherResponse(String response) {
        try {
            JSONObject jsonObject = new JSONObject(response);
            JSONArray jsonArray = jsonObject.getJSONArray("HeWeather6");
            String weatherContent = jsonArray.getJSONObject(0).toString();
            return new Gson().fromJson(weatherContent, NowWeather.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static ForecastWeather handleForecastWeatherResponse(String response) {
        try {
            JSONObject jsonObject = new JSONObject(response);
            JSONArray jsonArray = jsonObject.getJSONArray("HeWeather6");
            String weatherContent = jsonArray.getJSONObject(0).toString();
            return new Gson().fromJson(weatherContent, ForecastWeather.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static LifestyleWeather handleLifestyleWeatherResponse(String response) {
        try {
            JSONObject jsonObject = new JSONObject(response);
            JSONArray jsonArray = jsonObject.getJSONArray("HeWeather6");
            String weatherContent = jsonArray.getJSONObject(0).toString();
            return new Gson().fromJson(weatherContent, LifestyleWeather.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
