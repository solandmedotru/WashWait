package ru.solandme.washwait.mvp.main.domain.interactor;

import ru.solandme.washwait.mvp.main.domain.model.WashForecastDTO;
import ru.solandme.washwait.mvp.main.domain.model.WeatherDTO;

public interface IMainInteractor {
    void loadWeather();

    interface Callback {
        void onSuccess(WeatherDTO weatherDTO, WashForecastDTO washForecastDTO);
        void onError(String errorMessage);
    }
}
