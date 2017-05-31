package ru.solandme.washwait.mvp.main.domain.interactor;

import android.content.Context;

import ru.solandme.washwait.mvp.main.data.repository.WeatherRepository;
import ru.solandme.washwait.mvp.main.domain.model.WashForecastDTO;
import ru.solandme.washwait.mvp.main.domain.model.WeatherDTO;

public class MainInteractor implements IMainInteractor, WeatherRepository.Callback {

    private IMainInteractor.Callback callback;
    private WeatherRepository weatherRepository;

    public MainInteractor(Context context, IMainInteractor.Callback callback) {
        this.callback = callback;
        weatherRepository = new WeatherRepository(this, context);
    }

    @Override
    public void loadWeather() {
        weatherRepository.requestWeatherForecast();
    }

    @Override
    public void onSuccess(WeatherDTO weatherDTO) {
        //TODO сделать метод который после успешного получения погоды делает прогноз мыть или не мыть машину который возвращает WashForecastDTO
        WashForecastDTO washForecastDTO = new WashForecastDTO(); //как доделаю удалить
//        WashForecastDTO washForecastDTO = getWashForecast(weatherDTO);
        callback.onSuccess(weatherDTO, washForecastDTO);
    }

    @Override
    public void onError(String errorMessage) {
        callback.onError(errorMessage);
    }
}
