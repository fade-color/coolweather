package com.coolweather.android.gson;

import com.google.gson.annotations.SerializedName;

public class Now {

    public String cloud; // 云量

    @SerializedName("cond_code")
    public String weatherStatusCode; // 天气状况代码

    @SerializedName("cond_txt")
    public String weatherStatus; // 天气状况描述

    @SerializedName("fl")
    public String somatosensoryTemperature; // 体感温度

    @SerializedName("hum")
    public String relativeHumidity; // 相对湿度

    @SerializedName("pcpn")
    public String precipitation; // 降水量

    @SerializedName("pres")
    public String pressure; // 压强

    @SerializedName("tmp")
    public String temperature; // 温度

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
