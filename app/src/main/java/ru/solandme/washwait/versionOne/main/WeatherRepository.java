package ru.solandme.washwait.versionOne.main;

import android.content.Context;

class WeatherRepository implements IWeatherRepository {

    private OnWeatherUpdatedListener listener;

    public WeatherRepository(Context context, OnWeatherUpdatedListener listener) {
        this.listener = listener;
    }

    @Override
    public void getWeatherForecast() {
        //TODO сделать запрос погоды у погодного провайдера в зависимости от того какой выбран в настройках
    }

    @Override
    public void getCurrentWeather() {
        //TODO сделать запрос текущей погоды у выбранного погодного провайдера
    }
}
