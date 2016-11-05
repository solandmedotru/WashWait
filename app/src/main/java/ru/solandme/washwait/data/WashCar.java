package ru.solandme.washwait.data;

import android.util.Log;

import ru.solandme.washwait.POJO.WeatherFiveDays;

public class WashCar {

    private static final String TAG = "ru.solandme.washwait";

    private int washDayNumber = -1;
    private int firstDirtyDay = -1;
    private int clearDaysCounter = 0;
    private int daysCounter = 0;

    public WashCar(WeatherFiveDays weatherFiveDays) {
        Forecast[] forecasts = new Forecast[40];

        for (int i = 0; i < 40; i++) {
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

        for (int i = 0; i < forecasts.length; i++) {
            if (i == 7 || i == 15 || i == 23 || i == 31 || i == 39) daysCounter++;
            if (!forecasts[i].isDirty()) {
                clearDaysCounter++;
                if (clearDaysCounter == 16) {
                    washDayNumber = i - clearDaysCounter + 1;
                }
            } else {
                if (firstDirtyDay == -1) {
                    firstDirtyDay = daysCounter -1;
                }
                clearDaysCounter = 0;
            }
        }
        Log.e(TAG, "day: " + washDayNumber + " " + firstDirtyDay);

    }

    public int getGoodDayForWashCar() {
        return washDayNumber;
    }

    public int getFirstDirtyDay(){
        return firstDirtyDay;
    }
}
