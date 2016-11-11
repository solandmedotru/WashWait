package ru.solandme.washwait.data;

import android.util.Log;

import java.util.ArrayList;

import ru.solandme.washwait.POJO.BigWeatherForecast;
import ru.solandme.washwait.R;

public class WashHelper {

    private static final String TAG = "ru.solandme.washwait";
    private BigWeatherForecast weather;
    private int FORECAST_DISTANCE;
    private ArrayList<Forecast> forecasts;
    private int size;
    private long dataToWashCar = 0L;


    public void generateForecast(BigWeatherForecast weather, ArrayList<Forecast> forecasts, int FORECAST_DISTANCE) {
        this.forecasts = forecasts;
        this.weather = weather;
        this.FORECAST_DISTANCE = FORECAST_DISTANCE;

        if (null != weather) {
            forecasts.clear();
            size = weather.getList().size();

            for (int i = 0; i < size; i++) {
                Forecast forecast = new Forecast();

                forecast.setWeatherId(weather.getList().get(i).getWeather().get(0).getId());
                forecast.setTemperature(weather.getList().get(i).getTemp().getDay());
                forecast.setDate(weather.getList().get(i).getDt() * 1000);
                forecast.setImageRes(getWeatherPicture(weather.getList().get(i).getWeather().get(0).getIcon()));
                forecast.setCityName(weather.getCity().getName());
                forecast.setCountry(weather.getCity().getCountry());
                forecast.setDescription(weather.getList().get(i).getWeather().get(0).getDescription());
                forecast.setLat(weather.getCity().getCoord().getLat());
                forecast.setLon(weather.getCity().getCoord().getLon());
                forecasts.add(forecast);
            }
        }
    }

    public void setForecasts(ArrayList<Forecast> forecasts) {
        this.forecasts = forecasts;
    }

    public int getWashDayNumber() {

        int washDayNumber = -1;
        int firstDirtyDay = -1;
        int clearDaysCounter = 0;
        int daysCounter = 0;

        for (int i = 0; i < size; i++) {
            daysCounter++;
            if (!forecasts.get(i).isDirty()) {
                clearDaysCounter++;
                if (clearDaysCounter == FORECAST_DISTANCE) {
                    if (washDayNumber == -1) {
                        washDayNumber = daysCounter - clearDaysCounter;
                        dataToWashCar = forecasts.get(washDayNumber).getDate();
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

        Log.e(TAG, "dirtyCounter: " + (rainCounter + (snowCounter)) * 4);
        return (rainCounter + (snowCounter)) * 4;
    }

    public ArrayList<Forecast> getForecasts() {
        return forecasts;
    }

    private int getWeatherPicture(String icon) {

        switch (icon) {
            case "01d":
                return R.mipmap.clear_d;
            case "01n":
                return R.mipmap.clear_n;
            case "02d":
                return R.mipmap.few_clouds_d;
            case "02n":
                return R.mipmap.few_clouds_n;
            case "03d":
                return R.mipmap.scattered_clouds;
            case "03n":
                return R.mipmap.scattered_clouds;
            case "04d":
                return R.mipmap.broken_clouds;
            case "04n":
                return R.mipmap.broken_clouds;
            case "09d":
                return R.mipmap.shower_rain_d;
            case "09n":
                return R.mipmap.shower_rain_n;
            case "10d":
                return R.mipmap.rain_d;
            case "10n":
                return R.mipmap.rain_n;
            case "11d":
                return R.mipmap.thunder_d;
            case "11n":
                return R.mipmap.thunder_n;
            case "13d":
                return R.mipmap.snow_d;
            case "13n":
                return R.mipmap.snow_n;
            case "50d":
                return R.mipmap.fog;
            case "50n":
                return R.mipmap.fog;
            default:
                return R.mipmap.few_clouds_d;
        }
    }
}
