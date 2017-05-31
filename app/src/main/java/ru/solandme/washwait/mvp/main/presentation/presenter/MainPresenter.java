package ru.solandme.washwait.mvp.main.presentation.presenter;

import android.content.Context;

import ru.solandme.washwait.mvp.main.domain.interactor.IMainInteractor;
import ru.solandme.washwait.mvp.main.domain.interactor.MainInteractor;
import ru.solandme.washwait.mvp.main.domain.model.WashForecastDTO;
import ru.solandme.washwait.mvp.main.domain.model.WeatherDTO;
import ru.solandme.washwait.mvp.main.presentation.view.IMainView;

public class MainPresenter implements IMainPresenter, IMainInteractor.Callback {

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
    public void onRefresh() {
        mainView.startProgress();
        mainInteractor.loadWeather();
    }

    @Override
    public void onSuccess(WeatherDTO weatherDTO, WashForecastDTO washForecastDTO) {
        mainView.stopProgress();
        //TODO сделать отдельные методы для заполнения каждой вьюшки. mainView.showCurrentMaxTemperature(temp)
        mainView.showCurrentMaxTemperature(weatherDTO.getCurrentWeather().getTempMax());
    }

    @Override
    public void onError(String errorMessage) {
        mainView.stopProgress();
        mainView.showError(errorMessage);
    }
}
