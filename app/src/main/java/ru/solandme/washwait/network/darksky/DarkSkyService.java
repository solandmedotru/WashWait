package ru.solandme.washwait.network.darksky;


import java.util.Map;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.QueryMap;
import ru.solandme.washwait.network.darksky.model.DarkSkyForecast;

public interface DarkSkyService {

    @GET("{apiKey}/{lat},{lon}")
    Call<DarkSkyForecast> getForecastByCoordinats(@Path(value = "apiKey", encoded = true) String apiKey,
                                                  @Path(value = "lat", encoded = true) String lat,
                                                  @Path(value = "lon", encoded = true) String lon,
                                                  @QueryMap Map<String, String> queryParameter
    );
}
