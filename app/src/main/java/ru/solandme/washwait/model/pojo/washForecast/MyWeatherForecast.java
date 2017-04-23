package ru.solandme.washwait.model.pojo.washForecast;

import java.io.Serializable;
import java.util.List;

public class MyWeatherForecast implements Serializable{

    private long lastUpdate;
    private String cityName;
    private String country;
    private double latitude;
    private double longitude;
    private boolean isForecastResultOK;
    private boolean isCurrWeatherResultOK;

    private MyWeather currentWeather;
    private List<MyWeather> myWeatherList;

    public MyWeatherForecast() {
    }

    public long getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(long lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    public boolean isCurrWeatherResultOK() {
        return isCurrWeatherResultOK;
    }

    public void setCurrWeatherResultOK(boolean currWeatherResultOK) {
        isCurrWeatherResultOK = currWeatherResultOK;
    }

    public boolean isForecastResultOK() {
        return isForecastResultOK;
    }

    public void setForecastResultOK(boolean forecastResultOK) {
        isForecastResultOK = forecastResultOK;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public MyWeather getCurrentWeather() {
        return currentWeather;
    }

    public void setCurrentWeather(MyWeather currentWeather) {
        this.currentWeather = currentWeather;
    }

    public List<MyWeather> getMyWeatherList() {
        return myWeatherList;
    }

    public void setMyWeatherList(List<MyWeather> myWeatherList) {
        this.myWeatherList = myWeatherList;
    }
}
