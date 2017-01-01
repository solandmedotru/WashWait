package ru.solandme.washwait;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.preference.PreferenceManager;
import android.widget.RemoteViews;

import ru.solandme.washwait.utils.WeatherUtils;

public class MeteoWashWidget extends AppWidgetProvider {

  static void updateAppWidget(Context context, AppWidgetManager appWidgetManager, int appWidgetId) {
    SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
    int textColor = sharedPref.getInt("pref_textColor_key", Color.GRAY);
    int bgColor = sharedPref.getInt("pref_bgColor_key", Color.BLACK);

    // Construct the RemoteViews object
    RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.meteo_wash_widget);

    Intent intent = new Intent(context, MainActivity.class);
    PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
    remoteViews.setOnClickPendingIntent(R.id.widgetContent, pendingIntent);

    String units = sharedPref.getString("units", ForecastService.DEFAULT_UNITS);
    double maxTemp = Double.parseDouble(sharedPref.getString("pref_maxTemp_key", "0"));
    double minTemp = Double.parseDouble(sharedPref.getString("pref_minTemp_key", "0"));
    String description = sharedPref.getString("pref_description_key", "");
    int icon = Integer.parseInt(
        sharedPref.getString("pref_icon_key", String.valueOf(R.mipmap.broken_clouds)));
    int humidity = Integer.parseInt(sharedPref.getString("pref_humidity_key", "0"));
    double barometer = Double.parseDouble(sharedPref.getString("pref_barometer_key", "0"));
    double speedWind = Double.parseDouble(sharedPref.getString("pref_speedWind_key", "0"));
    int speedDirection = Integer.parseInt(sharedPref.getString("pref_speedDirection_key", "0"));
    String textForWashForecast = sharedPref.getString("pref_text_to_wash_key", "");

    remoteViews = fillWidget(context, textColor, bgColor, remoteViews, units, maxTemp, minTemp, description, icon,
            humidity, barometer, speedWind, speedDirection, textForWashForecast);

    // Instruct the widget manager to update the widget
    appWidgetManager.updateAppWidget(appWidgetId, remoteViews);
  }

  public static RemoteViews fillWidget(Context context, int textColor, int bgColor, RemoteViews remoteViews,
      String units, double maxTemp, double minTemp, String description, int icon, int humidity,
      double barometer, double speedWind, int speedDirection, String textForWashForecast) {
    remoteViews.setImageViewResource(R.id.weather_icon_day0, icon);
    remoteViews.setImageViewBitmap(R.id.max_t_field, WeatherUtils.getFontBitmap(context,
        WeatherUtils.getStringTemperature(maxTemp, units, context), textColor, 24));
    remoteViews.setImageViewBitmap(R.id.separator,
        WeatherUtils.getFontBitmap(context, " | ", textColor, 20));
    remoteViews.setImageViewBitmap(R.id.min_t_field, WeatherUtils.getFontBitmap(context,
        WeatherUtils.getStringTemperature(minTemp, units, context), textColor, 24));
    remoteViews.setImageViewBitmap(R.id.barometer_field, WeatherUtils.getFontBitmap(context,
        WeatherUtils.getStringBarometer(barometer, units, context), textColor, 14));
    remoteViews.setImageViewBitmap(R.id.speed_wind_field, WeatherUtils.getFontBitmap(context,
        WeatherUtils.getStringWind(speedDirection, speedWind, units, context), textColor, 14));
    remoteViews.setImageViewBitmap(R.id.humidity_field, WeatherUtils.getFontBitmap(context,
        (context.getString(R.string.wi_humidity) + " " + humidity + "%"), textColor, 14));

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
    ForecastService.startActionGetForecast(context, ForecastService.RUN_FROM_ACTIVITY);
    for (int appWidgetId : appWidgetIds) {
      updateAppWidget(context, appWidgetManager, appWidgetId);
    }
  }

  @Override public void onEnabled(Context context) {
    // Enter relevant functionality for when the first widget is created
    ForecastService.startActionGetForecast(context, ForecastService.RUN_FROM_ACTIVITY);
  }

  @Override public void onDisabled(Context context) {
    // Enter relevant functionality for when the last widget is disabled
  }
}

