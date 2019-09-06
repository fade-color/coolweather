package com.coolweather.android;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.coolweather.android.gson.DailyForecast;
import com.coolweather.android.gson.ForecastWeather;
import com.coolweather.android.gson.Lifestyle;
import com.coolweather.android.gson.LifestyleWeather;
import com.coolweather.android.gson.NowWeather;
import com.coolweather.android.service.AutoUpdateService;
import com.coolweather.android.util.HttpUtil;
import com.coolweather.android.util.Utility;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class WeatherActivity extends AppCompatActivity {

    private ScrollView weatherLayout;

    private TextView titleCity;

    private TextView titleUpdateTime;

    private TextView degreeText;

    private TextView weatherInfoText;

    private LinearLayout forecastLayout;

    private TextView pressureText;

    private TextView uvText;

    private TextView comfText;

    private TextView drsgText;

    private TextView fluText;

    private TextView sportText;

    private TextView trayText;

    private TextView uvSuggestionText;

    private TextView cwText;

    private TextView airText;
    
    private ImageView bingPicImg;

    public SwipeRefreshLayout swipeRefresh;

    public DrawerLayout drawerLayout;

    private Button navButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            View decorView = getWindow().getDecorView();
            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
        setContentView(R.layout.activity_weather);

        // 初始化各控件
        weatherLayout = findViewById(R.id.weather_layout);
        titleCity = findViewById(R.id.title_city);
        titleUpdateTime = findViewById(R.id.title_update_time);
        degreeText = findViewById(R.id.degree_text);
        weatherInfoText = findViewById(R.id.weather_info_text);
        forecastLayout = findViewById(R.id.forecast_layout);
        pressureText = findViewById(R.id.pressure_text);
        uvText = findViewById(R.id.uv_text);
        comfText = findViewById(R.id.comf_text);
        drsgText = findViewById(R.id.drsg_text);
        fluText = findViewById(R.id.flu_text);
        sportText = findViewById(R.id.sport_text);
        trayText = findViewById(R.id.tray_text);
        uvSuggestionText = findViewById(R.id.uv_sug_text);
        cwText = findViewById(R.id.cw_text);
        airText = findViewById(R.id.air_text);
        bingPicImg = findViewById(R.id.bing_pic_img);
        swipeRefresh = findViewById(R.id.swipe_refresh);
        swipeRefresh.setColorSchemeResources(R.color.colorPrimary);
        drawerLayout = findViewById(R.id.drawer_layout);
        navButton = findViewById(R.id.nav_button);

        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String bingPic = prefs.getString("bing_pic", null);
        String nowWeatherString = prefs.getString("NowWeather",null);
        String forecastWeatherString = prefs.getString("ForecastWeather", null);
        String lifestyleString = prefs.getString("LifestyleWeather", null);
        final String weatherId;
        if (bingPic != null) {
            Glide.with(this).load(bingPic).into(bingPicImg);
        } else {
            loadBingPic();
        }
        if (nowWeatherString != null && forecastWeatherString != null && lifestyleString != null) {
            // 有缓存时直接解析天气数据
            NowWeather nowWeather = Utility.handleNowWeatherResponse(nowWeatherString);
            ForecastWeather forecastWeather = Utility.handleForecastWeatherResponse(forecastWeatherString);
            LifestyleWeather lifestyleWeather = Utility.handleLifestyleWeatherResponse(lifestyleString);
            weatherId = nowWeather.basic.weatherId;
            showWeatherInfo(nowWeather);
            showWeatherInfo(forecastWeather);
            showWeatherInfo(lifestyleWeather);
        } else {
            // 无缓存时去服务器查询天气
            weatherId = getIntent().getStringExtra("weather_id");
            weatherLayout.setVisibility(View.INVISIBLE);
            requestWeather(weatherId);
        }
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                requestWeather(prefs.getString("weather_id",null));
            }
        });
        navButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });
    }

    private void loadBingPic() {
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
                        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(WeatherActivity.this).edit();
                        editor.putString("bing_pic", bingPic);
                        editor.apply();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Glide.with(WeatherActivity.this).load(bingPic).into(bingPicImg);
                            }
                        });
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * 根据天气id请求天气信息
     * @param weatherId 天气id
     */
    public void requestWeather(String weatherId) {
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(WeatherActivity.this).edit();
        editor.putString("weather_id", weatherId);
        editor.apply();
        String nowWeatherUrl = "https://free-api.heweather.net/s6/weather/now?key=" + Utility.HEWEATHER_KEY + "&location=" + weatherId;
        String forecastWeatherUrl = "https://free-api.heweather.net/s6/weather/forecast?key=" + Utility.HEWEATHER_KEY + "&location=" + weatherId;
        String lifestyleUrl = "https://free-api.heweather.net/s6/weather/lifestyle?key=" + Utility.HEWEATHER_KEY + "&location=" + weatherId;
        HttpUtil.sendOkHttpRequest(nowWeatherUrl, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(WeatherActivity.this, "获取今日天气信息失败", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                final String responseText = response.body().string();
                final NowWeather nowWeather = Utility.handleNowWeatherResponse(responseText);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (nowWeather != null && "ok".equals(nowWeather.status)) {
                            SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(WeatherActivity.this).edit();
                            editor.putString("NowWeather", responseText);
                            editor.apply();
                            showWeatherInfo(nowWeather);
                        } else {
                            Toast.makeText(WeatherActivity.this, "获取今日天气信息失败", Toast.LENGTH_SHORT).show();
                        }
                        swipeRefresh.setRefreshing(false);
                    }
                });
            }
        });
        HttpUtil.sendOkHttpRequest(forecastWeatherUrl, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(WeatherActivity.this, "获取天气信息出错", Toast.LENGTH_SHORT).show();
                        swipeRefresh.setRefreshing(false);
                    }
                });
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                final String responseText = response.body().string();
                final ForecastWeather forecastWeather = Utility.handleForecastWeatherResponse(responseText);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (forecastWeather != null && "ok".equals(forecastWeather.status)) {
                            SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(WeatherActivity.this).edit();
                            editor.putString("ForecastWeather", responseText);
                            editor.apply();
                            showWeatherInfo(forecastWeather);
                        } else {
                            Toast.makeText(WeatherActivity.this, "获取天气信息出错", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
        HttpUtil.sendOkHttpRequest(lifestyleUrl, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(WeatherActivity.this, "获取天气信息出错", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                final String responseText = response.body().string();
                final LifestyleWeather lifestyleWeather = Utility.handleLifestyleWeatherResponse(responseText);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (lifestyleWeather != null && "ok".equals(lifestyleWeather.status)) {
                            SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(WeatherActivity.this).edit();
                            editor.putString("LifestyleWeather", responseText);
                            editor.apply();
                            showWeatherInfo(lifestyleWeather);
                        } else {
                            Toast.makeText(WeatherActivity.this, "获取天气信息出错", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }

    private void showWeatherInfo(LifestyleWeather lifestyleWeather) {
        for (Lifestyle lifestyle :
                lifestyleWeather.lifestyle) {
            switch (lifestyle.type) {
                case "comf":
                    comfText.setText("舒适度：" + lifestyle.introduction + "\n" + lifestyle.suggestion);
                    break;
                case "drsg":
                    drsgText.setText("穿衣指数：" + lifestyle.introduction + "\n" + lifestyle.suggestion);
                    break;
                case "flu":
                    fluText.setText("感冒指数：" + lifestyle.introduction + "\n" + lifestyle.suggestion);
                    break;
                case "sport":
                    sportText.setText("运动指数：" + lifestyle.introduction + "\n" + lifestyle.suggestion);
                    break;
                case "trav":
                    trayText.setText("旅游指数：" + lifestyle.introduction + "\n" + lifestyle.suggestion);
                    break;
                case "uv":
                    uvSuggestionText.setText("紫外线指数：" + lifestyle.introduction + "\n" + lifestyle.suggestion);
                    break;
                case "cw":
                    cwText.setText("洗车指数：" + lifestyle.introduction + "\n" + lifestyle.suggestion);
                    break;
                case "air":
                    airText.setText("空气指数：" + lifestyle.introduction + "\n" + lifestyle.suggestion);
                    break;
                default:
                    break;
            }
        }
        weatherLayout.setVisibility(View.VISIBLE);
        Intent intent = new Intent(WeatherActivity.this, AutoUpdateService.class);
        startService(intent);
    }

    private void showWeatherInfo(ForecastWeather forecastWeather) {
        forecastLayout.removeAllViews();
        for (DailyForecast forecast :
                forecastWeather.forecastList) {
            View view = LayoutInflater.from(this).inflate(R.layout.forecast_item, forecastLayout, false);
            TextView dateText = view.findViewById(R.id.date_text);
            TextView infoText = view.findViewById(R.id.info_text);
            TextView maxText = view.findViewById(R.id.max_text);
            TextView minText = view.findViewById(R.id.min_text);
            dateText.setText(forecast.date);
            infoText.setText(forecast.weatherDay);
            maxText.setText(forecast.temperatureMax);
            minText.setText(forecast.tempratureMin);
            forecastLayout.addView(view);
        }
        DailyForecast today = forecastWeather.forecastList.get(0);
        pressureText.setText(today.pressure);
        uvText.setText(today.uvIndex);
    }

    private void showWeatherInfo(NowWeather nowWeather) {
        String cityName = nowWeather.basic.location;
        String updateTime = nowWeather.update.localTime.split(" ")[1]; // 2019-09-05 21:12   只取 21:12
        String degree = nowWeather.now.temperature + "℃";
        String weatherInfo = nowWeather.now.weatherStatus;
        titleCity.setText(cityName);
        titleUpdateTime.setText("上次刷新：" + updateTime);
        degreeText.setText(degree);
        weatherInfoText.setText(weatherInfo);
    }
}
