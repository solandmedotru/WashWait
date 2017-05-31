package ru.solandme.washwait.versionOne.main;

import ru.solandme.washwait.versionOne.model.WeatherDTO;

public interface OnWeatherUpdatedListener {
    void onSuccess(WeatherDTO weatherDTO);
    void onError(String errorMessage);
}
