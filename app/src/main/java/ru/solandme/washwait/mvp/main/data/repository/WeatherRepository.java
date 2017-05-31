package ru.solandme.washwait.mvp.main.data.repository;

import android.content.Context;

import ru.solandme.washwait.Constants;
import ru.solandme.washwait.R;
import ru.solandme.washwait.mvp.main.domain.model.WeatherDTO;
import ru.solandme.washwait.utils.SharedPrefsUtils;

public class WeatherRepository implements IWeatherClient.Callback{

    private WeatherRepository.Callback callback;
    private Context context;

    public WeatherRepository(WeatherRepository.Callback callback, Context context) {
        this.callback = callback;
        this.context = context;
    }

    public void requestWeatherForecast() {
        chooseWeatherClient().requestWeatherForecast();
    }

    private IWeatherClient chooseWeatherClient() {
        String weatherProvider = SharedPrefsUtils.getStringPreference(
                context,
                context.getString(R.string.pref_forecast_providers_key),
                Constants.DEFAULT_WEATHER_PROVIDER);

        switch (weatherProvider) {
            case "OpenWeatherMap":
                return new OWMClient(context, this);
            case "DarkSky":
                return new OWMClient(context, this);
            default:
                return new OWMClient(context, this);
        }
    }

    @Override
    public void onSuccess(WeatherDTO weatherDTO) {
        callback.onSuccess(weatherDTO);
    }

    @Override
    public void onError(String errorMessage) {
        callback.onError(errorMessage);
    }


    public interface Callback {
        void onSuccess(WeatherDTO weatherDTO);
        void onError(String errorMessage);
    }
}
