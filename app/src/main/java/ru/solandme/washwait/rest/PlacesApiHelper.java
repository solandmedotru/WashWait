package ru.solandme.washwait.rest;

import android.content.Context;

import com.google.android.gms.maps.model.LatLng;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import ru.solandme.washwait.R;
import ru.solandme.washwait.map.POJO.PlacesResponse;
import ru.solandme.washwait.places.POJO.PlaceInfo;

public class PlacesApiHelper {
    private static final String TAG = PlacesApiHelper.class.getSimpleName();
    public static final String GOOGLEAPIS_BASE_URL = "https://maps.googleapis.com";
    private final PlacesApiHelper self = this;

    private Context mContext;
    private static Retrofit retrofit = null;

    public PlacesApiHelper(Context context) {
        mContext = context;
    }

    public static Retrofit getInstance() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(GOOGLEAPIS_BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();;
        }
        return retrofit;
    }

    public void requestPlaces(String types, LatLng latLng, String lang, Callback<PlacesResponse> callback) {

        PlaceApiService service = getInstance().create(PlaceApiService.class);

        Call<PlacesResponse> call = service.requestPlaces(
                types,
                String.valueOf(latLng.latitude) + "," + String.valueOf(latLng.longitude),
                "distance",
                "false",
                lang,
                mContext.getString(R.string.google_maps_key));
        call.enqueue(callback);
    }

    public void requestNextPlaces(String pagetoken, Callback<PlacesResponse> callback) {

        PlaceApiService service = getInstance().create(PlaceApiService.class);

        Call<PlacesResponse> call = service.requestNextPlaces(pagetoken,
                mContext.getString(R.string.google_maps_key));
        call.enqueue(callback);
    }

    public void requestPlaceInfo(String placeid, String lang, Callback<PlaceInfo> callback) {

        PlaceApiService service = getInstance().create(PlaceApiService.class);

        Call<PlaceInfo> call = service.requestPlaceInfo(placeid, lang,
                mContext.getString(R.string.google_maps_key));
        call.enqueue(callback);
    }
}