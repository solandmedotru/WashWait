package ru.solandme.washwait.data;

import android.content.Context;
import android.util.Log;

import ru.solandme.washwait.POJO.WeatherFiveDays;
import ru.solandme.washwait.R;

public class WashCar {

    private static final String TAG = "ru.solandme.washwait";

    private int washDayNumber = -1;
    private int firstDirtyDay = -1;
    private int clearDaysCounter = 0;
    private int daysCounter = 0;

    public WashCar(WeatherFiveDays weatherFiveDays, int FORECAST_DISTANCE) {

        if (null != weatherFiveDays) {
            int size = weatherFiveDays.getList().size();
            Forecast[] forecasts = new Forecast[size];

            for (int i = 0; i < size; i++) {
                Forecast forecast = new Forecast();

                forecast.weatherIds = weatherFiveDays.getList().get(i).getWeather().get(0).getId();
                forecast.temperature = weatherFiveDays.getList().get(i).getMain().getTemp();


                Log.e(TAG,
                        weatherFiveDays.getList().get(i).getWeather().get(0).getMain()
                                + weatherFiveDays.getList().get(i).getWeather().get(0).getId());

                if ((null != weatherFiveDays.getList().get(i).getRain())) {
                    forecast.rainCounter = forecast.rainCounter + weatherFiveDays.getList().get(i).getRain().get3h();
                    Log.e(TAG, "rain: " + String.valueOf(forecast.rainCounter));
                }
                if ((null != weatherFiveDays.getList().get(i).getSnow())) {
                    forecast.snowCounter = forecast.snowCounter + weatherFiveDays.getList().get(i).getSnow().get3h();
                    Log.e(TAG, "snow: " + String.valueOf(forecast.snowCounter) + " " + forecast.temperature);
                }
                forecasts[i] = forecast;
            }

            for (int i = 0; i < size; i++) {
                if (i == 0 || i == 7 || i == 15 || i == 23 || i == 31 || i == 39) daysCounter++;
                if (!forecasts[i].isDirty()) {
                    clearDaysCounter++;
                    if (clearDaysCounter == FORECAST_DISTANCE) {
                        if(washDayNumber == -1) {
                            washDayNumber = daysCounter - 1;
                        }
                    }
                } else {
                    if (firstDirtyDay == -1) {
                        firstDirtyDay = daysCounter - 1;
                    }
                    clearDaysCounter = 0;
                }
            }
            Log.e(TAG, "day: " + washDayNumber + " " + firstDirtyDay);
        }
    }

    public String getForecastText(Context context) {
        String pluralsDay;
        if(firstDirtyDay == 0){
            pluralsDay = context.getResources().getString(R.string.zeroDay);
        } else {
            pluralsDay = context.getResources().getQuantityString(R.plurals.daysCounter, firstDirtyDay, firstDirtyDay);
        }

        if(washDayNumber != 0){
            return context.getResources().getString(R.string.not_wash)
                    + ", так как "
                    + pluralsDay
                    + " будет грязно!\n"
                    + "Ближайший день для мойки, "
                    + context.getResources().getQuantityString(R.plurals.daysCounter, washDayNumber, washDayNumber);
        } else return context.getResources().getString(R.string.can_wash);
    }
}
