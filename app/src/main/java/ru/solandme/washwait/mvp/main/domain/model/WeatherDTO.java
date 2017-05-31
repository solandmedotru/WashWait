package ru.solandme.washwait.mvp.main.domain.model;

import java.util.List;

public class WeatherDTO {
    private Weather currentWeather;
    private List<Weather> forecast;

    public Weather getCurrentWeather() {
        return currentWeather;
    }

    public void setCurrentWeather(Weather currentWeather) {
        this.currentWeather = currentWeather;
    }

    public List<Weather> getForecast() {
        return forecast;
    }

    public void setForecast(List<Weather> forecast) {
        this.forecast = forecast;
    }
}
