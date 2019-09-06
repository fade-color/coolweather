package com.coolweather.android.gson;

import com.google.gson.annotations.SerializedName;

public class Basic {

    @SerializedName("cid")
    public String weatherId;

    public String location;

    @SerializedName("parent_city")
    public String parentCity;

    @SerializedName("admin_area")
    public String adminArea;

    @SerializedName("cnty")
    public String country;

    @SerializedName("lat")
    public String latitude;

    @SerializedName("lon")
    public String longitude;

    @SerializedName("tz")
    public String timeZone;

}
