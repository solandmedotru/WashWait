package ru.solandme.washwait;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import ru.solandme.washwait.forecast.POJO.BigWeatherForecast;
import ru.solandme.washwait.forecast.POJO.Forecast;
import ru.solandme.washwait.rest.ForecastApiHelper;
import ru.solandme.washwait.rest.ForecastApiService;

public class ForecastService extends IntentService {

    private static final String TAG = ForecastService.class.getSimpleName();
    private static final String ACTION_GET_FORECAST = "ru.solandme.washwait.action.GET_FORECAST";
    private static final String DEFAULT_FORECAST_DISTANCE = "2";
    private static final String CNT = "16";
    private static final String DEFAULT_UNITS = "metric";

    private SharedPreferences sharedPref;
    private String lang = Locale.getDefault().getLanguage();
    private String appid = BuildConfig.OPEN_WEATHER_MAP_API_KEY;

    public static final double DEFAULT_LONGITUDE = 40.716667F;
    public static final double DEFAULT_LATITUDE = -74F;
    public static final String NOTIFICATION = "ru.solandme.washwait.service.receiver";
    String forecastDistance;
    BigWeatherForecast weather;

    ArrayList<Forecast> forecasts = new ArrayList<>();
    private boolean isResultOK;

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

                    generateForecast();
//                    saveForecastToDataBase();
                    isResultOK = true;
                    publishResults(isResultOK);

                }
            }

            @Override
            public void onFailure(Call<BigWeatherForecast> call, Throwable t) {
                Log.e(TAG, "onError: " + t);
                isResultOK = false;
                publishResults(isResultOK);
            }
        });


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
            default:
                return getResources().getString(R.string.not_wash);
        }
    }

    private void saveForecastToDataBase() {
//
//        WeatherDbHelper dbHelper = new WeatherDbHelper(this);
//        dbHelper.saveWeather(weather);
    }

    private void publishResults(boolean isResultOK) {
        Intent intent = new Intent(NOTIFICATION);
        if (isResultOK) {
            int washDayNumber = getWashDayNumber();
            String textForWashForecast = getTextForWashForecast(washDayNumber, getWashData(washDayNumber));
            Log.e(TAG, "day: " + washDayNumber + " " + textForWashForecast);
            intent.putExtra("TextForecast", textForWashForecast);
            intent.putExtra("Weather", forecasts);
            intent.putExtra("DirtyCounter", getDirtyCounter());

        }
        intent.putExtra("isResultOK", isResultOK);
        sendBroadcast(intent);
    }

    private int getWashDayNumber() {
        int washDayNumber = 15;
        int firstDirtyDay = -1;
        int clearDaysCounter = 0;
        int daysCounter = 0;

        for (int i = 0; i < forecasts.size(); i++) {
            int weatherId = forecasts.get(i).getWeatherId();
            double maxTemp = forecasts.get(i).getTemperature();

            daysCounter++;
            if (!isDirty(weatherId, maxTemp)) {
                clearDaysCounter++;
                if (clearDaysCounter == Integer.parseInt(forecastDistance)) {
                    if (washDayNumber == 15) {
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

    private long getWashData(int washDayNumber) {
        return forecasts.get(washDayNumber).getDate();
    }

    public void generateForecast() {

        if (null != weather) {
            forecasts.clear();
            int size = weather.getList().size();

            for (int i = 0; i < size; i++) {
                Forecast forecast = new Forecast();

                forecast.setWeatherId(weather.getList().get(i).getWeather().get(0).getId());
                forecast.setTemperature(weather.getList().get(i).getTemp().getMax());
                forecast.setDate(weather.getList().get(i).getDt() * 1000);
                forecast.setImageRes(getWeatherPicture(weather.getList().get(i).getWeather().get(0).getIcon()));
                forecast.setCityName(weather.getCity().getName());
                forecast.setCountry(weather.getCity().getCountry());
                forecast.setDescription(weather.getList().get(i).getWeather().get(0).getDescription());
                forecast.setLat(weather.getCity().getCoord().getLat());
                forecast.setLon(weather.getCity().getCoord().getLon());
                forecasts.add(forecast);
            }
        }
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

    public Double getDirtyCounter() {
        Double rainCounter = 0.0, snowCounter = 0.0;
        rainCounter = rainCounter + weather.getList().get(0).getRain();
        snowCounter = snowCounter + weather.getList().get(0).getSnow();

        Log.e(TAG, "dirtyCounter: " + (rainCounter + (snowCounter)) * 4);
        return (rainCounter + (snowCounter)) * 4;
    }

}

