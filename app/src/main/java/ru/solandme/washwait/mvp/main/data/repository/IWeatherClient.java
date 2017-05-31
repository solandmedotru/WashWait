package ru.solandme.washwait.mvp.main.data.repository;

import ru.solandme.washwait.mvp.main.domain.model.WeatherDTO;

public interface IWeatherClient {
    void requestWeatherForecast();
    void requestCurrentWeather();

    interface Callback {
        void onSuccess(WeatherDTO weatherDTO);
        void onError(String errorMessage);
    }
}
