package ru.solandme.washwait.rest;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
import ru.solandme.washwait.forecast.POJO.BigWeatherForecast;

public interface ForecastApiService {

    @GET("forecast/daily")
    Call<BigWeatherForecast> getForecastByCityName(@Query("q") String cityName,
                                                   @Query("units") String units,
                                                   @Query("lang") String lang,
                                                   @Query("cnt") String cnt,
                                                   @Query("appid") String appid);

    @GET("forecast/daily")
    Call<BigWeatherForecast> getForecastByCoordinats(@Query("lat") String lat,
                                                     @Query("lon") String lon,
                                                     @Query("units") String units,
                                                     @Query("lang") String lang,
                                                     @Query("cnt") String cnt,
                                                     @Query("appid") String appid);
}
