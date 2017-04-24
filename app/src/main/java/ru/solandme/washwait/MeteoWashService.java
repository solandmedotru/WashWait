package ru.solandme.washwait;

import android.app.IntentService;
import android.app.Notification;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import android.widget.RemoteViews;

import java.text.SimpleDateFormat;
import java.util.Locale;

import ru.solandme.washwait.model.washForecast.MyWeatherForecast;
import ru.solandme.washwait.network.IWeatherClient;
import ru.solandme.washwait.network.darksky.DarkSkyClient;
import ru.solandme.washwait.ui.MainActivity;
import ru.solandme.washwait.utils.SharedPrefsUtils;
import ru.solandme.washwait.widget.MeteoWashWidget;

public class MeteoWashService extends IntentService {

    private static final String TAG = MeteoWashService.class.getSimpleName();

    private MyWeatherForecast myWeatherForecast;

    private boolean isRunFromBackground;

    public MeteoWashService() {
        super(TAG);
    }

    public static void startActionGetForecast(Context context, boolean isRunFromBackground) {
        Intent intent = new Intent(context, MeteoWashService.class);
        intent.setAction(Constants.ACTION_GET_FORECAST);
        intent.putExtra(Constants.RUN_FROM, isRunFromBackground);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            isRunFromBackground = intent.getBooleanExtra(Constants.RUN_FROM, Constants.RUN_FROM_ACTIVITY);
            if (Constants.ACTION_GET_FORECAST.equals(action)) {
                handleForecast();
            }
        }
    }

    private void handleForecast() {

        String lang = Locale.getDefault().getLanguage();
        float lat = SharedPrefsUtils.getFloatPreference(this, getString(R.string.pref_lat_key), (float) Constants.DEFAULT_LATITUDE);
        float lon = SharedPrefsUtils.getFloatPreference(this, getString(R.string.pref_lon_key), (float) Constants.DEFAULT_LONGITUDE);
        String units = SharedPrefsUtils.getStringPreference(this, getString(R.string.pref_units_key), Constants.DEFAULT_UNITS);


        //TODO сделать выбор репозитория в зависимости от сохраненных параметров
//        IWeatherClient weatherClient = new OWMClient(getApplicationContext());
        IWeatherClient weatherClient = new DarkSkyClient(getApplicationContext());
        myWeatherForecast = weatherClient.getWeatherForecast(lat, lon, units, lang);
        publishResults(myWeatherForecast, isRunFromBackground);
    }

    boolean isBadConditions(double temperature, double dirtyCounter) {
        String units = SharedPrefsUtils.getStringPreference(this, getString(R.string.pref_units_key), Constants.DEFAULT_UNITS);
        double dirtyLimit = SharedPrefsUtils.getFloatPreference(this, getString(R.string.pref_dirty_limit_key), (float) Constants.DEFAULT_DIRTY_LIMIT);

        switch (units) {
            case "metric":
                return ((dirtyCounter > dirtyLimit) && temperature > -7) || (temperature < -15);
            case "imperial":
                return ((dirtyCounter > dirtyLimit) && temperature > 19) || (temperature < 5);
            default:
                return ((dirtyCounter > dirtyLimit) && temperature > 266) || (temperature < 258);
        }
    }

    private String getTextForWashForecast(int washDayNumber, double dataToWash) {
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

    private void publishResults(MyWeatherForecast myWeatherForecast, boolean runFromService) {
        Intent intent = new Intent(Constants.NOTIFICATION);
        String units = SharedPrefsUtils.getStringPreference(this, getString(R.string.pref_units_key), Constants.DEFAULT_UNITS);
        int textColor = SharedPrefsUtils.getIntegerPreference(this, getString(R.string.pref_textColor_key), Color.GRAY);
        int bgColor = SharedPrefsUtils.getIntegerPreference(this, getString(R.string.pref_bgColor_key), Color.BLACK);
        if (myWeatherForecast.isForecastResultOK() && myWeatherForecast.isCurrWeatherResultOK()) {
            int washDayNumber = getWashDayNumber();

            String textForWashForecast = getTextForWashForecast(washDayNumber, getWashData(washDayNumber));

            if (washDayNumber == Constants.FIRST_DAY_POSITION && runFromService)
                sendNotification(textForWashForecast);

            Log.d(TAG, "day: " + washDayNumber + " " + textForWashForecast);

            intent.putExtra("TextForecast", textForWashForecast);
            intent.putExtra("Weather", myWeatherForecast);

            Context context = this;
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            RemoteViews remoteViews =
                    new RemoteViews(context.getPackageName(), R.layout.meteo_wash_widget);
            ComponentName thisWidget = new ComponentName(context, MeteoWashWidget.class);

            double maxTemp = myWeatherForecast.getCurrentWeather().getTempMax();
            double minTemp = myWeatherForecast.getCurrentWeather().getTempMin();
            String description = myWeatherForecast.getCurrentWeather().getDescription();
            int icon = myWeatherForecast.getCurrentWeather().getImageRes();
            int humidity = (int) myWeatherForecast.getCurrentWeather().getHumidity();
            double barometer = myWeatherForecast.getCurrentWeather().getPressure();
            double speedWind = myWeatherForecast.getCurrentWeather().getWindSpeed();
            int speedDirection = (int) myWeatherForecast.getCurrentWeather().getWindDirection();

            SharedPrefsUtils.setStringPreference(this, getString(R.string.pref_maxTemp_key), String.valueOf(maxTemp));
            SharedPrefsUtils.setStringPreference(this, getString(R.string.pref_minTemp_key), String.valueOf(minTemp));
            SharedPrefsUtils.setStringPreference(this, getString(R.string.pref_description_key), description);
            SharedPrefsUtils.setStringPreference(this, getString(R.string.pref_icon_key), String.valueOf(icon));
            SharedPrefsUtils.setStringPreference(this, getString(R.string.pref_humidity_key), String.valueOf(humidity));
            SharedPrefsUtils.setStringPreference(this, getString(R.string.pref_barometer_key), String.valueOf(barometer));
            SharedPrefsUtils.setStringPreference(this, getString(R.string.pref_speedWind_key), String.valueOf(speedWind));
            SharedPrefsUtils.setStringPreference(this, getString(R.string.pref_speedDirection_key), String.valueOf(speedDirection));
            SharedPrefsUtils.setStringPreference(this, getString(R.string.pref_text_to_wash_key), textForWashForecast);

            remoteViews = MeteoWashWidget.fillWidget(context, textColor, bgColor, remoteViews, units, maxTemp, minTemp, description, icon,
                    humidity, barometer, speedWind, speedDirection, textForWashForecast);

            appWidgetManager.updateAppWidget(thisWidget, remoteViews);
        }
        intent.putExtra("isForecastResultOK", myWeatherForecast.isForecastResultOK());
        intent.putExtra("isCurrWeatherResultOK", myWeatherForecast.isCurrWeatherResultOK());

        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
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
        notificationManager.notify(Constants.NOTIFICATION_ID, notification);
    }

    private int getWashDayNumber() {

        String forecastDistance = SharedPrefsUtils.getStringPreference(this, getString(R.string.pref_limit_key), Constants.DEFAULT_FORECAST_DISTANCE);
        int washDayNumber = 15;
        int firstDirtyDay = -1;
        int clearDaysCounter = 0;
        int daysCounter = 0;

        for (int i = 0; i < myWeatherForecast.getMyWeatherList().size(); i++) {
            int weatherId = myWeatherForecast.getMyWeatherList().get(i).getId();
            double maxTemp = myWeatherForecast.getMyWeatherList().get(i).getTempMax();

            daysCounter++;
            if (!isBadConditions(maxTemp, myWeatherForecast.getMyWeatherList().get(i).getDirtyCounter())) {
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

    private double getWashData(int washDayNumber) {

        if (washDayNumber >= myWeatherForecast.getMyWeatherList().size())
            return myWeatherForecast.getMyWeatherList().get(myWeatherForecast.getMyWeatherList().size() - 1).getTime();
        return myWeatherForecast.getMyWeatherList().get(washDayNumber).getTime();
    }




}

