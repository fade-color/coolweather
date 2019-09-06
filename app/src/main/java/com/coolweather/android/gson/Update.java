package com.coolweather.android.gson;

import com.google.gson.annotations.SerializedName;

public class Update {

    @SerializedName("loc")
    public String localTime;

    @SerializedName("utc")
    public String utcTime;

}
