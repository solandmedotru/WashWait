package ru.solandme.washwait;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import ru.solandme.washwait.data.WeatherDbHelper;
import ru.solandme.washwait.forecast.POJO.BigWeatherForecast;
import ru.solandme.washwait.rest.ForecastApiHelper;
import ru.solandme.washwait.rest.ForecastApiService;

public class ForecastService extends IntentService {

    private static final String TAG = ForecastService.class.getSimpleName();
    private static final String ACTION_GET_FORECAST = "ru.solandme.washwait.action.GET_FORECAST";
    private static final double DEFAULT_LONGITUDE = 40.716667F;
    private static final double DEFAULT_LATITUDE = -74F;
    private static final String DEFAULT_FORECAST_DISTANCE = "2";
    private static final String CNT = "16";
    private static final String DEFAULT_UNITS = "metric";

    private SharedPreferences sharedPref;
    private String lang = Locale.getDefault().getLanguage();
    private String appid = BuildConfig.OPEN_WEATHER_MAP_API_KEY;

    public static final String NOTIFICATION = "ru.solandme.washwait.service.receiver";
    String forecastDistance;

    BigWeatherForecast weather;

    public ForecastService() {
        super(TAG);
    }

    public static void startActionGetForecast(Context context) {
        Intent intent = new Intent(context, ForecastService.class);
        intent.setAction(ACTION_GET_FORECAST);
        context.startService(intent);
    }


    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_GET_FORECAST.equals(action)) {
                handleForecast();
            }
        }
    }

    private void handleForecast() {
        sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        float lat = sharedPref.getFloat("lat", (float) DEFAULT_LATITUDE);
        float lon = sharedPref.getFloat("lon", (float) DEFAULT_LONGITUDE);
        String units = sharedPref.getString("units", DEFAULT_UNITS);
        forecastDistance = sharedPref.getString(getString(R.string.pref_limit_key), DEFAULT_FORECAST_DISTANCE);
        String city = sharedPref.getString("city", getResources().getString(R.string.choose_location));

        final ForecastApiService apiService = ForecastApiHelper.requestForecast(getApplicationContext()).create(ForecastApiService.class);

        Call<BigWeatherForecast> weatherCall = apiService.getForecastByCoordinats(String.valueOf(lat), String.valueOf(lon), units, lang, CNT, appid);
        weatherCall.enqueue(new Callback<BigWeatherForecast>() {
            @Override
            public void onResponse(Call<BigWeatherForecast> call, Response<BigWeatherForecast> response) {
                if (response.isSuccessful()) {

                    weather = response.body();
                    saveForecastToDataBase();
                    publishResults(weather);

                }
            }

            @Override
            public void onFailure(Call<BigWeatherForecast> call, Throwable t) {
                Log.e(TAG, "onError: " + t);
            }
        });


    }

    private long getWashData(int washDayNumber) {
        return weather.getList().get(washDayNumber).getDt() * 1000;
    }

    private int getWashDayNumber() {
        int washDayNumber = -1;
        int firstDirtyDay = -1;
        int clearDaysCounter = 0;
        int daysCounter = 0;

        for (int i = 0; i < weather.getList().size(); i++) {
            int weatherId = weather.getList().get(i).getWeather().get(0).getId();
            double maxTemp = weather.getList().get(i).getTemp().getMax();

            daysCounter++;
            if (!isDirty(weatherId, maxTemp)) {
                clearDaysCounter++;
                if (clearDaysCounter == Integer.parseInt(forecastDistance)) {
                    if (washDayNumber == -1) {
                        washDayNumber = daysCounter - clearDaysCounter;
                    }
                }
            } else {
                clearDaysCounter = 0;
            }
        }

        Log.e(TAG, "day: " + washDayNumber + " " + firstDirtyDay);
        return washDayNumber;
    }

    boolean isDirty(int weatherId, double temperature) {
        return weatherId < 600 || weatherId < 700 && temperature > -10;
    }

    private String getTextForWashForecast(int washDayNumber, long dataToWash) {
        String dateToWashFormated = new SimpleDateFormat("dd MMMM, EE", Locale.getDefault()).format(dataToWash);
        switch (washDayNumber) {
            case 0:
                return getResources().getString(R.string.can_wash);
            case 1:
                return getResources().getString(R.string.wash, dateToWashFormated.toUpperCase());
            case 2:
                return getResources().getString(R.string.wash, dateToWashFormated.toUpperCase());
            case 3:
                return getResources().getString(R.string.wash, dateToWashFormated.toUpperCase());
            case 4:
                return getResources().getString(R.string.wash, dateToWashFormated.toUpperCase());
            case 5:
                return getResources().getString(R.string.wash, dateToWashFormated.toUpperCase());
            case 6:
                return getResources().getString(R.string.wash, dateToWashFormated.toUpperCase());
            case 7:
                return getResources().getString(R.string.wash, dateToWashFormated.toUpperCase());
            case 8:
                return getResources().getString(R.string.wash, dateToWashFormated.toUpperCase());
            case 9:
                return getResources().getString(R.string.wash, dateToWashFormated.toUpperCase());
            case 10:
                return getResources().getString(R.string.wash, dateToWashFormated.toUpperCase());
            case 11:
                return getResources().getString(R.string.wash, dateToWashFormated.toUpperCase());
            case 12:
                return getResources().getString(R.string.wash, dateToWashFormated.toUpperCase());
            case 13:
                return getResources().getString(R.string.wash, dateToWashFormated.toUpperCase());
            case 14:
                return getResources().getString(R.string.wash, dateToWashFormated.toUpperCase());
            case 15:
                return getResources().getString(R.string.wash, dateToWashFormated.toUpperCase());
            default:
                return getResources().getString(R.string.not_wash);
        }
    }

    private void saveForecastToDataBase() {

        WeatherDbHelper dbHelper = new WeatherDbHelper(this);
        dbHelper.saveWeather(weather);
    }

    private void publishResults(BigWeatherForecast weather) {
        int washDayNumber = getWashDayNumber();
        String textForWashForecast = getTextForWashForecast(washDayNumber, getWashData(washDayNumber));
        Log.e(TAG, "day: " + washDayNumber + " " + textForWashForecast);

        Intent intent = new Intent(NOTIFICATION);
        intent.putExtra("TextForecast", textForWashForecast);
        sendBroadcast(intent);
    }

}

