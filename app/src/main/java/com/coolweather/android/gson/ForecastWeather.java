package com.coolweather.android.gson;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ForecastWeather {

    public Basic basic;

    public Update update;

    public String status;

    @SerializedName("daily_forecast")
    public List<DailyForecast> forecastList;

}
