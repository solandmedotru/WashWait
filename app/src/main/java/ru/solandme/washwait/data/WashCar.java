package ru.solandme.washwait.data;

import android.content.Context;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Locale;

import ru.solandme.washwait.POJO.WeatherFiveDays;
import ru.solandme.washwait.R;

public class WashCar {

    private static final String TAG = "ru.solandme.washwait";


    public static String getForecastText(Context context, WeatherFiveDays weatherFiveDays, int FORECAST_DISTANCE) {
        int washDayNumber = -1;
        int firstDirtyDay = -1;
        int clearDaysCounter = 0;
        int daysCounter = 0;
        Long dataWashCar = 0L;
        Double dirtyCounter = 0.0;

        if (null != weatherFiveDays) {
            int size = weatherFiveDays.getList().size();
            Forecast[] forecasts = new Forecast[size];

            for (int i = 0; i < size; i++) {
                Forecast forecast = new Forecast();

                forecast.weatherIds = weatherFiveDays.getList().get(i).getWeather().get(0).getId();
                forecast.temperature = weatherFiveDays.getList().get(i).getMain().getTemp();


                Log.e(TAG, weatherFiveDays.getList().get(i).getWeather().get(0).getMain()
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
                if (i == 7 || i == 15 || i == 23 || i == 31 || i == 39) daysCounter++;
                if (!forecasts[i].isDirty()) {
                    clearDaysCounter++;
                    if (clearDaysCounter == FORECAST_DISTANCE) {
                        if (washDayNumber == -1) {
                            washDayNumber = daysCounter - 1;
                            dataWashCar = weatherFiveDays.getList().get(i).getDt();
                        }
                    }
                } else {
                    if(i < FORECAST_DISTANCE) dirtyCounter = dirtyCounter + forecasts[i].rainCounter + forecasts[i].snowCounter;
                    clearDaysCounter = 0;
                }
            }
            Log.e(TAG, "day: " + washDayNumber + " " + firstDirtyDay + " " + dirtyCounter);
        }

        String dataToWashCar = new SimpleDateFormat("EEEE, dd", Locale.getDefault()).format(dataWashCar * 1000);
        switch (washDayNumber) {
            case 0:
                return context.getResources().getString(R.string.can_wash);
            case 1:
                return context.getResources().getString(R.string.wash, dataToWashCar);
            case 2:
                return context.getResources().getString(R.string.wash, dataToWashCar);
            case 3:
                return context.getResources().getString(R.string.wash, dataToWashCar);
            case 4:
                return context.getResources().getString(R.string.wash, dataToWashCar);
            default:
                return context.getResources().getString(R.string.not_wash);
        }

    }
}
