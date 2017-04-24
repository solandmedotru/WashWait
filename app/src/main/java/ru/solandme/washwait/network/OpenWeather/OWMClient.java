package ru.solandme.washwait.network.OpenWeather;

import android.content.Context;
import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import ru.solandme.washwait.BuildConfig;
import ru.solandme.washwait.R;
import ru.solandme.washwait.model.washForecast.MyWeather;
import ru.solandme.washwait.model.washForecast.MyWeatherForecast;
import ru.solandme.washwait.network.ForecastApiHelper;
import ru.solandme.washwait.network.IWeatherClient;
import ru.solandme.washwait.network.OpenWeather.model.forecast.OpenWeatherForecast;
import ru.solandme.washwait.network.OpenWeather.model.weather.OpenWeatherCurrent;

public class OWMClient implements IWeatherClient {
    private static final String TAG = OWMClient.class.getSimpleName();
    private static final String CNT = "16";
    private static final String OPEN_WEATHER_MAP_API_KEY = BuildConfig.OPEN_WEATHER_MAP_API_KEY;
    private static final String BASE_URL = "http://api.openweathermap.org/data/2.5/";

    private MyWeatherForecast myWeatherForecast;
    private final OWMService apiService;

    public OWMClient(Context context) {
        myWeatherForecast = new MyWeatherForecast();
        apiService = ForecastApiHelper.requestForecast(context, BASE_URL).create(OWMService.class);
    }

    public MyWeatherForecast getWeatherForecast(float lat, float lon, String units, String lang) {
        getWeather(lat, lon, units, lang);
        getCurrentWeather(lat, lon, units, lang);
        return myWeatherForecast;
    }

    private void getCurrentWeather(float lat, float lon, String units, String lang) {
        Call<OpenWeatherCurrent> currWeatherCall = apiService.getCurrentWeatherByCoordinats(
                String.valueOf(lat),
                String.valueOf(lon),
                units,
                lang,
                OPEN_WEATHER_MAP_API_KEY);

        try {
            OpenWeatherCurrent item = currWeatherCall.execute().body();
            MyWeather currentWeather = new MyWeather();
            currentWeather.setTime(item.getDt());
            currentWeather.setDescription(item.getWeather().get(0).getDescription());
            currentWeather.setTempMin((float) item.getMain().getTempMin());
            currentWeather.setTempMax((float) item.getMain().getTempMax());
            currentWeather.setPressure((float) item.getMain().getPressure());
            currentWeather.setHumidity((float) item.getMain().getHumidity());
            currentWeather.setWindSpeed((float) item.getWind().getSpeed());
            currentWeather.setWindDirection((float) item.getWind().getDeg());
            float rain = item.getRain() != null ? (float) item.getRain().get3h() : 0;
            float snow = item.getSnow() != null ? (float) item.getSnow().get3h() : 0;
            currentWeather.setRain(rain);
            currentWeather.setSnow(snow);
            currentWeather.setDirtyCounter(getDirtyCounter(rain, snow));
            currentWeather.setImageRes(getWeatherPicture(item.getWeather().get(0).getIcon()));

            myWeatherForecast.setCurrentWeather(currentWeather);
            myWeatherForecast.setCurrWeatherResultOK(true);
        } catch (IOException e) {
            e.printStackTrace();
            myWeatherForecast.setCurrWeatherResultOK(false);
        }
    }

    private void getWeather(float lat, float lon, String units, String lang) {
        Call<OpenWeatherForecast> weatherCall = apiService.getForecastByCoordinats(
                String.valueOf(lat),
                String.valueOf(lon),
                units,
                lang,
                CNT,
                OPEN_WEATHER_MAP_API_KEY);

        try {
            OpenWeatherForecast weatherForecast = weatherCall.execute().body();

            myWeatherForecast.setLastUpdate(System.currentTimeMillis() / 1000);
            myWeatherForecast.setCityName(weatherForecast.getCity().getName());
            myWeatherForecast.setCountry(weatherForecast.getCity().getCountry());
            myWeatherForecast.setLatitude(weatherForecast.getCity().getCoord().getLat());
            myWeatherForecast.setLongitude(weatherForecast.getCity().getCoord().getLon());

            List<MyWeather> myWeatherList = new ArrayList<>();

            for (int i = 0; i < weatherForecast.getList().size(); i++) {
                ru.solandme.washwait.network.OpenWeather.model.forecast.List item = weatherForecast.getList().get(i);
                MyWeather weather = new MyWeather();
                weather.setId(item.getWeather().get(0).getId());
                weather.setTime(item.getDt());
                weather.setDescription(item.getWeather().get(0).getDescription());
                weather.setTempMin((float) item.getTemp().getMin());
                weather.setTempMax((float) item.getTemp().getMax());
                weather.setPressure((float) item.getPressure());
                weather.setHumidity((float) item.getHumidity());
                weather.setWindSpeed((float) item.getSpeed());
                weather.setWindDirection((float) item.getDeg());
                weather.setRain((float) item.getRain());
                weather.setSnow((float) item.getSnow());
                weather.setDirtyCounter(getDirtyCounter((float) item.getRain(), (float) item.getSnow()));
                weather.setImageRes(getWeatherPicture(item.getWeather().get(0).getIcon()));

                myWeatherList.add(weather);
            }

            myWeatherForecast.setMyWeatherList(myWeatherList);
            myWeatherForecast.setForecastResultOK(true);
        } catch (IOException e) {
            e.printStackTrace();
            myWeatherForecast.setForecastResultOK(false);
        }
    }

    private float getDirtyCounter(float rainCounter, float snowCounter) {
        Log.e(TAG, "dirtyCounter: " + (rainCounter + (snowCounter)) * 4);
        return (rainCounter + (snowCounter * 2)) * 4;
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
