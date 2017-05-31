package ru.solandme.washwait.versionOne.main;

import android.content.Context;

class MainInteractor implements IMainInteractor {

    private OnUpdatedListener onUpdatedListener;
    private IWeatherRepository weatherRepository;

    public MainInteractor(Context context, OnUpdatedListener onUpdatedListener) {
        this.onUpdatedListener = onUpdatedListener;
        this.weatherRepository = new OpenWeatherMapRepository(context);
    }


    @Override
    public void loadWeather() {
        weatherRepository.getWeatherForecast();
    }
}
