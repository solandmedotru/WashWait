package ru.solandme.washwait.versionOne.main;

import android.content.Context;

import ru.solandme.washwait.Constants;
import ru.solandme.washwait.R;
import ru.solandme.washwait.utils.SharedPrefsUtils;
import ru.solandme.washwait.versionOne.model.WashForecastDTO;
import ru.solandme.washwait.versionOne.model.WeatherDTO;

class MainInteractor implements IMainInteractor, OnWeatherUpdatedListener {

    private OnDTOUpdatedListener onDTOUpdatedListener;
    private Context context;

    public MainInteractor(Context context, OnDTOUpdatedListener onDTOUpdatedListener) {
        this.context = context;
        this.onDTOUpdatedListener = onDTOUpdatedListener;
    }

    @Override
    public void loadWeather() {
        chooseWeatherRepository().requestWeatherForecast();
    }

    private IWeatherRepository chooseWeatherRepository() {
        String weatherProvider = SharedPrefsUtils.getStringPreference(
                context,
                context.getString(R.string.pref_forecast_providers_key),
                Constants.DEFAULT_WEATHER_PROVIDER);

        switch (weatherProvider) {
            case "OpenWeatherMap":
                return new OWMRepository(context, this);
            case "DarkSky":
                return new OWMRepository(context, this);
            default:
                return new OWMRepository(context, this);
        }
    }

    @Override
    public void onSuccess(WeatherDTO weatherDTO) {
        //TODO сделать метод который после успешного получения погоды делает прогноз мыть или не мыть машину который возвращает WashForecastDTO
        WashForecastDTO washForecastDTO = new WashForecastDTO(); //как доделаю удалить
//        WashForecastDTO washForecastDTO = getWashForecast(weatherDTO);
        onDTOUpdatedListener.onSuccess(weatherDTO, washForecastDTO);
    }

    @Override
    public void onError(String errorMessage) {
        onDTOUpdatedListener.onError(errorMessage);
    }
}
