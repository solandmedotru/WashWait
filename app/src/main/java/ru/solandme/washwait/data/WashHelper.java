package ru.solandme.washwait.data;

import android.util.Log;

import ru.solandme.washwait.POJO.BigWeatherForecast;

public class WashHelper {

    private static final String TAG = "ru.solandme.washwait";
    private BigWeatherForecast weather;
    private int FORECAST_DISTANCE;
    private Forecast[] forecasts;
    private int size;
    private long dataToWashCar = 0L;

    public WashHelper(BigWeatherForecast weather, int FORECAST_DISTANCE) {
        this.weather = weather;
        this.FORECAST_DISTANCE = FORECAST_DISTANCE;

        if (null != weather) {
            size = weather.getList().size();
            forecasts = new Forecast[size];

            for (int i = 0; i < size; i++) {
                Forecast forecast = new Forecast();

                forecast.weatherIds = weather.getList().get(i).getWeather().get(0).getId();
                forecast.temperature = weather.getList().get(i).getTemp().getDay();
                forecasts[i] = forecast;
            }
        }
    }

    public int getWashDayNumber() {

        int washDayNumber = -1;
        int firstDirtyDay = -1;
        int clearDaysCounter = 0;
        int daysCounter = 0;

        for (int i = 0; i < size; i++) {
            daysCounter++;
            if (!forecasts[i].isDirty()) {
                clearDaysCounter++;
                if (clearDaysCounter == FORECAST_DISTANCE) {
                    if (washDayNumber == -1) {
                        washDayNumber = daysCounter - i;
                        dataToWashCar = weather.getList().get(i).getDt();
                    }
                }
            } else {
                clearDaysCounter = 0;
            }
        }
        Log.e(TAG, "day: " + washDayNumber + " " + firstDirtyDay);

        return washDayNumber;
    }

    public Long getDataToWashCar() {
        return dataToWashCar;
    }

    public Double getDirtyCounter() {
        Double rainCounter = 0.0, snowCounter = 0.0;
            rainCounter = rainCounter + weather.getList().get(0).getRain();
            snowCounter = snowCounter + weather.getList().get(0).getSnow();

        Log.e(TAG, "dirtyCounter: " + (rainCounter + snowCounter));
        return rainCounter + snowCounter;
    }
}
