package com.coolweather.android.gson;

import com.google.gson.annotations.SerializedName;

public class Lifestyle {

    public String type; // 类型

    @SerializedName("brf")
    public String introduction; // 生活指数简介

    @SerializedName("txt")
    public String suggestion; // 建议

}
