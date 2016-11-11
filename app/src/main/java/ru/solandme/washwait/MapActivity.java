package ru.solandme.washwait;

import android.support.v4.app.FragmentActivity;
import android.os.Bundle;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;

import noman.googleplaces.NRPlaces;
import noman.googleplaces.Place;
import noman.googleplaces.PlaceType;
import noman.googleplaces.PlacesException;
import noman.googleplaces.PlacesListener;


public class MapActivity extends FragmentActivity implements OnMapReadyCallback, PlacesListener {

    private GoogleMap mMap;
    private GoogleApiClient mGoogleApiClient;

    private double myLat;
    private double myLon;
    List<Place> places;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        Bundle bundle = getIntent().getExtras();
        myLat = bundle.getDouble("lat");
        myLon = bundle.getDouble("lon");


        mGoogleApiClient = new GoogleApiClient
                .Builder(this)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .build();


        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        new NRPlaces.Builder()
                .listener(this)
                .key("AIzaSyDVkzWmncsH-7tkaIYl0SlRMPmL0NAkjOc")
                .latlng(myLat, myLon)
                .radius(5000)
                .type(PlaceType.CAR_WASH)
                .build()
                .execute();

        // Add a marker in Sydney and move the camera
        LatLng myCoordinate = new LatLng(myLat, myLon);
        mMap.addMarker(new MarkerOptions().position(myCoordinate).title("I am Here").icon(BitmapDescriptorFactory
                .defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(myCoordinate));

    }

    @Override
    public void onPlacesFailure(PlacesException e) {

    }

    @Override
    public void onPlacesStart() {

    }

    @Override
    public void onPlacesSuccess(List<Place> places) {
        this.places = places;
    }

    @Override
    public void onPlacesFinished() {
        for (int i = 0; i < places.size(); i++) {
            double lat = places.get(i).getLatitude();
            double lon = places.get(i).getLongitude();
            LatLng myCoord = new LatLng(lat, lon);
            mMap.addMarker(new MarkerOptions().position(myCoord).title(places.get(i).getName()));
        }
    }
}
