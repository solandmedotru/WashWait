package ru.solandme.washwait.rest;

import android.content.Context;

import com.google.android.gms.maps.model.LatLng;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import ru.solandme.washwait.R;
import ru.solandme.washwait.map.POJO.PlacesResponse;

public class PlacesApiHelper {
    private static final String TAG = PlacesApiHelper.class.getSimpleName();
    public static final String GOOGLEAPIS_BASE_URL = "https://maps.googleapis.com";
    private final PlacesApiHelper self = this;

    private Context mContext;

    public PlacesApiHelper(Context context) {
        mContext = context;
    }

    public void requestPlaces(String types, LatLng latLng, int radius, String lang, Callback<PlacesResponse> callback) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(GOOGLEAPIS_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        PlaceApiService service = retrofit.create(PlaceApiService.class);

        Call<PlacesResponse> call = service.requestPlaces(types,
                String.valueOf(latLng.latitude) + "," + String.valueOf(latLng.longitude),
                String.valueOf(radius),
                "false",
                lang,
                mContext.getString(R.string.google_maps_key));
        call.enqueue(callback);
    }
}