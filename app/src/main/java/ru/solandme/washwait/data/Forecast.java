package ru.solandme.washwait.data;

public class Forecast {

    int weatherIds;
    double rainCounter;
    double snowCounter;
    double temperature;
    String imageRes;
    String date;

    boolean isDirty() {
        return weatherIds < 600 || weatherIds < 700 && temperature > -10;
    }

    public int getWeatherIds() {
        return weatherIds;
    }

    public void setWeatherIds(int weatherIds) {
        this.weatherIds = weatherIds;
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

    public String getImageRes() {
        return imageRes;
    }

    public void setImageRes(String imageRes) {
        this.imageRes = imageRes;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
