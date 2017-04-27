package ru.solandme.washwait.model.washForecast;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

public class MyWeatherForecast implements Parcelable {

    private long lastUpdate;
    private String cityName;
    private String country;
    private double latitude;
    private double longitude;
    private boolean isForecastResultOK;
    private boolean isCurrWeatherResultOK;
    private int maxPeriod;
    private String units;

    private MyWeather currentWeather;
    private List<MyWeather> myWeatherList;

    public MyWeatherForecast(int maxPeriod) {
        this.maxPeriod = maxPeriod;
    }

    public String getUnits() {
        return units;
    }

    public void setUnits(String units) {
        this.units = units;
    }

    public long getLastUpdate() {
        return lastUpdate;
    }

    public int getMaxPeriod() {
        return maxPeriod;
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


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.lastUpdate);
        dest.writeString(this.cityName);
        dest.writeString(this.country);
        dest.writeDouble(this.latitude);
        dest.writeDouble(this.longitude);
        dest.writeByte(this.isForecastResultOK ? (byte) 1 : (byte) 0);
        dest.writeByte(this.isCurrWeatherResultOK ? (byte) 1 : (byte) 0);
        dest.writeInt(this.maxPeriod);
        dest.writeString(this.units);
        dest.writeParcelable(this.currentWeather, flags);
        dest.writeList(this.myWeatherList);
    }

    protected MyWeatherForecast(Parcel in) {
        this.lastUpdate = in.readLong();
        this.cityName = in.readString();
        this.country = in.readString();
        this.latitude = in.readDouble();
        this.longitude = in.readDouble();
        this.isForecastResultOK = in.readByte() != 0;
        this.isCurrWeatherResultOK = in.readByte() != 0;
        this.maxPeriod = in.readInt();
        this.units = in.readString();
        this.currentWeather = in.readParcelable(MyWeather.class.getClassLoader());
        this.myWeatherList = new ArrayList<MyWeather>();
        in.readList(this.myWeatherList, MyWeather.class.getClassLoader());
    }

    public static final Creator<MyWeatherForecast> CREATOR = new Creator<MyWeatherForecast>() {
        @Override
        public MyWeatherForecast createFromParcel(Parcel source) {
            return new MyWeatherForecast(source);
        }

        @Override
        public MyWeatherForecast[] newArray(int size) {
            return new MyWeatherForecast[size];
        }
    };
}
