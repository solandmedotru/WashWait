package ru.solandme.washwait.network;

import ru.solandme.washwait.model.washForecast.MyWeatherForecast;

public interface IWeatherClient {
    MyWeatherForecast getWeatherForecast(float lat, float lon, String units, String lang);
}
