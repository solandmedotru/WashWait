package ru.solandme.washwait.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.widget.RemoteViews;

import ru.solandme.washwait.Constants;
import ru.solandme.washwait.MeteoWashService;
import ru.solandme.washwait.R;
import ru.solandme.washwait.ui.MainActivity;
import ru.solandme.washwait.utils.FormatUtils;
import ru.solandme.washwait.utils.SharedPrefsUtils;

public class MeteoWashWidget extends AppWidgetProvider {

    public static final String DEFAULT_NUMBER = "0";
    public static final String DEFAULT_STRING = "";

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager, int appWidgetId) {
        int textColor = SharedPrefsUtils.getIntegerPreference(context, context.getString(R.string.pref_textColor_key), Color.GRAY);
        int bgColor = SharedPrefsUtils.getIntegerPreference(context, context.getString(R.string.pref_bgColor_key), Color.BLACK);

        // Construct the RemoteViews object
        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.meteo_wash_widget);

        Intent intent = new Intent(context, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
        remoteViews.setOnClickPendingIntent(R.id.widgetContent, pendingIntent);

        String units = SharedPrefsUtils.getStringPreference(context, context.getString(R.string.pref_units_key), Constants.DEFAULT_UNITS);
        double maxTemp = Double.parseDouble(SharedPrefsUtils.getStringPreference(context, context.getString(R.string.pref_maxTemp_key), DEFAULT_NUMBER));
        double minTemp = Double.parseDouble(SharedPrefsUtils.getStringPreference(context, context.getString(R.string.pref_minTemp_key), DEFAULT_NUMBER));
        String description = SharedPrefsUtils.getStringPreference(context, context.getString(R.string.pref_description_key), DEFAULT_STRING);
        int icon = Integer.parseInt(
                SharedPrefsUtils.getStringPreference(context, context.getString(R.string.pref_icon_key), String.valueOf(R.mipmap.broken_clouds)));
        int humidity = Integer.parseInt(SharedPrefsUtils.getStringPreference(context, context.getString(R.string.pref_humidity_key), DEFAULT_NUMBER));
        double barometer = Double.parseDouble(SharedPrefsUtils.getStringPreference(context, context.getString(R.string.pref_barometer_key), DEFAULT_NUMBER));
        double speedWind = Double.parseDouble(SharedPrefsUtils.getStringPreference(context, context.getString(R.string.pref_speedWind_key), DEFAULT_NUMBER));
        int speedDirection = Integer.parseInt(SharedPrefsUtils.getStringPreference(context, context.getString(R.string.pref_speedDirection_key), DEFAULT_NUMBER));
        String textForWashForecast = SharedPrefsUtils.getStringPreference(context, context.getString(R.string.pref_text_to_wash_key), DEFAULT_STRING);

        remoteViews = fillWidget(context, textColor, bgColor, remoteViews, units, maxTemp, minTemp, description, icon,
                humidity, barometer, speedWind, speedDirection, textForWashForecast);

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, remoteViews);
    }

    public static RemoteViews fillWidget(Context context, int textColor, int bgColor, RemoteViews remoteViews,
                                         String units, double maxTemp, double minTemp, String description, int icon, int humidity,
                                         double barometer, double speedWind, int speedDirection, String textForWashForecast) {
        remoteViews.setImageViewResource(R.id.weather_icon_today, icon);
        remoteViews.setImageViewBitmap(R.id.max_t_field, FormatUtils.getFontBitmap(context,
                FormatUtils.getStringTemperature(context, maxTemp, units), textColor, 36));
        remoteViews.setTextViewText(R.id.separator, " | ");
        remoteViews.setTextColor(R.id.separator, textColor);
        remoteViews.setImageViewBitmap(R.id.min_t_field, FormatUtils.getFontBitmap(context,
                FormatUtils.getStringTemperature(context, minTemp, units), textColor, 36));
        remoteViews.setTextViewText(R.id.barometer_field, FormatUtils.getStringBarometer(context, barometer, units).substring(1));
        remoteViews.setTextColor(R.id.barometer_field, textColor);
        remoteViews.setTextViewText(R.id.speed_wind_field, FormatUtils.getStringWind(context, speedDirection, speedWind, units).substring(1));
        remoteViews.setTextColor(R.id.speed_wind_field, textColor);
        remoteViews.setTextViewText(R.id.humidity_field, (humidity + "%"));
        remoteViews.setTextColor(R.id.humidity_field, textColor);
        remoteViews.setTextViewText(R.id.details_field, description);
        remoteViews.setTextColor(R.id.details_field, textColor);
        remoteViews.setTextViewText(R.id.forecast_message, textForWashForecast);
        remoteViews.setTextColor(R.id.forecast_message, textColor);

        remoteViews.setInt(R.id.widgetContent, "setBackgroundColor", bgColor);
        return remoteViews;
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        MeteoWashService.startServiceForGetForecast(context, Constants.RUN_FROM_ACTIVITY);
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
        MeteoWashService.startServiceForGetForecast(context, Constants.RUN_FROM_ACTIVITY);
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }
}

