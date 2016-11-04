package ru.solandme.washwait.rest;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
import ru.solandme.washwait.POJO.CurrentWeather;
import ru.solandme.washwait.POJO.WeatherFiveDays;
import ru.solandme.washwait.POJO.WeatherResponse;

public interface ApiInterface {
    @GET("forecast/daily")
    Call<WeatherResponse> getWeatherByCityName(@Query("q") String cityName,
                                               @Query("cnt") String cnt,
                                               @Query("units") String units,
                                               @Query("lang") String lang,
                                               @Query("appid") String appid);

    Call<WeatherResponse> getWeatherByCoordinats(@Query("lat") String lat,
                                                 @Query("lon") String lon,
                                                 @Query("cnt") String cnt,
                                                 @Query("units") String units,
                                                 @Query("lang") String lang,
                                                 @Query("appid") String appid);

    @GET("weather")
    Call<CurrentWeather> getCurrentWeatherByCityName(@Query("q") String cityName,
                                                     @Query("units") String units,
                                                     @Query("lang") String lang,
                                                     @Query("appid") String appid);

    Call<CurrentWeather> getCurrentWeatherByCoordinats(@Query("lat") String lat,
                                                       @Query("lon") String lon,
                                                       @Query("units") String units,
                                                       @Query("lang") String lang,
                                                       @Query("appid") String appid);

    @GET("forecast")
    Call<WeatherFiveDays> getWeatherFiveDaysByCityName(@Query("q") String cityName,
                                               @Query("units") String units,
                                               @Query("lang") String lang,
                                               @Query("appid") String appid);

    Call<WeatherResponse> getWeatherFiveDaysByCoordinats(@Query("lat") String lat,
                                                 @Query("lon") String lon,
                                                 @Query("units") String units,
                                                 @Query("lang") String lang,
                                                 @Query("appid") String appid);
}
