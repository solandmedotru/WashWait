package ru.solandme.washwait.rest;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
import ru.solandme.washwait.map.POJO.PlacesResponse;

public interface PlaceApiService {
    @GET("/maps/api/place/nearbysearch/json")
    Call<PlacesResponse> requestPlaces(@Query("types") String types,
                                       @Query("location") String location,
                                       @Query("rankby") String rankby,
                                       @Query("sensor") String sensor,
                                       @Query("language") String language,
                                       @Query("key") String key);

    @GET("/maps/api/place/nearbysearch/json")
    Call<PlacesResponse> requestNextPlaces(@Query("pagetoken") String pagetoken);
}
