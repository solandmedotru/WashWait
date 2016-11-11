package ru.solandme.washwait;

import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.places.GeoDataApi;
import com.google.android.gms.location.places.PlaceFilter;
import com.google.android.gms.location.places.PlaceLikelihood;
import com.google.android.gms.location.places.PlaceLikelihoodBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import ru.solandme.washwait.map.POJO.Location;
import ru.solandme.washwait.map.POJO.PlacesResponse;
import ru.solandme.washwait.map.POJO.Result;
import ru.solandme.washwait.rest.PlacesApiHelper;


public class MapActivity extends FragmentActivity implements OnMapReadyCallback{

    private static final String TAG = "MapActivity";
    private GoogleMap mMap;

    private double myLat;
    private double myLon;
    ArrayList<String> arrayList = new ArrayList<>();
    ArrayAdapter adapter;

    PlacesApiHelper mHelper;
    private LatLng mCurrentLatLng;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        ListView carWashList = (ListView) findViewById(R.id.car_wash_list);

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, arrayList);

        carWashList.setAdapter(adapter);

        mHelper = new PlacesApiHelper(this);

        Bundle bundle = getIntent().getExtras();
        myLat = bundle.getDouble("lat");
        myLon = bundle.getDouble("lon");

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mCurrentLatLng = new LatLng(myLat, myLon);
        mHelper.requestPlaces("car_wash", mCurrentLatLng, 10000, "ru", mResultCallback);

        mMap.clear();
        mMap.addMarker(new MarkerOptions()
                .position(mCurrentLatLng)
                .title("I am Here").icon(BitmapDescriptorFactory
                .defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
    }

    private Callback<PlacesResponse> mResultCallback = new Callback<PlacesResponse>() {
        @Override
        public void onResponse(Call<PlacesResponse> call, Response<PlacesResponse> response) {
            List<Result> results = response.body().getResults();
            for(Result result : results) {
                Location location = result.getGeometry().getLocation();
                LatLng latLng = new LatLng(location.getLat(), location.getLng());
                String name = result.getName();
                arrayList.add(name);
                adapter.notifyDataSetChanged();
                mMap.addMarker(new MarkerOptions().position(latLng).title(name));
            }
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(mCurrentLatLng, 12));
        }

        @Override
        public void onFailure(Call<PlacesResponse> call, Throwable t) {

        }
    };
}
