package ru.solandme.washwait;

import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

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


public class MapActivity extends FragmentActivity implements OnMapReadyCallback{

    private static final String TAG = "MapActivity";
    private GoogleMap mMap;

    private double myLat;
    private double myLon;
    private String lang;


    MyPlacesRVAdapter adapter;
    RecyclerView carWashList;

    PlacesApiHelper mHelper;
    private LatLng mCurrentLatLng;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        carWashList = (RecyclerView) findViewById(R.id.rwCarWashPlaces);
        carWashList.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));


        mHelper = new PlacesApiHelper(this);

        Bundle bundle = getIntent().getExtras();
        myLat = bundle.getDouble("lat");
        myLon = bundle.getDouble("lon");
        lang = bundle.getString("lang");

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mCurrentLatLng = new LatLng(myLat, myLon);
        mHelper.requestPlaces("car_wash", mCurrentLatLng, 10000, lang, mResultCallback);

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
            adapter = new MyPlacesRVAdapter(results, mCurrentLatLng);
            carWashList.setAdapter(adapter);
            adapter.notifyDataSetChanged();
            for(Result result : results) {
                Location location = result.getGeometry().getLocation();
                LatLng latLng = new LatLng(location.getLat(), location.getLng());
                String name = result.getName();
                mMap.addMarker(new MarkerOptions().position(latLng).title(name));
            }
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(mCurrentLatLng, 12));
        }

        @Override
        public void onFailure(Call<PlacesResponse> call, Throwable t) {

        }
    };

}
