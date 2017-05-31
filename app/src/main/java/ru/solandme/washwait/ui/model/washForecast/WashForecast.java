package ru.solandme.washwait.ui.model.washForecast;

import android.content.Context;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Locale;

import ru.solandme.washwait.R;

public class WashForecast {
    private static final String TAG = WashForecast.class.getSimpleName();
    private String text;
    private int washDayNumber;
    private Context context;

    public WashForecast(Context context, MyWeatherForecast myWeatherForecast, String forecastDistance, float precipitationLimit) {
        this.context = context;
        this.washDayNumber = getWashDayByDistance(forecastDistance, precipitationLimit, myWeatherForecast);
        this.text = getTextForWashForecast(washDayNumber, getWashData(washDayNumber, myWeatherForecast));
//        this.units = SharedPrefsUtils.getStringPreference(context, context.getString(R.string.pref_units_key), Constants.DEFAULT_UNITS);
    }

    public int getWashDayNumber() {
        return washDayNumber;
    }

    public String getText() {
        return text;
    }

    private String getTextForWashForecast(int washDayNumber, double dataToWash) {
        String dateToWashFormat;
        if (dataToWash != 0) {
            dateToWashFormat = new SimpleDateFormat("dd MMMM, EE", Locale.getDefault()).format(dataToWash * 1000);
        } else {
            washDayNumber = 15;
            dateToWashFormat = "N/D";
        }

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

    private double getWashData(int washDayNumber, MyWeatherForecast myWeatherForecast) {

        if (myWeatherForecast != null) {
            if (washDayNumber > myWeatherForecast.getMyWeatherList().size())
                return myWeatherForecast.getMyWeatherList().get(myWeatherForecast.getMaxPeriod() - 1).getTime();
            return myWeatherForecast.getMyWeatherList().get(washDayNumber).getTime();
        } else return 0;

    }

    private int getWashDayByDistance(String forecastDistance, float precipitationLimit, MyWeatherForecast myWeatherForecast) {

        int washDayNumber = myWeatherForecast.getMaxPeriod() - 1;
        int firstDirtyDay = -1;
        int clearDaysCounter = 0;
        int daysCounter = 0;

        for (int i = 0; i < myWeatherForecast.getMyWeatherList().size(); i++) {
            double maxTemp = myWeatherForecast.getMyWeatherList().get(i).getTempMax();
            float precipitation = myWeatherForecast.getMyWeatherList().get(i).getPrecipitation();

            daysCounter++;
            if (!isBadConditions(maxTemp, precipitation, precipitationLimit, myWeatherForecast.getUnits())) {
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

    private boolean isBadConditions(double temperature, float precipitation, float precipitationLimit, String units) {
        switch (units) {
            case "metric":
            case "si":
            case "ca":
                return ((precipitation > precipitationLimit) && temperature > -7) || (temperature < -15);
            case "imperial":
            case "us":
                return ((precipitation > precipitationLimit) && temperature > 19) || (temperature < 5);
            default:
                return ((precipitation > precipitationLimit) && temperature > 266) || (temperature < 258);
        }
    }
}
