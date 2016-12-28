package ru.solandme.washwait.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.util.TypedValue;
import ru.solandme.washwait.R;

public class WeatherUtils {

  public static int getWindRes(int direction) {
    int val = (int) Math.round(((double) direction % 360) / 45);
    switch (val % 16) {
      case 0:
        return R.string.wi_wind_north;
      case 1:
        return R.string.wi_wind_north_east;
      case 2:
        return R.string.wi_wind_east;
      case 3:
        return R.string.wi_wind_south_east;
      case 4:
        return R.string.wi_wind_south;
      case 5:
        return R.string.wi_wind_south_west;
      case 6:
        return R.string.wi_wind_west;
      case 7:
        return R.string.wi_wind_north_west;
      case 8:
        return R.string.wi_wind_north;
    }
    return R.string.wi_wind_east;
  }

  public static String getStringWind(int speedDirection, double speedWind, String units,
      Context context) {
    String windUnits;
    switch (units) {
      case "metric":
        windUnits = String.format("%s", context.getString(R.string.meter_per_sec));
        break;
      case "imperial":
        windUnits = String.format("%s", context.getString(R.string.miles_per_h));
        break;
      default:
        windUnits = String.format("%s", context.getString(R.string.meter_per_sec));
        break;
    }
    return String.format("%s %s %s", context.getString(WeatherUtils.getWindRes(speedDirection)),
        (int) Math.round(speedWind), windUnits);
  }

  public static String getStringBarometer(double barometer, String units, Context context) {
    String barUnits;
    switch (units) {
      case "metric":
        barUnits = String.format("%s", context.getString(R.string.m_hg));
        barometer = Math.round(barometer * 100 / 133.3224);
        break;
      case "imperial":
        barUnits = String.format("%s", context.getString(R.string.h_pa));
        break;
      default:
        barUnits = String.format("%s", context.getString(R.string.m_hg));
        break;
    }
    return String.format("%s %s %s", context.getString(R.string.wi_barometer), (int) barometer,
        barUnits);
  }

  public static String getStringTemperature(double maxTemp, String units, Context context) {
    String unitTemperature;
    switch (units) {
      case "metric":
        unitTemperature = String.format("%s", context.getString(R.string.wi_celsius));
        break;
      case "imperial":
        unitTemperature = String.format("%s", context.getString(R.string.wi_fahrenheit));
        break;
      default:
        unitTemperature = String.format("%sK", "\u00b0");
        break;
    }
    return String.format("%s%s", (int) Math.round(maxTemp), unitTemperature);
  }

  public static Bitmap getFontBitmap(Context context, String text, int color, float fontSizeSP) {
    int fontSizePX = convertDiptoPix(context, fontSizeSP);
    int pad = (fontSizePX / 9);
    Paint paint = new Paint();
    Typeface typeface = Typeface.createFromAsset(context.getAssets(), "fonts/weatherFont.ttf");
    paint.setAntiAlias(true);
    paint.setTypeface(typeface);
    paint.setColor(color);
    paint.setTextSize(fontSizePX);

    int textWidth = (int) (paint.measureText(text) + pad * 2);
    int height = (int) (fontSizePX / 0.75);
    Bitmap bitmap = Bitmap.createBitmap(textWidth, height, Bitmap.Config.ARGB_4444);
    Canvas canvas = new Canvas(bitmap);
    float xOriginal = pad;
    canvas.drawText(text, xOriginal, fontSizePX, paint);
    return bitmap;
  }

  private static int convertDiptoPix(Context context, float dip) {
    int value = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dip,
        context.getResources().getDisplayMetrics());
    return value;
  }
}
