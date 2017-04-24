package ru.solandme.washwait.model.washForecast;

import android.content.Context;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Locale;

import ru.solandme.washwait.Constants;
import ru.solandme.washwait.R;
import ru.solandme.washwait.utils.SharedPrefsUtils;

public class WashForecast {
    private static final String TAG = WashForecast.class.getSimpleName();
    private String text;
    private int washDayNumber;
    private Context context;
    private MyWeatherForecast myWeatherForecast;
    private double dirtyLimit;

    public WashForecast(Context context, MyWeatherForecast myWeatherForecast, String forecastDistance) {
        this.context = context;
        this.myWeatherForecast = myWeatherForecast;
        this.washDayNumber = getWashDayByDistance(forecastDistance);
        this.dirtyLimit = SharedPrefsUtils.getFloatPreference(context, context.getString(R.string.pref_dirty_limit_key), (float) Constants.DEFAULT_DIRTY_LIMIT);
        this.text = getTextForWashForecast(washDayNumber, getWashData(washDayNumber));
//        this.units = SharedPrefsUtils.getStringPreference(context, context.getString(R.string.pref_units_key), Constants.DEFAULT_UNITS);
    }

    public int getWashDayNumber() {
        return washDayNumber;
    }

    public String getText() {
        return text;
    }

    private String getTextForWashForecast(int washDayNumber, double dataToWash) {
        String dateToWashFormat = new SimpleDateFormat("dd MMMM, EE", Locale.getDefault()).format(dataToWash * 1000);
        switch (washDayNumber) {
            case 0:
                return context.getResources().getString(R.string.can_wash);
            case 1:
                return context.getResources().getString(R.string.wash, dateToWashFormat.toUpperCase());
            case 2:
                return context.getResources().getString(R.string.wash, dateToWashFormat.toUpperCase());
            case 3:
                return context.getResources().getString(R.string.wash, dateToWashFormat.toUpperCase());
            case 4:
                return context.getResources().getString(R.string.wash, dateToWashFormat.toUpperCase());
            case 5:
                return context.getResources().getString(R.string.wash, dateToWashFormat.toUpperCase());
            case 6:
                return context.getResources().getString(R.string.wash, dateToWashFormat.toUpperCase());
            case 7:
                return context.getResources().getString(R.string.wash, dateToWashFormat.toUpperCase());
            case 8:
                return context.getResources().getString(R.string.wash, dateToWashFormat.toUpperCase());
            case 9:
                return context.getResources().getString(R.string.wash, dateToWashFormat.toUpperCase());
            case 10:
                return context.getResources().getString(R.string.wash, dateToWashFormat.toUpperCase());
            case 11:
                return context.getResources().getString(R.string.wash, dateToWashFormat.toUpperCase());
            case 12:
                return context.getResources().getString(R.string.wash, dateToWashFormat.toUpperCase());
            case 13:
                return context.getResources().getString(R.string.wash, dateToWashFormat.toUpperCase());
            case 14:
                return context.getResources().getString(R.string.wash, dateToWashFormat.toUpperCase());
            default:
                return context.getResources().getString(R.string.not_wash);
        }
    }

    private double getWashData(int washDayNumber) {

        if (washDayNumber >= myWeatherForecast.getMyWeatherList().size())
            return myWeatherForecast.getMyWeatherList().get(myWeatherForecast.getMyWeatherList().size() - 1).getTime();
        return myWeatherForecast.getMyWeatherList().get(washDayNumber).getTime();
    }

    private int getWashDayByDistance(String forecastDistance) {

        int washDayNumber = myWeatherForecast.getMaxPeriod() - 1;
        int firstDirtyDay = -1;
        int clearDaysCounter = 0;
        int daysCounter = 0;

        for (int i = 0; i < myWeatherForecast.getMyWeatherList().size(); i++) {
            double maxTemp = myWeatherForecast.getMyWeatherList().get(i).getTempMax();

            daysCounter++;
            if (!isBadConditions(maxTemp, myWeatherForecast.getMyWeatherList().get(i).getDirtyCounter(), myWeatherForecast.getUnits())) {
                clearDaysCounter++;
                if (clearDaysCounter == Integer.parseInt(forecastDistance)) {
                    if (washDayNumber == myWeatherForecast.getMaxPeriod() - 1) {
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

    private boolean isBadConditions(double temperature, double dirtyCounter, String units) {
        switch (units) {
            case "metric":
                return ((dirtyCounter > dirtyLimit) && temperature > -7) || (temperature < -15);
            case "si":
                return ((dirtyCounter > dirtyLimit) && temperature > -7) || (temperature < -15);
            case "ca":
                return ((dirtyCounter > dirtyLimit) && temperature > -7) || (temperature < -15);
            case "imperial":
                return ((dirtyCounter > dirtyLimit) && temperature > 19) || (temperature < 5);
            default:
                return ((dirtyCounter > dirtyLimit) && temperature > 266) || (temperature < 258);
        }
    }
}
