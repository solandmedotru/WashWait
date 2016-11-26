package ru.solandme.washwait;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;

import com.survivingwithandroid.weather.lib.WeatherClient;
import com.survivingwithandroid.weather.lib.WeatherConfig;
import com.survivingwithandroid.weather.lib.client.okhttp.WeatherDefaultClient;
import com.survivingwithandroid.weather.lib.exception.WeatherLibException;
import com.survivingwithandroid.weather.lib.exception.WeatherProviderInstantiationException;
import com.survivingwithandroid.weather.lib.model.DayForecast;
import com.survivingwithandroid.weather.lib.model.WeatherForecast;
import com.survivingwithandroid.weather.lib.provider.IProviderType;
import com.survivingwithandroid.weather.lib.provider.forecastio.ForecastIOProviderType;
import com.survivingwithandroid.weather.lib.provider.openweathermap.OpenweathermapProviderType;
import com.survivingwithandroid.weather.lib.provider.wunderground.WeatherUndergroundProviderType;
import com.survivingwithandroid.weather.lib.provider.yahooweather.YahooProviderType;
import com.survivingwithandroid.weather.lib.request.WeatherRequest;

import java.util.List;
import java.util.Locale;

public class ForecastService extends IntentService {

    private static final String ACTION_GET_FORECAST = "ru.solandme.washwait.action.GET_FORECAST";
    private static final String TAG = ForecastService.class.getSimpleName();

    private static final String WEATHER_PROVIDER_KEY = "weatherProvider";
    private static final String LONGITUDE = "longitude";
    private static final String LATITUDE = "latitude";
    private static final String UNITS = "units";
    public static final double DEFAULT_LONGITUDE = 40.716667F;
    public static final double DEFAULT_LATITUDE = -74F;

    WeatherClient client;
    List<DayForecast> dayForecastList;

    public ForecastService() {
        super(TAG);
    }

    public static void startActionGetForecast(Context context, String weatherProvider, double lon, double lat, String units) {
        Intent intent = new Intent(context, ForecastService.class);
        intent.setAction(ACTION_GET_FORECAST);
        intent.putExtra(WEATHER_PROVIDER_KEY, weatherProvider);
        intent.putExtra(LONGITUDE, lon);
        intent.putExtra(LATITUDE, lat);
        intent.putExtra(UNITS, units);
        context.startService(intent);
    }


    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_GET_FORECAST.equals(action)) {
                final String weatherProvider = intent.getStringExtra(WEATHER_PROVIDER_KEY);
                final double lon = intent.getDoubleExtra(LONGITUDE, DEFAULT_LONGITUDE);
                final double lat = intent.getDoubleExtra(LATITUDE, DEFAULT_LATITUDE);
                final String units = intent.getStringExtra(UNITS);
                handleForecast(weatherProvider, lon, lat, units);
            }
        }
    }

    private void handleForecast(String weatherProvider, double lon, double lat, String units) {

        WeatherClient.ClientBuilder builder = new WeatherClient.ClientBuilder();
        WeatherConfig config = new WeatherConfig();
        switch (units) {
            case "metric":
                config.unitSystem = WeatherConfig.UNIT_SYSTEM.M;
                break;
            case "imperial":
                config.unitSystem = WeatherConfig.UNIT_SYSTEM.I;
                break;
        }
        config.lang = Locale.getDefault().getLanguage().toLowerCase();
        config.numDays = 16;

        IProviderType forecastProviderType;
        switch (weatherProvider) {
            case "Openweathermap":
                forecastProviderType = new OpenweathermapProviderType();
                config.ApiKey = BuildConfig.OPEN_WEATHER_MAP_API_KEY;
                break;
            case "Forecast.io":
                forecastProviderType = new ForecastIOProviderType();
                config.ApiKey = BuildConfig.FORECAST_IO_API_KEY;
                break;
            case "Yahoo":
                forecastProviderType = new YahooProviderType();
                break;
            case "Weatherundergroung":
                forecastProviderType = new WeatherUndergroundProviderType();
                break;
            default:
                forecastProviderType = new OpenweathermapProviderType();
        }

        try {
            client = builder.attach(getApplicationContext())
                    .provider(forecastProviderType)
                    .httpClient(WeatherDefaultClient.class)
                    .config(config)
                    .build();
        } catch (WeatherProviderInstantiationException e) {
            e.printStackTrace();
        }


        client.getForecastWeather(new WeatherRequest(lon, lat), new WeatherClient.ForecastWeatherEventListener() {
            @Override
            public void onWeatherError(WeatherLibException wle) {
                wle.printStackTrace();
            }

            @Override
            public void onConnectionError(Throwable t) {
                t.printStackTrace();
            }

            @Override
            public void onWeatherRetrieved(WeatherForecast forecast) {
                dayForecastList = forecast.getForecast();
                saveForecastToDataBase(dayForecastList);
            }
        });
    }

    private void saveForecastToDataBase(List<DayForecast> dayForecastList) {

    }

}

