package ru.solandme.washwait.mvp.main.data.repository;

import android.content.Context;
import android.os.Handler;
import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import ru.solandme.washwait.BuildConfig;
import ru.solandme.washwait.Constants;
import ru.solandme.washwait.R;
import ru.solandme.washwait.mvp.main.data.network.ApiFactory;
import ru.solandme.washwait.mvp.main.data.network.OWMService;
import ru.solandme.washwait.mvp.main.data.network.forecast.OpenWeatherForecast;
import ru.solandme.washwait.mvp.main.domain.model.Weather;
import ru.solandme.washwait.mvp.main.domain.model.WeatherDTO;
import ru.solandme.washwait.utils.FormatUtils;
import ru.solandme.washwait.utils.SharedPrefsUtils;

public class OWMClient implements IWeatherClient {
    private static final String TAG = OWMClient.class.getSimpleName();

    private static final String OPEN_WEATHER_MAP_API_KEY = BuildConfig.OPEN_WEATHER_MAP_API_KEY;
    private static final String BASE_URL = "http://api.openweathermap.org/data/2.5/";
    private static final int MAX_PERIOD = 16;

    private WeatherDTO weatherDTO;
    private final OWMService weatherService;
    private IWeatherClient.Callback callback;
    private Context context;

    public OWMClient(Context context, IWeatherClient.Callback callback) {
        this.callback = callback;
        this.context = context;
        ApiFactory.resetService();
        weatherService = ApiFactory.getWeatherService(context, BASE_URL).create(OWMService.class);
        weatherDTO = new WeatherDTO();
    }

    @Override
    public void requestWeatherForecast() {
        String lang = Locale.getDefault().getLanguage();
        float lat = SharedPrefsUtils.getFloatPreference(context, context.getString(R.string.pref_lat_key), (float) Constants.DEFAULT_LATITUDE);
        float lon = SharedPrefsUtils.getFloatPreference(context, context.getString(R.string.pref_lon_key), (float) Constants.DEFAULT_LONGITUDE);
        String units = SharedPrefsUtils.getStringPreference(context, context.getString(R.string.pref_units_key), Constants.DEFAULT_UNITS);


        Call<OpenWeatherForecast> weatherCall = weatherService.getForecastByCoordinats(
                String.valueOf(lat),
                String.valueOf(lon),
                units,
                lang,
                String.valueOf(MAX_PERIOD),
                OPEN_WEATHER_MAP_API_KEY);

        Handler mainHandler = new Handler(context.getMainLooper());
        try {
            OpenWeatherForecast forecast = weatherCall.execute().body();
            weatherDTO = new WeatherDTO();
            if (forecast != null) {

                Weather currentWeather = takeWeather(forecast, 0);
                List<Weather> weatherList = takeWeathersForAllPeriod(forecast);

                weatherDTO.setCurrentWeather(currentWeather);
                weatherDTO.setForecast(weatherList);



                mainHandler.post(() -> callback.onSuccess(weatherDTO));
            } else {
                mainHandler.post(() -> callback.onError(context.getString(R.string.error_from_response)));
            }
        } catch (IOException e) {
            e.printStackTrace();
            mainHandler.post(() -> callback.onError(e.getMessage()));
        }

//        weatherCall.enqueue(new retrofit2.Callback<OpenWeatherForecast>() {
//            @Override
//            public void onResponse(Call<OpenWeatherForecast> call, Response<OpenWeatherForecast> response) {
//                weatherDTO = new WeatherDTO();
//                if (response != null) {
//                    OpenWeatherForecast forecast = response.body();
//
//                    Weather currentWeather = takeWeather(forecast, 0);
//                    List<Weather> weatherList = takeWeathersForAllPeriod(forecast);
//
//                    weatherDTO.setCurrentWeather(currentWeather);
//                    weatherDTO.setForecast(weatherList);
//
//                    callback.onSuccess(weatherDTO);
//                } else {
//                    callback.onError(context.getString(R.string.error_from_response));
//                }
//            }
//
//            @Override
//            public void onFailure(Call<OpenWeatherForecast> call, Throwable t) {
//                callback.onError(t.getMessage());
//            }
//        });
    }

    private List<Weather> takeWeathersForAllPeriod(OpenWeatherForecast forecast) {
        //TODO доделать полное заполнение всех параметров погоды
        List<Weather> weatherList = new ArrayList<>();
        for (int i = 0; i < forecast.getList().size(); i++) {
            Weather weather = takeWeather(forecast, i);
            weatherList.add(weather);
        }

        return weatherList;
    }

    private Weather takeWeather(OpenWeatherForecast forecast, int day) {
        //TODO доделать полное заполнение всех параметров погоды
        String units = SharedPrefsUtils.getStringPreference(context, context.getString(R.string.pref_units_key), Constants.DEFAULT_UNITS);
        Weather weather = new Weather();

        weather.setTempMax(FormatUtils.getStringTemperature(context, forecast.getList().get(day).getTemp().getMax(), units));
        weather.setTempMin(FormatUtils.getStringTemperature(context, forecast.getList().get(day).getTemp().getMin(), units));

        weather.setWashDay(isWashDay(forecast, day));

        weather.setIcon(getWeatherPicture(forecast.getList().get(day).getWeather().get(0).getIcon()));

        return weather;
    }

    private boolean isWashDay(OpenWeatherForecast forecast, int day) {

        double dirtyLimit = SharedPrefsUtils.getFloatPreference(context, context.getString(R.string.pref_dirty_limit_key), (float) Constants.DEFAULT_DIRTY_LIMIT);
        int distance = Integer.parseInt(SharedPrefsUtils.getStringPreference(context, context.getString(R.string.pref_limit_key), Constants.DEFAULT_FORECAST_DISTANCE));
        boolean isWashDay = true;

        if (day + distance < forecast.getList().size()) {

            for (int i = day; i < day + distance; i++) {
                double rain = forecast.getList().get(i).getRain();
                double snow = forecast.getList().get(i).getSnow();
                double precipitation = calculatePrecipitation(rain, snow);
                Log.d(TAG, "dirty: " + i + " " + precipitation);

                if (precipitation > dirtyLimit) {
                    isWashDay = false;
                }
            }

            Log.d(TAG, "isWashDay in period: " + day + isWashDay);

            return isWashDay;
        } else {
            Log.d(TAG, "isWashDay out period: " + day + false);
            return false;
        }
    }

    @Override
    public void requestCurrentWeather() {
        //TODO доделать получение текущей погоды
    }


    private double calculatePrecipitation(double rain, double snow) {
        return (rain + (snow * 2)) * 4; //осадки за 12 часов, умножаем на 4 так, как приходят количество осадков за 3 часа
    }

    private int getWeatherPicture(String icon) {

        switch (icon) {
            case "01d":
                return R.mipmap.clear_d;
            case "01n":
                return R.mipmap.clear_n;
            case "02d":
                return R.mipmap.few_clouds_d;
            case "02n":
                return R.mipmap.few_clouds_n;
            case "03d":
                return R.mipmap.scattered_clouds;
            case "03n":
                return R.mipmap.scattered_clouds;
            case "04d":
                return R.mipmap.broken_clouds;
            case "04n":
                return R.mipmap.broken_clouds;
            case "09d":
                return R.mipmap.shower_rain_d;
            case "09n":
                return R.mipmap.shower_rain_n;
            case "10d":
                return R.mipmap.rain_d;
            case "10n":
                return R.mipmap.rain_n;
            case "11d":
                return R.mipmap.thunder_d;
            case "11n":
                return R.mipmap.thunder_n;
            case "13d":
                return R.mipmap.snow_d;
            case "13n":
                return R.mipmap.snow_n;
            case "50d":
                return R.mipmap.fog;
            case "50n":
                return R.mipmap.fog;
            default:
                return R.mipmap.few_clouds_d;
        }
    }
}
