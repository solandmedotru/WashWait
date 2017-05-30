package ru.solandme.washwait.versionOne.main;

import android.content.Context;

import ru.solandme.washwait.versionOne.model.WeatherDate;

class MainRepository implements IMainRepository {

    Context context;

    public MainRepository(Context context) {
        this.context = context;
    }

    @Override
    public WeatherDate getWeather() {

        return null;
    }
}
