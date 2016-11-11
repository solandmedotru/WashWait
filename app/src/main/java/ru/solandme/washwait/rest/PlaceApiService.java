package ru.solandme.washwait.rest;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
import ru.solandme.washwait.map.POJO.PlacesResponse;

/**
 * URL Sample:
 * https://maps.googleapis.com/maps/api/place/search/json
 * ?types=cafe
 * &location=37.787930,-122.4074990
 * &radius=5000
 * &sensor=false
 * &key=YOUR_API_KEY
 */
public interface PlaceApiService {
    @GET("/maps/api/place/search/json")
    Call<PlacesResponse> requestPlaces(@Query("types") String types,
                                       @Query("location") String location,
                                       @Query("radius") String radius,
                                       @Query("sensor") String sensor,
                                       @Query("language") String language,
                                       @Query("key") String key);
}
