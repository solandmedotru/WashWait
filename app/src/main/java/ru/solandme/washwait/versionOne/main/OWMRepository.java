package ru.solandme.washwait.versionOne.main;

import android.content.Context;
import android.util.Log;

import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import ru.solandme.washwait.BuildConfig;
import ru.solandme.washwait.Constants;
import ru.solandme.washwait.R;
import ru.solandme.washwait.utils.SharedPrefsUtils;
import ru.solandme.washwait.versionOne.main.network.OWMService;
import ru.solandme.washwait.versionOne.main.network.RetrofitHelper;
import ru.solandme.washwait.versionOne.main.network.forecast.OpenWeatherForecast;
import ru.solandme.washwait.versionOne.model.WeatherDTO;

public class OWMRepository implements IWeatherRepository{
    private static final String TAG = OWMRepository.class.getSimpleName();

    private static final String OPEN_WEATHER_MAP_API_KEY = BuildConfig.OPEN_WEATHER_MAP_API_KEY;
    private static final String BASE_URL = "http://api.openweathermap.org/data/2.5/";
    private static final int MAX_PERIOD = 16;

    private WeatherDTO weatherDTO;
    private final OWMService apiService;
    private OnWeatherUpdatedListener listener;
    private Context context;

    public OWMRepository(Context context, OnWeatherUpdatedListener listener) {
        this.listener = listener;
        this.context = context;
        RetrofitHelper.resetRetrofit();
        apiService = RetrofitHelper.requestForecast(context, BASE_URL).create(OWMService.class);
        weatherDTO = new WeatherDTO();
    }


    @Override
    public void requestWeatherForecast() {
        String lang = Locale.getDefault().getLanguage();
        float lat = SharedPrefsUtils.getFloatPreference(context, context.getString(R.string.pref_lat_key), (float) Constants.DEFAULT_LATITUDE);
        float lon = SharedPrefsUtils.getFloatPreference(context, context.getString(R.string.pref_lon_key), (float) Constants.DEFAULT_LONGITUDE);
        String units = SharedPrefsUtils.getStringPreference(context, context.getString(R.string.pref_units_key), Constants.DEFAULT_UNITS);

        Call<OpenWeatherForecast> weatherCall = apiService.getForecastByCoordinats(
                String.valueOf(lat),
                String.valueOf(lon),
                units,
                lang,
                String.valueOf(MAX_PERIOD),
                OPEN_WEATHER_MAP_API_KEY);


        weatherCall.enqueue(new Callback<OpenWeatherForecast>() {
            @Override
            public void onResponse(Call<OpenWeatherForecast> call, Response<OpenWeatherForecast> response) {
                weatherDTO = new WeatherDTO();
                if (response !=null) {
                    OpenWeatherForecast forecast = response.body();
                    weatherDTO.getCurrentWeather().setTempMax(String.valueOf(forecast.getList().get(0).getTemp().getMax()));
                    //TODO доделать полное заполнение всех параметров погоды
                }
                listener.onSuccess(weatherDTO);
            }

            @Override
            public void onFailure(Call<OpenWeatherForecast> call, Throwable t) {
                listener.onError(t.getMessage());
            }
        });
    }

    @Override
    public void requestCurrentWeather() {
        //TODO доделать получение текущей погоды
    }


    private float calculatePrecipitation(float rain, float snow) {
        Log.e(TAG, "dirtyCounter: " + (rain + (snow * 2)) * 4);
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
