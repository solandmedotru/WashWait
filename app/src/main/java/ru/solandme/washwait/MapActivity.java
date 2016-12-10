package ru.solandme.washwait;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
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
import ru.solandme.washwait.places.POJO.Photo;
import ru.solandme.washwait.places.POJO.PlaceInfo;
import ru.solandme.washwait.rest.PlacesApiHelper;
import ru.solandme.washwait.utils.Utils;

public class MapActivity extends AppCompatActivity implements
        OnMapReadyCallback,
        MyPlacesRVAdapter.OnPlaceSelectedListener,
        ConnectionCallbacks,
        OnConnectionFailedListener,
        LocationListener {

    private static final String TAG = "MapActivity";
    private static final int MA_PERMISSIONS_REQUEST_LOCATION = 99;
    private static final long UPDATE_INTERVAL = 10 * 1000;  /* 10 secs */
    private static final long FASTEST_INTERVAL = 2000; /* 2 sec */
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
        Utils.onActivityCreateSetTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        bundle = getIntent().getExtras();
        currentLatLng = new LatLng(bundle.getFloat("lat"), bundle.getFloat("lon"));
        Log.e(TAG, "onCreate: " + currentLatLng.toString() );
        lang = bundle.getString("lang");

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkLocationPermission();
        }

        carWashList = (RecyclerView) findViewById(R.id.rwCarWashPlaces);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        carWashList.setLayoutManager(linearLayoutManager);
        DividerItemDecoration dividerItemDecoration =
                new DividerItemDecoration(carWashList.getContext(), linearLayoutManager.getOrientation());
        carWashList.addItemDecoration(dividerItemDecoration);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    protected synchronized void buildGoogleApiClient() {
        googleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
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

        placesHelper = new PlacesApiHelper(this);
        requestPlacesNearCurrentLocation();
    }


    private void moveCameraToLocation(LatLng currentLatLng) {
        map.moveCamera(CameraUpdateFactory.newLatLng(currentLatLng));
        map.animateCamera(CameraUpdateFactory.zoomTo(13));
    }

    private void requestPlacesNearCurrentLocation() {
        placesHelper.requestPlaces("car_wash", currentLatLng, lang, new Callback<PlacesResponse>() {
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
                Log.e(TAG, "onResponse: " + currentLatLng.toString() );
            }

            @Override
            public void onFailure(Call<PlacesResponse> call, Throwable t) {

            }
        });
    }


    @Override
    public void onPlaceItemSelected(final int position, final Result result) {

        placesHelper.requestPlaceInfo(result.getPlaceId(), lang, new Callback<PlaceInfo>() {
            @Override
            public void onResponse(Call<PlaceInfo> call, Response<PlaceInfo> response) {

                ru.solandme.washwait.places.POJO.Result result = response.body().getResult();

                Log.i(TAG, "Place found: " + result.getName());

                Intent intent = new Intent(MapActivity.this, AboutPlace.class);
                intent.putExtra(AboutPlace.PLACE_NAME_KEY, result.getName());
                intent.putExtra(AboutPlace.PHONE_KEY, result.getInternationalPhoneNumber());
                intent.putExtra(AboutPlace.ADDRESS_KEY, result.getVicinity());
                intent.putExtra(AboutPlace.RATING_KEY, ((float) result.getRating()));
                intent.putExtra(AboutPlace.LAT_KEY, (result.getGeometry().getLocation().getLat()));
                intent.putExtra(AboutPlace.LON_KEY, (result.getGeometry().getLocation().getLng()));

                if (result.getWebsite() != null)
                    intent.putExtra(AboutPlace.WEB_URL_KEY, result.getWebsite());


                if (result.getPhotos() != null) {
                    String photoAttributions = "";
                    for (String attr : result.getPhotos().get(0).getHtmlAttributions()) {
                        photoAttributions = photoAttributions + attr + "\n";
                    }
                    intent.putExtra(AboutPlace.PHOTO_ATTRIBUTES_KEY, photoAttributions);
                    intent.putExtra(AboutPlace.PHOTO_REF_KEY, result.getPhotos().get(0).getPhotoReference());
                }

                if (result.getOpeningHours() != null) {
                    String open = "";
                    for (int i = 0; i < result.getOpeningHours().getWeekdayText().size(); i++) {
                        open = open + "\n" + result.getOpeningHours().getWeekdayText().get(i);
                        intent.putExtra(AboutPlace.OPEN_HOURS_KEY, open);
                    }
                }
                startActivity(intent);
            }

            @Override
            public void onFailure(Call<PlaceInfo> call, Throwable t) {
                Log.e(TAG, "Place not found");
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
        Log.e(TAG, "onLocationChanged: " + currentLatLng.toString() );
        requestPlacesNearCurrentLocation();
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
    protected void onDestroy() {
        // only stop if it's connected, otherwise we crash
        if (googleApiClient != null) {
            googleApiClient.disconnect();
        }
        super.onDestroy();
    }
}
