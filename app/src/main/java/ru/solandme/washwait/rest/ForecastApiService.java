package ru.solandme.washwait.rest;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
import ru.solandme.washwait.POJO.forecast.WeatherForecast;
import ru.solandme.washwait.POJO.weather.CurrWeather;

public interface ForecastApiService {

    @GET("forecast/daily")
    Call<WeatherForecast> getForecastByCoordinats(@Query("lat") String lat,
                                                  @Query("lon") String lon,
                                                  @Query("units") String units,
                                                  @Query("lang") String lang,
                                                  @Query("cnt") String cnt,
                                                  @Query("appid") String appId);

    @GET("weather")
    Call<CurrWeather> getCurrentWeatherByCoordinats(@Query("lat") String lat,
                                                    @Query("lon") String lon,
                                                    @Query("units") String units,
                                                    @Query("lang") String lang,
                                                    @Query("appid") String appId);
}
