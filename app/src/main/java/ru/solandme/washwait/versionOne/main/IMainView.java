package ru.solandme.washwait.versionOne.main;

public interface IMainView {

    void load();
    void showError(String errorMessage);
    void navigateToMap();
    void startProgress();
    void stopProgress();

    void showCurrentMaxTemperature(String currentMaxTemp);
}
