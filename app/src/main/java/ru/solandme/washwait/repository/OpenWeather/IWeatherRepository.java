package ru.solandme.washwait.repository.OpenWeather;

import ru.solandme.washwait.model.pojo.washForecast.MyWeatherForecast;

public interface IWeatherRepository {
    MyWeatherForecast getWeatherForecast(float lat, float lon, String units, String lang);
}
