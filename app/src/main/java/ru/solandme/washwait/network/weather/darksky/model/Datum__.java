
package ru.solandme.washwait.network.weather.darksky.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Datum__ {

    @SerializedName("time")
    @Expose
    private int time;
    @SerializedName("summary")
    @Expose
    private String summary;
    @SerializedName("icon")
    @Expose
    private String icon;
    @SerializedName("sunriseTime")
    @Expose
    private int sunriseTime;
    @SerializedName("sunsetTime")
    @Expose
    private int sunsetTime;
    @SerializedName("moonPhase")
    @Expose
    private double moonPhase;
    @SerializedName("precipIntensity")
    @Expose
    private double precipIntensity;
    @SerializedName("precipIntensityMax")
    @Expose
    private double precipIntensityMax;
    @SerializedName("precipProbability")
    @Expose
    private double precipProbability;
    @SerializedName("temperatureMin")
    @Expose
    private double temperatureMin;
    @SerializedName("temperatureMinTime")
    @Expose
    private int temperatureMinTime;
    @SerializedName("temperatureMax")
    @Expose
    private double temperatureMax;
    @SerializedName("temperatureMaxTime")
    @Expose
    private int temperatureMaxTime;
    @SerializedName("apparentTemperatureMin")
    @Expose
    private double apparentTemperatureMin;
    @SerializedName("apparentTemperatureMinTime")
    @Expose
    private int apparentTemperatureMinTime;
    @SerializedName("apparentTemperatureMax")
    @Expose
    private double apparentTemperatureMax;
    @SerializedName("apparentTemperatureMaxTime")
    @Expose
    private int apparentTemperatureMaxTime;
    @SerializedName("dewPoint")
    @Expose
    private double dewPoint;
    @SerializedName("humidity")
    @Expose
    private double humidity;
    @SerializedName("windSpeed")
    @Expose
    private double windSpeed;
    @SerializedName("windBearing")
    @Expose
    private int windBearing;
    @SerializedName("visibility")
    @Expose
    private double visibility;
    @SerializedName("cloudCover")
    @Expose
    private double cloudCover;
    @SerializedName("pressure")
    @Expose
    private double pressure;
    @SerializedName("ozone")
    @Expose
    private double ozone;

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public int getSunriseTime() {
        return sunriseTime;
    }

    public void setSunriseTime(int sunriseTime) {
        this.sunriseTime = sunriseTime;
    }

    public int getSunsetTime() {
        return sunsetTime;
    }

    public void setSunsetTime(int sunsetTime) {
        this.sunsetTime = sunsetTime;
    }

    public double getMoonPhase() {
        return moonPhase;
    }

    public void setMoonPhase(double moonPhase) {
        this.moonPhase = moonPhase;
    }

    public double getPrecipIntensity() {
        return precipIntensity;
    }

    public void setPrecipIntensity(double precipIntensity) {
        this.precipIntensity = precipIntensity;
    }

    public double getPrecipIntensityMax() {
        return precipIntensityMax;
    }

    public void setPrecipIntensityMax(double precipIntensityMax) {
        this.precipIntensityMax = precipIntensityMax;
    }

    public double getPrecipProbability() {
        return precipProbability;
    }

    public void setPrecipProbability(double precipProbability) {
        this.precipProbability = precipProbability;
    }

    public double getTemperatureMin() {
        return temperatureMin;
    }

    public void setTemperatureMin(double temperatureMin) {
        this.temperatureMin = temperatureMin;
    }

    public int getTemperatureMinTime() {
        return temperatureMinTime;
    }

    public void setTemperatureMinTime(int temperatureMinTime) {
        this.temperatureMinTime = temperatureMinTime;
    }

    public double getTemperatureMax() {
        return temperatureMax;
    }

    public void setTemperatureMax(double temperatureMax) {
        this.temperatureMax = temperatureMax;
    }

    public int getTemperatureMaxTime() {
        return temperatureMaxTime;
    }

    public void setTemperatureMaxTime(int temperatureMaxTime) {
        this.temperatureMaxTime = temperatureMaxTime;
    }

    public double getApparentTemperatureMin() {
        return apparentTemperatureMin;
    }

    public void setApparentTemperatureMin(double apparentTemperatureMin) {
        this.apparentTemperatureMin = apparentTemperatureMin;
    }

    public int getApparentTemperatureMinTime() {
        return apparentTemperatureMinTime;
    }

    public void setApparentTemperatureMinTime(int apparentTemperatureMinTime) {
        this.apparentTemperatureMinTime = apparentTemperatureMinTime;
    }

    public double getApparentTemperatureMax() {
        return apparentTemperatureMax;
    }

    public void setApparentTemperatureMax(double apparentTemperatureMax) {
        this.apparentTemperatureMax = apparentTemperatureMax;
    }

    public int getApparentTemperatureMaxTime() {
        return apparentTemperatureMaxTime;
    }

    public void setApparentTemperatureMaxTime(int apparentTemperatureMaxTime) {
        this.apparentTemperatureMaxTime = apparentTemperatureMaxTime;
    }

    public double getDewPoint() {
        return dewPoint;
    }

    public void setDewPoint(double dewPoint) {
        this.dewPoint = dewPoint;
    }

    public double getHumidity() {
        return humidity;
    }

    public void setHumidity(double humidity) {
        this.humidity = humidity;
    }

    public double getWindSpeed() {
        return windSpeed;
    }

    public void setWindSpeed(double windSpeed) {
        this.windSpeed = windSpeed;
    }

    public int getWindBearing() {
        return windBearing;
    }

    public void setWindBearing(int windBearing) {
        this.windBearing = windBearing;
    }

    public double getVisibility() {
        return visibility;
    }

    public void setVisibility(double visibility) {
        this.visibility = visibility;
    }

    public double getCloudCover() {
        return cloudCover;
    }

    public void setCloudCover(double cloudCover) {
        this.cloudCover = cloudCover;
    }

    public double getPressure() {
        return pressure;
    }

    public void setPressure(double pressure) {
        this.pressure = pressure;
    }

    public double getOzone() {
        return ozone;
    }

    public void setOzone(double ozone) {
        this.ozone = ozone;
    }

}
