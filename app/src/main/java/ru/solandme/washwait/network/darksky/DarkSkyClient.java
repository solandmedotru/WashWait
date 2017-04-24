package ru.solandme.washwait.network.darksky;

import android.content.Context;
import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import ru.solandme.washwait.BuildConfig;
import ru.solandme.washwait.R;
import ru.solandme.washwait.model.washForecast.MyWeather;
import ru.solandme.washwait.model.washForecast.MyWeatherForecast;
import ru.solandme.washwait.network.ForecastApiHelper;
import ru.solandme.washwait.network.IWeatherClient;
import ru.solandme.washwait.network.darksky.model.Currently;
import ru.solandme.washwait.network.darksky.model.DarkSkyForecast;
import ru.solandme.washwait.network.darksky.model.Datum__;

public class DarkSkyClient implements IWeatherClient {
    private static final String TAG = DarkSkyClient.class.getSimpleName();
    private static final String BASE_URL = "https://api.darksky.net/forecast/";
    private static final String DARK_SKY_API_KEY = BuildConfig.DARK_SKY_API_KEY;
    private static final int MAX_PERIOD = 8;

    private static final String OPTIONS_LANGUAGE = "lang";
    private static final String OPTIONS_UNIT = "units";

    private final DarkSkyService apiService;
    private MyWeatherForecast myWeatherForecast;

    public DarkSkyClient(Context context) {
        apiService = ForecastApiHelper.requestForecast(context, BASE_URL).create(DarkSkyService.class);
    }


    @Override
    public MyWeatherForecast getWeatherForecast(float lat, float lon, String units, String lang) {
        myWeatherForecast = new MyWeatherForecast(MAX_PERIOD);
        myWeatherForecast.setUnits(units);
        return getWeather(lat, lon, units, lang);
    }

    private MyWeatherForecast getWeather(float lat, float lon, String units, String lang) {

        Call<DarkSkyForecast> weatherCall = apiService.getForecastByCoordinats(
                DARK_SKY_API_KEY,
                String.valueOf(lat),
                String.valueOf(lon),
                getQueryMapParameters(lang, units)
        );

        try {
            DarkSkyForecast darkSkyForecast = weatherCall.execute().body();

            myWeatherForecast.setLastUpdate(System.currentTimeMillis() / 1000);
            myWeatherForecast.setCityName(darkSkyForecast.getTimezone());
            myWeatherForecast.setCountry("");
            myWeatherForecast.setLatitude(darkSkyForecast.getLatitude());
            myWeatherForecast.setLongitude(darkSkyForecast.getLongitude());

            List<MyWeather> myWeatherList = new ArrayList<>();

            for (int i = 0; i < darkSkyForecast.getDaily().getData().size(); i++) {
                Datum__ item = darkSkyForecast.getDaily().getData().get(i);
                MyWeather weather = new MyWeather();
                weather.setTime(item.getTime());
                weather.setDescription(item.getSummary());
                weather.setTempMin((float) item.getTemperatureMin());
                weather.setTempMax((float) item.getTemperatureMax());
                weather.setPressure((float) item.getPressure());
                weather.setHumidity((float) item.getHumidity()*100);
                weather.setWindSpeed((float) item.getWindSpeed());
                weather.setWindDirection((float) item.getWindBearing());
                weather.setRain((float) item.getPrecipIntensityMax());
                weather.setSnow(0);
                weather.setDirtyCounter(getDirtyCounter((float) item.getPrecipIntensityMax(), 0));
                weather.setImageRes(getWeatherPicture(item.getIcon()));

                myWeatherList.add(weather);
            }


            MyWeather currentWeather = new MyWeather();
            Currently item = darkSkyForecast.getCurrently();
            currentWeather.setTime(item.getTime());
            currentWeather.setDescription(item.getSummary());
            currentWeather.setTempMin((float) item.getTemperature());
            currentWeather.setTempMax((float) item.getTemperature());
            currentWeather.setPressure((float) item.getPressure());
            currentWeather.setHumidity((float) item.getHumidity()*100);
            currentWeather.setWindSpeed((float) item.getWindSpeed());
            currentWeather.setWindDirection((float) item.getWindBearing());
            currentWeather.setRain((float)item.getPrecipIntensity());
            currentWeather.setSnow(0);
            currentWeather.setDirtyCounter(getDirtyCounter((float)item.getPrecipIntensity(), 0));
            currentWeather.setImageRes(getWeatherPicture(item.getIcon()));

            myWeatherForecast.setCurrentWeather(currentWeather);
            myWeatherForecast.setMyWeatherList(myWeatherList);
            myWeatherForecast.setForecastResultOK(true);
            myWeatherForecast.setCurrWeatherResultOK(true);
        } catch (IOException e) {
            e.printStackTrace();
            myWeatherForecast.setForecastResultOK(false);
            myWeatherForecast.setCurrWeatherResultOK(false);
        }
        return myWeatherForecast;
    }

    private float getDirtyCounter(float rainCounter, float snowCounter) {
        Log.e(TAG, "dirtyCounter: " + (rainCounter + (snowCounter)) * 4);
        return (rainCounter + (snowCounter * 2)) * 4;
    }

    private Map<String, String> getQueryMapParameters(String lang, String units) {
        Map<String, String> queryMap = new HashMap<>();
        if (lang != null) {
            queryMap.put(OPTIONS_LANGUAGE, lang);
        } else {
            queryMap.put(OPTIONS_LANGUAGE, "en");
        }
        if (units != null) {
            queryMap.put(OPTIONS_UNIT, units);
        } else {
            queryMap.put(OPTIONS_UNIT, "auto");
        }
        return queryMap;
    }

    private int getWeatherPicture(String icon) {

        switch (icon) {
            case "clear-day":
                return R.mipmap.clear_d;
            case "clear-night":
                return R.mipmap.clear_n;
            case "partly-cloudy-day":
                return R.mipmap.few_clouds_d;
            case "partly-cloudy-night":
                return R.mipmap.few_clouds_n;
            case "cloudy":
                return R.mipmap.scattered_clouds;
            case "rain":
                return R.mipmap.rain_d;
            case "snow":
                return R.mipmap.snow_d;
            case "fog":
                return R.mipmap.fog;
            default:
                return R.mipmap.few_clouds_d;
        }
    }
}
