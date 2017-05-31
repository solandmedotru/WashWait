package ru.solandme.washwait.versionOne.main;

import ru.solandme.washwait.versionOne.model.WashForecastDTO;
import ru.solandme.washwait.versionOne.model.WeatherDTO;

public interface OnDTOUpdatedListener {
    void onSuccess(WeatherDTO weatherDTO, WashForecastDTO washForecastDTO);
    void onError(String errorMessage);
}
