package ru.solandme.washwait.mvp.main.data.repository;

import ru.solandme.washwait.mvp.main.domain.model.WashForecastDTO;
import ru.solandme.washwait.mvp.main.domain.model.WeatherDTO;

public interface OnDTOUpdatedListener {
    void onSuccess(WeatherDTO weatherDTO, WashForecastDTO washForecastDTO);
    void onError(String errorMessage);
}
