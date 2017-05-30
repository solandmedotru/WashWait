package ru.solandme.washwait.versionOne.main;

import ru.solandme.washwait.versionOne.model.WashForecastDate;
import ru.solandme.washwait.versionOne.model.WeatherDate;

public interface IMainView {

    void load();
    void updateScreen(WeatherDate weatherDate, WashForecastDate forecastDate);
    void showError(String errorMessage);
    void navigateToMap();
}
