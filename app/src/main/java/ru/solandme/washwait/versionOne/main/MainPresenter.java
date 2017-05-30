package ru.solandme.washwait.versionOne.main;

import android.content.Context;

import ru.solandme.washwait.versionOne.model.WashForecastDate;
import ru.solandme.washwait.versionOne.model.WeatherDate;

public class MainPresenter implements IMainPresenter, OnUpdatedListener {

    private IMainView mainView;
    private IMainInteractor mainInteractor;

    public MainPresenter(Context context) {
        this.mainInteractor = new MainInteractor(context, this);
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
        mainView.startProgress();
        mainInteractor.loadWeather();
    }

    @Override
    public void onSuccess(WeatherDate weatherDate, WashForecastDate washForecastDate) {
        mainView.stopProgress();
        //TODO сделать отдельные методы для заполнения каждой вьюшки. mainView.showCurrentMaxTemperature(temp)
        mainView.showCurrentMaxTemperature(weatherDate.getCurrentMaxTemp());
    }

    @Override
    public void onError(String errorMessage) {
        mainView.stopProgress();
        mainView.showError(errorMessage);
    }
}
