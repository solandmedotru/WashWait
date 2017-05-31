package ru.solandme.washwait.mvp.main.data.network;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
import ru.solandme.washwait.mvp.main.data.network.forecast.OpenWeatherForecast;
import ru.solandme.washwait.mvp.main.data.network.weather.OpenWeatherCurrent;


public interface OWMService {

    @GET("forecast/daily")
    Call<OpenWeatherForecast> getForecastByCoordinats(@Query("lat") String lat,
                                                      @Query("lon") String lon,
                                                      @Query("units") String units,
                                                      @Query("lang") String lang,
                                                      @Query("cnt") String cnt,
                                                      @Query("appid") String appId);

    @GET("weather")
    Call<OpenWeatherCurrent> getCurrentWeatherByCoordinats(@Query("lat") String lat,
                                                           @Query("lon") String lon,
                                                           @Query("units") String units,
                                                           @Query("lang") String lang,
                                                           @Query("appid") String appId);
}
