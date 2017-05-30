package ru.solandme.washwait.versionOne.main;

import ru.solandme.washwait.versionOne.model.WashForecastDate;
import ru.solandme.washwait.versionOne.model.WeatherDate;

public interface OnUpdatedListener {
    void onSuccess(WeatherDate weatherDate, WashForecastDate washForecastDate);
    void onError(String errorMessage);
}
