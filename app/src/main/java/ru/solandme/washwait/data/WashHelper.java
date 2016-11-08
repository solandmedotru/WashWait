package ru.solandme.washwait.data;

import android.util.Log;
import java.text.SimpleDateFormat;
import java.util.Locale;
import ru.solandme.washwait.POJO.WeatherFiveDays;

public class WashHelper {

    private static final String TAG = "ru.solandme.washwait";
    private WeatherFiveDays weatherFiveDays;
    private int FORECAST_DISTANCE;
    Forecast[] forecasts;
    int size;
    String dataToWashCar;

    public WashHelper(WeatherFiveDays weatherFiveDays, int FORECAST_DISTANCE) {
        this.weatherFiveDays = weatherFiveDays;
        this.FORECAST_DISTANCE = FORECAST_DISTANCE;

        if (null != weatherFiveDays) {
            size = weatherFiveDays.getList().size();
            forecasts = new Forecast[size];

            for (int i = 0; i < size; i++) {
                Forecast forecast = new Forecast();

                forecast.weatherIds = weatherFiveDays.getList().get(i).getWeather().get(0).getId();
                forecast.temperature = weatherFiveDays.getList().get(i).getMain().getTemp();
                forecasts[i] = forecast;
            }
        }
    }

    public int getWashDayNumber() {

        int washDayNumber = -1;
        int firstDirtyDay = -1;
        int clearDaysCounter = 0;
        int daysCounter = 0;
        Long dataWashCar = 0L;

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
                clearDaysCounter = 0;
            }
        }
        Log.e(TAG, "day: " + washDayNumber + " " + firstDirtyDay);
        dataToWashCar = new SimpleDateFormat("EEEE, dd", Locale.getDefault()).format(dataWashCar * 1000);

        return washDayNumber;
    }

    public String getDataToWashCar() {
        return dataToWashCar;
    }

    public Double getDirtyCounter() {
        Double rainCounter = 0.0, snowCounter = 0.0;
        if ((null != weatherFiveDays.getList().get(0).getRain())) {
            for (int i = 0; i < 8; i++) {
                rainCounter = rainCounter + weatherFiveDays.getList().get(i).getRain().get3h();
            }

        }
        if ((null != weatherFiveDays.getList().get(0).getSnow())) {
            for (int i = 0; i < 8; i++) {
                snowCounter = snowCounter + weatherFiveDays.getList().get(i).getSnow().get3h();
            }

        }
        Log.e(TAG, "dirtyCounter: " + (rainCounter + snowCounter));
        return rainCounter + snowCounter;
    }
}
