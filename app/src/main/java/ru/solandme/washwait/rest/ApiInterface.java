package ru.solandme.washwait.rest;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
import ru.solandme.washwait.POJO.WeatherFiveDays;
public interface ApiInterface {

    @GET("forecast")
    Call<WeatherFiveDays> getWeatherFiveDaysByCityName(@Query("q") String cityName,
                                                       @Query("units") String units,
                                                       @Query("lang") String lang,
                                                       @Query("appid") String appid);

    Call<WeatherFiveDays> getWeatherFiveDaysByCoordinats(@Query("lat") String lat,
                                                         @Query("lon") String lon,
                                                         @Query("units") String units,
                                                         @Query("lang") String lang,
                                                         @Query("appid") String appid);
}
