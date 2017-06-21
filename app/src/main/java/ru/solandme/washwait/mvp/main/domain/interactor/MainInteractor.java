package ru.solandme.washwait.mvp.main.domain.interactor;

import android.content.Context;

import ru.solandme.washwait.mvp.main.data.repository.WeatherRepository;
import ru.solandme.washwait.mvp.main.domain.model.WeatherDTO;

public class MainInteractor implements IMainInteractor, WeatherRepository.Callback {

    private IMainInteractor.Callback callback;
    private WeatherRepository weatherRepository;

    public MainInteractor(Context context, IMainInteractor.Callback callback) {
        this.callback = callback;
        weatherRepository = new WeatherRepository(this, context);
    }

    @Override
    public void loadWeather() {
        new Thread(() -> {
            weatherRepository.requestWeatherForecast();
        }).start();
    }

    @Override
    public void onSuccess(WeatherDTO weatherDTO) {
        callback.onSuccess(weatherDTO);
    }

    @Override
    public void onError(String errorMessage) {
        callback.onError(errorMessage);
    }

}
