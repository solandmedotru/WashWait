package ru.solandme.washwait;

import android.app.IntentService;
import android.app.Notification;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import android.widget.RemoteViews;
import java.text.SimpleDateFormat;
import java.util.Locale;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import ru.solandme.washwait.POJO.forecast.WeatherForecast;
import ru.solandme.washwait.POJO.weather.CurrWeather;
import ru.solandme.washwait.data.WeatherDbHelper;
import ru.solandme.washwait.rest.ForecastApiHelper;
import ru.solandme.washwait.rest.ForecastApiService;

public class ForecastService extends IntentService {

    private static final String TAG = ForecastService.class.getSimpleName();
    private static final String ACTION_GET_FORECAST = "ru.solandme.washwait.action.GET_FORECAST";
    private static final String DEFAULT_FORECAST_DISTANCE = "2";
    private static final String CNT = "16";
    private static final String APPID = BuildConfig.OPEN_WEATHER_MAP_API_KEY;
    private static final int NOTIFICATION_ID = 1981;
    private String forecastDistance;
    private WeatherForecast weatherForecast;
    private CurrWeather currWeather;
    private boolean isForecastResultOK;
    private boolean isCurrWeatherResultOK;
    private boolean isFinishedCurrWeather;
    private boolean isFinishedForecast;
    private boolean isRunFromBackground;
    private SharedPreferences sharedPref;

    public static final String RUN_FROM = "isRunFromBackground";
    public static final String NOTIFICATION = "ru.solandme.washwait.service.receiver";
    public static final boolean RUN_FROM_ACTIVITY = false;
    public static final boolean RUN_FROM_BACKGROUND = true;
    public static final double DEFAULT_LONGITUDE = 40.716667F;
    public static final double DEFAULT_LATITUDE = -74F;
    public static final String DEFAULT_UNITS = "metric";

    public ForecastService() {
        super(TAG);
    }

    public static void startActionGetForecast(Context context, boolean isRunFromBackground) {
        Intent intent = new Intent(context, ForecastService.class);
        intent.setAction(ACTION_GET_FORECAST);
        intent.putExtra(RUN_FROM, isRunFromBackground);
        context.startService(intent);
    }


    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            isRunFromBackground = intent.getBooleanExtra(RUN_FROM, ForecastService.RUN_FROM_ACTIVITY);
            if (ACTION_GET_FORECAST.equals(action)) {
                handleForecast();
            }
        }
    }

    private void handleForecast() {
        sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        String lang = Locale.getDefault().getLanguage();
        float lat = sharedPref.getFloat("lat", (float) DEFAULT_LATITUDE);
        float lon = sharedPref.getFloat("lon", (float) DEFAULT_LONGITUDE);
        String units = sharedPref.getString("units", DEFAULT_UNITS);
        forecastDistance = sharedPref.getString(getString(R.string.pref_limit_key), DEFAULT_FORECAST_DISTANCE);

        final ForecastApiService apiService = ForecastApiHelper.requestForecast(getApplicationContext()).create(ForecastApiService.class);

        Call<WeatherForecast> weatherCall = apiService.getForecastByCoordinats(String.valueOf(lat), String.valueOf(lon), units, lang, CNT, APPID);
        weatherCall.enqueue(new Callback<WeatherForecast>() {
            @Override
            public void onResponse(Call<WeatherForecast> call, Response<WeatherForecast> response) {
                if (response.isSuccessful()) {

                    weatherForecast = response.body();

                    int size = weatherForecast.getList().size();

                    for (int i = 0; i < size; i++) {
                        weatherForecast.getList().get(i).setImageRes(getWeatherPicture(weatherForecast.getList().get(i).getWeather().get(0).getIcon()));
                        weatherForecast.getList().get(i).setDirtyCounter(getDirtyCounter(i));
                    }

                    saveForecastToDataBase(weatherForecast);

                    isForecastResultOK = true;
                } else {
                    isForecastResultOK = false;
                }
                isFinishedForecast = true;
                publishResults(isForecastResultOK, isCurrWeatherResultOK, isRunFromBackground);
            }

            @Override
            public void onFailure(Call<WeatherForecast> call, Throwable t) {
                Log.e(TAG, "onError: " + t);
                isForecastResultOK = false;
                isFinishedForecast = true;
                publishResults(isForecastResultOK, isCurrWeatherResultOK, isRunFromBackground);
            }
        });

        Call<CurrWeather> currWeatherCall = apiService.getCurrentWeatherByCoordinats(String.valueOf(lat), String.valueOf(lon), units, lang, APPID);
        currWeatherCall.enqueue(new Callback<CurrWeather>() {
            @Override
            public void onResponse(Call<CurrWeather> call, Response<CurrWeather> response) {
                if (response.isSuccessful()) {
                    Log.e(TAG, "onResponse: current");

                    currWeather = response.body();
                    currWeather.getWeather().get(0).getIcon();
                    currWeather.setImageRes(getWeatherPicture(currWeather.getWeather().get(0).getIcon()));

                    isCurrWeatherResultOK = true;
                } else {
                    Log.e(TAG, "onElse: current");
                    isCurrWeatherResultOK = false;
                }
                isFinishedCurrWeather = true;
                publishResults(isForecastResultOK, isCurrWeatherResultOK, isRunFromBackground);
            }

            @Override
            public void onFailure(Call<CurrWeather> call, Throwable t) {
                Log.e(TAG, "onError: current" + t);
                isCurrWeatherResultOK = false;
                isFinishedCurrWeather = true;
                publishResults(isForecastResultOK, isCurrWeatherResultOK, isRunFromBackground);
            }
        });
    }

    boolean isBadConditions(int weatherId, double temperature) {
        String units = sharedPref.getString(getString(R.string.pref_units_key), DEFAULT_UNITS);

        switch (units) {
            case "metric":
                return (weatherId < 600) || (weatherId < 700 && temperature > -7) || (temperature < -15);
            case "imperial":
                return (weatherId < 600) || (weatherId < 700 && temperature > 19) || (temperature < 5);
            default:
                return (weatherId < 600) || (weatherId < 700 && temperature > 266) || (temperature < 258);
        }
    }

    private String getTextForWashForecast(int washDayNumber, long dataToWash) {
        String dateToWashFormat = new SimpleDateFormat("dd MMMM, EE", Locale.getDefault()).format(dataToWash * 1000);
        switch (washDayNumber) {
            case 0:
                return getResources().getString(R.string.can_wash);
            case 1:
                return getResources().getString(R.string.wash, dateToWashFormat.toUpperCase());
            case 2:
                return getResources().getString(R.string.wash, dateToWashFormat.toUpperCase());
            case 3:
                return getResources().getString(R.string.wash, dateToWashFormat.toUpperCase());
            case 4:
                return getResources().getString(R.string.wash, dateToWashFormat.toUpperCase());
            case 5:
                return getResources().getString(R.string.wash, dateToWashFormat.toUpperCase());
            case 6:
                return getResources().getString(R.string.wash, dateToWashFormat.toUpperCase());
            case 7:
                return getResources().getString(R.string.wash, dateToWashFormat.toUpperCase());
            case 8:
                return getResources().getString(R.string.wash, dateToWashFormat.toUpperCase());
            case 9:
                return getResources().getString(R.string.wash, dateToWashFormat.toUpperCase());
            case 10:
                return getResources().getString(R.string.wash, dateToWashFormat.toUpperCase());
            case 11:
                return getResources().getString(R.string.wash, dateToWashFormat.toUpperCase());
            case 12:
                return getResources().getString(R.string.wash, dateToWashFormat.toUpperCase());
            case 13:
                return getResources().getString(R.string.wash, dateToWashFormat.toUpperCase());
            case 14:
                return getResources().getString(R.string.wash, dateToWashFormat.toUpperCase());
            default:
                return getResources().getString(R.string.not_wash);
        }
    }

    private void saveForecastToDataBase(WeatherForecast weatherForecast) {

        WeatherDbHelper dbHelper = new WeatherDbHelper(this);
        dbHelper.saveWeather(weatherForecast);
        dbHelper.close();
    }

    private void publishResults(boolean isForecastResultOK, boolean isCurrWeatherResultOK, boolean runFromService) {
        Intent intent = new Intent(NOTIFICATION);
        String units = sharedPref.getString("units", DEFAULT_UNITS);
        int textColor = sharedPref.getInt("pref_textColor_key", Color.GRAY);
        if (isForecastResultOK && isCurrWeatherResultOK) {
            int washDayNumber = getWashDayNumber();

            String textForWashForecast = getTextForWashForecast(washDayNumber, getWashData(washDayNumber));

            if (washDayNumber == 0 && runFromService) sendNotification(textForWashForecast);

            Log.e(TAG, "day: " + washDayNumber + " " + textForWashForecast);

            intent.putExtra("TextForecast", textForWashForecast);
            intent.putExtra("Weather", weatherForecast);
            intent.putExtra("CurrWeather", currWeather);

            Context context = this;
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            RemoteViews remoteViews =
                new RemoteViews(context.getPackageName(), R.layout.meteo_wash_widget);
            ComponentName thisWidget = new ComponentName(context, MeteoWashWidget.class);

            double maxTemp = currWeather.getMain().getTempMax();
            double minTemp = currWeather.getMain().getTempMin();
            String description = currWeather.getWeather().get(0).getDescription();
            int icon = currWeather.getImageRes();
            int humidity = currWeather.getMain().getHumidity();
            double barometer = currWeather.getMain().getPressure();
            double speedWind = currWeather.getWind().getSpeed();
            int speedDirection = (int) currWeather.getWind().getDeg();

            sharedPref.edit().putString("pref_maxTemp_key", String.valueOf(maxTemp)).apply();
            sharedPref.edit().putString("pref_minTemp_key", String.valueOf(minTemp)).apply();
            sharedPref.edit().putString("pref_description_key", description).apply();
            sharedPref.edit().putString("pref_icon_key", String.valueOf(icon)).apply();
            sharedPref.edit().putString("pref_humidity_key", String.valueOf(humidity)).apply();
            sharedPref.edit().putString("pref_barometer_key", String.valueOf(barometer)).apply();
            sharedPref.edit().putString("pref_speedWind_key", String.valueOf(speedWind)).apply();
            sharedPref.edit()
                .putString("pref_speedDirection_key", String.valueOf(speedDirection))
                .apply();
            sharedPref.edit().putString("pref_text_to_wash_key", textForWashForecast).apply();

            MeteoWashWidget.fillWidget(context, textColor, remoteViews, units, maxTemp, minTemp,
                description, icon, humidity, barometer, speedWind, speedDirection,
                textForWashForecast);

            appWidgetManager.updateAppWidget(thisWidget, remoteViews);
        }
        intent.putExtra("isForecastResultOK", isForecastResultOK);
        intent.putExtra("isCurrWeatherResultOK", isCurrWeatherResultOK);

        if (isFinishedCurrWeather && isFinishedForecast) {
            LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
        }


    }

    private void sendNotification(String textForWashForecast) {

        Intent notificationIntent = new Intent(this, MainActivity.class);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent contentIntent = PendingIntent.getActivity(this,
                0, notificationIntent, PendingIntent.FLAG_CANCEL_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);

        Notification notification = builder.setContentIntent(contentIntent)
                .setContentTitle(getString(R.string.app_name))
                .setTicker(textForWashForecast)
                .setContentText(textForWashForecast)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher))
                .setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_SOUND)
                .setWhen(System.currentTimeMillis()).build();

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.notify(NOTIFICATION_ID, notification);
    }

    private int getWashDayNumber() {
        int washDayNumber = 15;
        int firstDirtyDay = -1;
        int clearDaysCounter = 0;
        int daysCounter = 0;

        for (int i = 0; i < weatherForecast.getList().size(); i++) {
            int weatherId = weatherForecast.getList().get(i).getWeather().get(0).getId();
            double maxTemp = weatherForecast.getList().get(i).getTemp().getMax();

            daysCounter++;
            if (!isBadConditions(weatherId, maxTemp)) {
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

        if (washDayNumber >= weatherForecast.getList().size())
            return weatherForecast.getList().get(weatherForecast.getList().size() - 1).getDt();
        return weatherForecast.getList().get(washDayNumber).getDt();
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

    public double getDirtyCounter(int position) {
        double rainCounter = 0.0, snowCounter = 0.0;
        rainCounter = rainCounter + weatherForecast.getList().get(position).getRain();
        snowCounter = snowCounter + weatherForecast.getList().get(position).getSnow();

        Log.e(TAG, "dirtyCounter: " + (rainCounter + (snowCounter)) * 4);
        return (rainCounter + (snowCounter)) * 4;
    }

}

