package ru.solandme.washwait.network.weather;

import ru.solandme.washwait.ui.model.washForecast.MyWeatherForecast;

public interface IWeatherClient {
    MyWeatherForecast getWeatherForecast(float lat, float lon, String units, String lang);
}
