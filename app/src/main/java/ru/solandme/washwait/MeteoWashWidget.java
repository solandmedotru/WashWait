package ru.solandme.washwait;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.preference.PreferenceManager;
import android.widget.RemoteViews;
import ru.solandme.washwait.utils.WeatherUtils;

public class MeteoWashWidget extends AppWidgetProvider {

  static void updateAppWidget(Context context, AppWidgetManager appWidgetManager, int appWidgetId) {
    SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);

    // Construct the RemoteViews object
    RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.meteo_wash_widget);

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

    remoteViews.setImageViewResource(R.id.weather_icon_day0, icon);
    remoteViews.setImageViewBitmap(R.id.max_t_field, WeatherUtils.getFontBitmap(context,
        WeatherUtils.getStringTemperature(maxTemp, units, context), Color.WHITE, 34));
    remoteViews.setImageViewBitmap(R.id.min_t_field, WeatherUtils.getFontBitmap(context,
        WeatherUtils.getStringTemperature(minTemp, units, context), Color.WHITE, 34));
    remoteViews.setImageViewBitmap(R.id.barometer_field, WeatherUtils.getFontBitmap(context,
        WeatherUtils.getStringBarometer(barometer, units, context), Color.WHITE, 14));
    remoteViews.setImageViewBitmap(R.id.speed_wind_field, WeatherUtils.getFontBitmap(context,
        WeatherUtils.getStringWind(speedDirection, speedWind, units, context), Color.WHITE, 14));
    remoteViews.setImageViewBitmap(R.id.humidity_field, WeatherUtils.getFontBitmap(context,
        (context.getString(R.string.wi_humidity) + " " + humidity + "%"), Color.WHITE, 14));
    remoteViews.setImageViewBitmap(R.id.details_field,
        WeatherUtils.getFontBitmap(context, description, Color.WHITE, 14));
    remoteViews.setImageViewBitmap(R.id.forecast_message,
        WeatherUtils.getFontBitmap(context, textForWashForecast, Color.WHITE, 14));

    // Instruct the widget manager to update the widget
    appWidgetManager.updateAppWidget(appWidgetId, remoteViews);
  }

  @Override
  public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
    // There may be multiple widgets active, so update all of them
    ForecastService.startActionGetForecast(context, ForecastService.RUN_FROM_ACTIVITY);
    for (int appWidgetId : appWidgetIds) {
      updateAppWidget(context, appWidgetManager, appWidgetId);
    }
  }

  @Override public void onDeleted(Context context, int[] appWidgetIds) {
    // When the user deletes the widget, delete the preference associated with it.
    for (int appWidgetId : appWidgetIds) {
      MeteoWashWidgetConfigureActivity.deleteTitlePref(context, appWidgetId);
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

