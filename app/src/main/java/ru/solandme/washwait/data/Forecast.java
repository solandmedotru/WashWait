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
    private double lat;
    private double lon;

    boolean isDirty() {
        return weatherId < 600 || weatherId < 700 && temperature > -10;
    }

    public int getWeatherId() {
        return weatherId;
    }

    public void setWeatherId(int weatherId) {
        this.weatherId = weatherId;
    }

    public double getRainCounter() {
        return rainCounter;
    }

    public void setRainCounter(double rainCounter) {
        this.rainCounter = rainCounter;
    }

    public double getSnowCounter() {
        return snowCounter;
    }

    public void setSnowCounter(double snowCounter) {
        this.snowCounter = snowCounter;
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

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public String getCountry() {
        return country;
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

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLon() {
        return lon;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }
}
