package ru.solandme.washwait;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import ru.solandme.washwait.map.POJO.Location;
import ru.solandme.washwait.map.POJO.PlacesResponse;
import ru.solandme.washwait.map.POJO.Result;
import ru.solandme.washwait.rest.PlacesApiHelper;

public class MapActivity extends FragmentActivity implements OnMapReadyCallback, MyPlacesRVAdapter.OnPlaceSelectedListener {

    private static final String TAG = "MapActivity";
    private static final String TAG_ABOUT_PLACE = "AboutPlace";
    private GoogleMap map;
    private GoogleApiClient googleApiClient;

    private float currentLat;
    private float currentLon;
    private LatLng currentLatLng;
    private String lang;

    MyPlacesRVAdapter adapter;
    RecyclerView carWashList;
    List<Result> results;

    PlacesApiHelper placesHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        googleApiClient = new GoogleApiClient
                .Builder(this)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .build();

        Bundle bundle = getIntent().getExtras();
        currentLat = bundle.getFloat("lat");
        currentLon = bundle.getFloat("lon");
        lang = bundle.getString("lang");

        carWashList = (RecyclerView) findViewById(R.id.rwCarWashPlaces);
        carWashList.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;

        currentLatLng = new LatLng(currentLat, currentLon);
        placesHelper = new PlacesApiHelper(this);
        placesHelper.requestPlaces("car_wash", currentLatLng, 10000, lang, placesResponseCallback);

        addCurrentLocationMarker();
    }

    private void addCurrentLocationMarker() {
        map.clear();
        map.addMarker(new MarkerOptions()
                .position(currentLatLng)
                .title(getString(R.string.i_am_here)).icon(BitmapDescriptorFactory
                        .defaultMarker(BitmapDescriptorFactory.HUE_VIOLET)));
    }

    private Callback<PlacesResponse> placesResponseCallback = new Callback<PlacesResponse>() {
        @Override
        public void onResponse(Call<PlacesResponse> call, Response<PlacesResponse> response) {

            results = response.body().getResults();

            for (Result result : results) {
                Location location = result.getGeometry().getLocation();
                LatLng latLng = new LatLng(location.getLat(), location.getLng());
                map.addMarker(new MarkerOptions()
                        .position(latLng)
                        .snippet(result.getVicinity())
                        .title(result.getName()));
            }

            map.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 12));

            adapter = new MyPlacesRVAdapter(results, currentLatLng, MapActivity.this);
            carWashList.setAdapter(adapter);
        }

        @Override
        public void onFailure(Call<PlacesResponse> call, Throwable t) {

        }
    };

    @Override
    public void onPlaceItemSelected(final int position, final Result result) {
        Places.GeoDataApi.getPlaceById(googleApiClient, result.getPlaceId())
                .setResultCallback(new ResultCallback<PlaceBuffer>() {
                    @Override
                    public void onResult(PlaceBuffer places) {
                        if (places.getStatus().isSuccess() && places.getCount() > 0) {
                            final Place myPlace = places.get(0);
                            Log.i(TAG, "Place found: " + myPlace.getName());

                            Bundle args = new Bundle();
                            args.putString("name", myPlace.getName().toString());
                            args.putString("phone", myPlace.getPhoneNumber().toString());
                            args.putString("placeId", myPlace.getId());
                            args.putString("address", myPlace.getAddress().toString());
                            args.putFloat("rating", myPlace.getRating());

                            if (myPlace.getWebsiteUri() != null)
                                args.putString("webUrl", myPlace.getWebsiteUri().toString());


                            if (result.getPhotos().size() > 0) {
                                args.putString("photoRef", result.getPhotos().get(0).getPhotoReference());
                            }
                            if (result.getOpeningHours() != null) {
                                String open = "";
                                for (int i = 0; i < result.getOpeningHours().getWeekdayText().size(); i++) {
                                    open = open + "\n" + result.getOpeningHours().getWeekdayText().get(i).toString();
                                    args.putString("openHours", open);

                                }
                            }

                            AboutPlace aboutPlaceDialog = new AboutPlace();
                            aboutPlaceDialog.setArguments(args);
                            aboutPlaceDialog.show(getSupportFragmentManager(), TAG_ABOUT_PLACE);
                        } else {
                            Log.e(TAG, "Place not found");
                        }
                        places.release();
                    }
                });

    }

    @Override
    protected void onStart() {
        super.onStart();
        googleApiClient.connect();
    }

    @Override
    protected void onStop() {
        googleApiClient.disconnect();
        super.onStop();
    }
}
