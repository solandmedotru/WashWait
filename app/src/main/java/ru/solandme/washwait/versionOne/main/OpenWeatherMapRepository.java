package ru.solandme.washwait.versionOne.main;

import android.content.Context;

import ru.solandme.washwait.versionOne.model.WeatherDate;

class OpenWeatherMapRepository implements IWeatherRepository {

    Context context;

    public OpenWeatherMapRepository(Context context) {
        this.context = context;
    }


    @Override
    public WeatherDate getWeatherForecast() {


        return null;
    }
}
