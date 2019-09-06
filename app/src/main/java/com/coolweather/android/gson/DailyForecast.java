package com.coolweather.android.gson;

import com.google.gson.annotations.SerializedName;

public class DailyForecast {

    @SerializedName("cond_code_d")
    public String statusCodeDay; // 白天天气状况代码

    @SerializedName("cond_code_n")
    public String statusCOdeNight; // 夜晚天气状况代码

    @SerializedName("cond_txt_d")
    public String weatherDay;

    @SerializedName("cond_txt_n")
    public String weatherNight;

    @SerializedName("date")
    public String date;

    @SerializedName("hum")
    public String relativeHumidity; // 相对湿度

    @SerializedName("mr")
    public String moonRiseTime;

    @SerializedName("ms")
    public String moonfallTime;

    @SerializedName("pcpn")
    public String precipitation; // 降水量

    @SerializedName("pop")
    public String precipitationProbability; // 降水概率

    @SerializedName("pres")
    public String pressure; // 压强

    @SerializedName("sr")
    public String sunRiseTime;

    @SerializedName("ss")
    public String sunFallTime;

    @SerializedName("tmp_max")
    public String temperatureMax;

    @SerializedName("tmp_min")
    public String tempratureMin;

    @SerializedName("uv_index")
    public String uvIndex;

    @SerializedName("vis")
    public String visibility; // 能见度

    @SerializedName("wind_deg")
    public String windDegree;

    @SerializedName("wind_dir")
    public String windDirection;

    @SerializedName("wind_sc")
    public String windPower; // 风力

    @SerializedName("wind_spd")
    public String windSpeed;

}
