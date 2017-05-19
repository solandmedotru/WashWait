package ru.solandme.washwait.network.map;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
import ru.solandme.washwait.network.map.model.map.PlacesResponse;
import ru.solandme.washwait.network.map.model.places.PlaceInfo;

interface PlaceApiService {
    @GET("/maps/api/place/nearbysearch/json")
    Call<PlacesResponse> requestPlaces(@Query("types") String types,
                                       @Query("location") String location,
                                       @Query("rankby") String rankBy,
                                       @Query("sensor") String sensor,
                                       @Query("language") String language,
                                       @Query("key") String key);

//    @GET("/maps/api/place/nearbysearch/json")
//    Call<PlacesResponse> requestNextPlaces(@Query("pagetoken") String pageToken,
//                                           @Query("key") String key);


    @GET("/maps/api/place/details/json")
    Call<PlaceInfo> requestPlaceInfo(@Query("placeid") String placeId,
                                  @Query("language") String language,
                                  @Query("key") String key);
}
