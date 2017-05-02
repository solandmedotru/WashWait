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

import java.util.Locale;

import ru.solandme.washwait.model.washForecast.MyWeatherForecast;
import ru.solandme.washwait.model.washForecast.WashForecast;
import ru.solandme.washwait.network.IWeatherClient;
import ru.solandme.washwait.network.OpenWeather.OWMClient;
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
        String forecastDistance = SharedPrefsUtils.getStringPreference(this, getString(R.string.pref_limit_key), Constants.DEFAULT_FORECAST_DISTANCE);
        float precipitationLimit = SharedPrefsUtils.getFloatPreference(this, getString(R.string.pref_dirty_limit_key), (float) Constants.DEFAULT_DIRTY_LIMIT);


        //TODO сделать выбор репозитория в зависимости от сохраненных параметров
        IWeatherClient weatherClient;
        if (true) {
            weatherClient = new DarkSkyClient(getApplicationContext());
            if(units.equals("metric")) myWeatherForecast = weatherClient.getWeatherForecast(lat, lon, "si", lang);
            if(units.equals("imperial")) myWeatherForecast = weatherClient.getWeatherForecast(lat, lon, "us", lang);
        } else {
            weatherClient = new OWMClient(getApplicationContext());
            myWeatherForecast = weatherClient.getWeatherForecast(lat, lon, units, lang);
        }

        WashForecast washForecast = new WashForecast(this, myWeatherForecast, forecastDistance, precipitationLimit);
        publishResults(myWeatherForecast, washForecast, isRunFromBackground);
    }


    private void publishResults(MyWeatherForecast myWeatherForecast, WashForecast washForecast, boolean runFromService) {
        Intent intent = new Intent(Constants.NOTIFICATION);
        String units = SharedPrefsUtils.getStringPreference(this, getString(R.string.pref_units_key), Constants.DEFAULT_UNITS);
        int textColor = SharedPrefsUtils.getIntegerPreference(this, getString(R.string.pref_textColor_key), Color.GRAY);
        int bgColor = SharedPrefsUtils.getIntegerPreference(this, getString(R.string.pref_bgColor_key), Color.BLACK);
        if (myWeatherForecast.isForecastResultOK() && myWeatherForecast.isCurrWeatherResultOK()) {

            if (washForecast.getWashDayNumber() == Constants.FIRST_DAY_POSITION && runFromService)
                sendNotification(washForecast.getText());

            Log.d(TAG, "day: " + washForecast.getWashDayNumber() + " " + washForecast.getText());

            intent.putExtra("TextForecast", washForecast.getText());
            intent.putExtra("Weather", myWeatherForecast);

            Context context = this;
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.meteo_wash_widget);
            ComponentName thisWidget = new ComponentName(context, MeteoWashWidget.class);

            double maxTemp = myWeatherForecast.getMyWeatherList().get(0).getTempMax();
            double minTemp = myWeatherForecast.getMyWeatherList().get(0).getTempMin();
            String description = myWeatherForecast.getMyWeatherList().get(0).getDescription();
            int icon = myWeatherForecast.getMyWeatherList().get(0).getImageRes();
            int humidity = (int) myWeatherForecast.getMyWeatherList().get(0).getHumidity();
            double barometer = myWeatherForecast.getMyWeatherList().get(0).getPressure();
            double speedWind = myWeatherForecast.getMyWeatherList().get(0).getWindSpeed();
            int speedDirection = (int) myWeatherForecast.getMyWeatherList().get(0).getWindDirection();

            SharedPrefsUtils.setStringPreference(this, getString(R.string.pref_maxTemp_key), String.valueOf(maxTemp));
            SharedPrefsUtils.setStringPreference(this, getString(R.string.pref_minTemp_key), String.valueOf(minTemp));
            SharedPrefsUtils.setStringPreference(this, getString(R.string.pref_description_key), description);
            SharedPrefsUtils.setStringPreference(this, getString(R.string.pref_icon_key), String.valueOf(icon));
            SharedPrefsUtils.setStringPreference(this, getString(R.string.pref_humidity_key), String.valueOf(humidity));
            SharedPrefsUtils.setStringPreference(this, getString(R.string.pref_barometer_key), String.valueOf(barometer));
            SharedPrefsUtils.setStringPreference(this, getString(R.string.pref_speedWind_key), String.valueOf(speedWind));
            SharedPrefsUtils.setStringPreference(this, getString(R.string.pref_speedDirection_key), String.valueOf(speedDirection));
            SharedPrefsUtils.setStringPreference(this, getString(R.string.pref_text_to_wash_key), washForecast.getText());

            remoteViews = MeteoWashWidget.fillWidget(context, textColor, bgColor, remoteViews, units, maxTemp, minTemp, description, icon,
                    humidity, barometer, speedWind, speedDirection, washForecast.getText());

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
}

