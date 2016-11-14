package ru.solandme.washwait.data;

public class Forecast {

    private int weatherId;
    private double rainCounter;
    private double snowCounter;
    private double temperature;
    private int imageRes;
    private String cityName;
    private String country;
    private long date;
    private String description;
    private float lat;
    private float lon;

    boolean isDirty() {
        return weatherId < 600 || weatherId < 700 && temperature > -10;
    }

    public void setWeatherId(int weatherId) {
        this.weatherId = weatherId;
    }

    public double getTemperature() {
        return temperature;
    }

    public void setTemperature(double temperature) {
        this.temperature = temperature;
    }

    public int getImageRes() {
        return imageRes;
    }

    public void setImageRes(int imageRes) {
        this.imageRes = imageRes;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }


    public void setCountry(String country) {
        this.country = country;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setLat(float lat) {
        this.lat = lat;
    }

    public void setLon(float lon) {
        this.lon = lon;
    }
}
