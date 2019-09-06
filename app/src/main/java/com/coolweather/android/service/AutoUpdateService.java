package com.coolweather.android.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.coolweather.android.WeatherActivity;
import com.coolweather.android.gson.ForecastWeather;
import com.coolweather.android.gson.LifestyleWeather;
import com.coolweather.android.gson.NowWeather;
import com.coolweather.android.util.HttpUtil;
import com.coolweather.android.util.Utility;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class AutoUpdateService extends Service {
    public AutoUpdateService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        updateWeather();
        updateBingPic();
        AlarmManager manager = (AlarmManager) getSystemService(ALARM_SERVICE);
        int anHour = 60 * 60 * 1000; // 一小时毫秒数
        long triggerAtTime = SystemClock.elapsedRealtime() + anHour;
        Intent i = new Intent(this, AutoUpdateService.class);
        PendingIntent pi = PendingIntent.getService(this, 0, i, 0);
        manager.cancel(pi);
        manager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerAtTime, pi);
        return super.onStartCommand(intent, flags, startId);
    }

    private void updateWeather() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String weatherString = prefs.getString("NowWeather", null);
        if (weatherString != null) {
            NowWeather nowWeather = Utility.handleNowWeatherResponse(weatherString);
            String weatherId = nowWeather.basic.weatherId;
            String nowWeatherUrl = "https://free-api.heweather.net/s6/weather/now?key=" + Utility.HEWEATHER_KEY + "&location=" + weatherId;
            String forecastWeatherUrl = "https://free-api.heweather.net/s6/weather/forecast?key=" + Utility.HEWEATHER_KEY + "&location=" + weatherId;
            String lifestyleUrl = "https://free-api.heweather.net/s6/weather/lifestyle?key=" + Utility.HEWEATHER_KEY + "&location=" + weatherId;
            HttpUtil.sendOkHttpRequest(nowWeatherUrl, new Callback() {
                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e) {
                    e.printStackTrace();
                }

                @Override
                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                    String responseText = response.body().string();
                    NowWeather nowWeather = Utility.handleNowWeatherResponse(responseText);
                    if (nowWeather != null && "ok".equals(nowWeather.status)) {
                        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(AutoUpdateService.this).edit();
                        editor.putString("NowWeather", responseText);
                        editor.apply();
                    }
                }
            });
            HttpUtil.sendOkHttpRequest(forecastWeatherUrl, new Callback() {
                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e) {
                    e.printStackTrace();
                }

                @Override
                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                    final String responseText = response.body().string();
                    final ForecastWeather forecastWeather = Utility.handleForecastWeatherResponse(responseText);
                    if (forecastWeather != null && "ok".equals(forecastWeather.status)) {
                        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(AutoUpdateService.this).edit();
                        editor.putString("ForecastWeather", responseText);
                        editor.apply();
                    }
                }
            });
            HttpUtil.sendOkHttpRequest(lifestyleUrl, new Callback() {
                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e) {
                    e.printStackTrace();
                }

                @Override
                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                    final String responseText = response.body().string();
                    final LifestyleWeather lifestyleWeather = Utility.handleLifestyleWeatherResponse(responseText);
                    if (lifestyleWeather != null && "ok".equals(lifestyleWeather.status)) {
                        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(AutoUpdateService.this).edit();
                        editor.putString("LifestyleWeather", responseText);
                        editor.apply();
                    }
                }
            });
        }
    }

    private void updateBingPic() {
        String requestBingPic = "https://cn.bing.com/HPImageArchive.aspx?format=js&idx=0&n=1&mkt=zh-CN";
        HttpUtil.sendOkHttpRequest(requestBingPic, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                try {
                    String responseText = response.body().string();
                    JSONObject jsonObject = new JSONObject(responseText);
                    final String bingPic = "https://cn.bing.com" + jsonObject.getJSONArray("images").getJSONObject(0).getString("url");
                    if (!bingPic.split("https://cn.bing.com")[1].isEmpty()) {
                        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(AutoUpdateService.this).edit();
                        editor.putString("bing_pic", bingPic);
                        editor.apply();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
