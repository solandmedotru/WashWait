package ru.solandme.washwait.versionOne.main;

import ru.solandme.washwait.versionOne.model.WashForecastDate;
import ru.solandme.washwait.versionOne.model.WeatherDate;

public class MainPresenter implements IMainPresenter, OnUpdatedListener {

    private IMainView mainView;
    private IMainInteractor mainInteractor;

    public MainPresenter() {
        this.mainInteractor = new MainInteractor(this);
    }

    @Override
    public void attachView(IMainView mainView) {
        this.mainView = mainView;
    }

    @Override
    public void detachView() {
        this.mainView = null;
    }

    @Override
    public void load() {
        mainInteractor.loadWeather();
    }

    @Override
    public void onSuccess(WeatherDate weatherDate, WashForecastDate washForecastDate) {
        //TODO сделать отдельные методы для заполнения каждой вьюшки. mainView.showCurrentMaxTemperature(String temp);
        mainView.updateScreen(weatherDate, washForecastDate);
    }

    @Override
    public void onError(String errorMessage) {
        mainView.showError(errorMessage);
    }
}
