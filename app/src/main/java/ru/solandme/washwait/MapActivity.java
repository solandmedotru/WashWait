package ru.solandme.washwait;


import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import ru.solandme.washwait.adapters.MyPlacesRVAdapter;
import ru.solandme.washwait.map.POJO.PlacesResponse;
import ru.solandme.washwait.map.POJO.Result;
import ru.solandme.washwait.rest.PlacesApiHelper;

public class MapActivity extends FragmentActivity implements
        OnMapReadyCallback,
        MyPlacesRVAdapter.OnPlaceSelectedListener,
        ConnectionCallbacks,
        OnConnectionFailedListener,
        LocationListener {

    private static final String TAG = "MapActivity";
    private static final String TAG_ABOUT_PLACE = "AboutPlace";
    private static final int MA_PERMISSIONS_REQUEST_LOCATION = 99;
    private long UPDATE_INTERVAL = 10 * 1000;  /* 10 secs */
    private long FASTEST_INTERVAL = 2000; /* 2 sec */
    private GoogleMap map;
    private GoogleApiClient googleApiClient;

    LocationRequest mLocationRequest;
    Location mLastLocation;
    Marker mCurrLocationMarker;

    private LatLng currentLatLng;
    private String lang;

    MyPlacesRVAdapter adapter;
    RecyclerView carWashList;
    List<Result> results;

    PlacesApiHelper placesHelper;
    Bundle bundle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        bundle = getIntent().getExtras();
        currentLatLng = new LatLng(bundle.getFloat("lat"), bundle.getFloat("lon"));
        lang = bundle.getString("lang");

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkLocationPermission();
        }

        carWashList = (RecyclerView) findViewById(R.id.rwCarWashPlaces);
        carWashList.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    protected synchronized void buildGoogleApiClient() {
        googleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .build();
        googleApiClient.connect();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;

        //Initialize Google Play Services
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                buildGoogleApiClient();
                map.setMyLocationEnabled(true);
            }
        } else {
            buildGoogleApiClient();
            map.setMyLocationEnabled(true);
        }

        requestPlacesToCurrentLocation(currentLatLng);
    }


    private void moveCameraToLocation(LatLng currentLatLng) {
        map.moveCamera(CameraUpdateFactory.newLatLng(currentLatLng));
        map.animateCamera(CameraUpdateFactory.zoomTo(12));
    }

    private void requestPlacesToCurrentLocation(LatLng currentLatLng) {
        placesHelper = new PlacesApiHelper(this);
        placesHelper.requestPlaces("car_wash", currentLatLng, 10000, lang, placesResponseCallback);
    }

    private Callback<PlacesResponse> placesResponseCallback = new Callback<PlacesResponse>() {
        @Override
        public void onResponse(Call<PlacesResponse> call, Response<PlacesResponse> response) {

            results = response.body().getResults();

            for (Result result : results) {
                ru.solandme.washwait.map.POJO.Location location = result.getGeometry().getLocation();
                LatLng latLng = new LatLng(location.getLat(), location.getLng());
                map.addMarker(new MarkerOptions()
                        .position(latLng)
                        .snippet(result.getVicinity())
                        .title(result.getName()));
            }

            adapter = new MyPlacesRVAdapter(results, MapActivity.this);
            carWashList.setAdapter(adapter);
            moveCameraToLocation(currentLatLng);
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
    public void onLocationChanged(android.location.Location location) {
        mLastLocation = location;
        if (mCurrLocationMarker != null) {
            mCurrLocationMarker.remove();
        }

        //Place current location marker
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.title(getString(R.string.current_position));
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET));
        mCurrLocationMarker = map.addMarker(markerOptions);

        currentLatLng = latLng;
        requestPlacesToCurrentLocation(currentLatLng);
        //stop location updates
        if (map != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, this);
        }
    }

    public boolean checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MA_PERMISSIONS_REQUEST_LOCATION);
            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MA_PERMISSIONS_REQUEST_LOCATION);
            }
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MA_PERMISSIONS_REQUEST_LOCATION: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {
                        if (googleApiClient == null) {
                            buildGoogleApiClient();
                        }
                        map.setMyLocationEnabled(true);
                    }
                } else {
                    Toast.makeText(this, R.string.permission_danied, Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, mLocationRequest, this);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        if (i == CAUSE_SERVICE_DISCONNECTED) {
            Toast.makeText(this, "Disconnected. Please re-connect.", Toast.LENGTH_SHORT).show();
        } else if (i == CAUSE_NETWORK_LOST) {
            Toast.makeText(this, "Network lost. Please re-connect.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
    }

    @Override
    protected void onPause() {
        if (googleApiClient != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, this);
        }
        super.onPause();
    }

    @Override
    protected void onStop() {
        // only stop if it's connected, otherwise we crash
        if (googleApiClient != null) {
            googleApiClient.disconnect();
        }
        super.onStop();
    }
}
