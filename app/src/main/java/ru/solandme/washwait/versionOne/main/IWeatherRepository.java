package ru.solandme.washwait.versionOne.main;

import ru.solandme.washwait.versionOne.model.WeatherDate;

interface IWeatherRepository {
    WeatherDate getWeatherForecast();
}
