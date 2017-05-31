package ru.solandme.washwait.versionOne.main;

import android.content.Context;

import ru.solandme.washwait.versionOne.model.WeatherDTO;

class MainInteractor implements IMainInteractor, OnWeatherUpdatedListener {

    private OnDTOUpdatedListener onDTOUpdatedListener;
    private IWeatherRepository weatherRepository;

    public MainInteractor(Context context, OnDTOUpdatedListener onDTOUpdatedListener) {
        this.onDTOUpdatedListener = onDTOUpdatedListener;
        this.weatherRepository = new OWMRepository(context, this);
    }


    @Override
    public void loadWeather() {
        weatherRepository.requestWeatherForecast();
    }

    @Override
    public void onSuccess(WeatherDTO weatherDTO) {
        //TODO сделать метод который после успешного получения погоды делает прогноз мыть или не мыть машину который возвращает WashForecastDTO
//        WashForecastDTO washForecastDTO = getWashForecast(weatherDTO);
//        onDTOUpdatedListener.onSuccess(weatherDTO, washForecastDTO);
    }

    @Override
    public void onError(String errorMessage) {
        onDTOUpdatedListener.onError(errorMessage);
    }
}
