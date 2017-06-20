package ru.solandme.washwait.mvp.main.presentation.view;

public interface IMainView {

    void showError(String errorMessage);
    void navigateToMap();
    void startProgress();
    void stopProgress();

    void showCurrentMaxTemperature(String currentMaxTemp);

    void showCurrentMinTemperature(String currentMinTemp);
}
