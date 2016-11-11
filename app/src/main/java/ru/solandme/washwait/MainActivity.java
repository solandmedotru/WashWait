package ru.solandme.washwait;

import android.content.Intent;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;


public class MainActivity extends AppCompatActivity implements WeatherFragment.OnForecastSelectedListener{

    private static final String TAG_WEATHER_FRAG = "WeatherFragment";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        if (null == fragmentManager.findFragmentByTag(TAG_WEATHER_FRAG)) {
            fragmentTransaction
                    .add(R.id.container, new WeatherFragment(), TAG_WEATHER_FRAG)
                    .commit();
        } else {
            fragmentTransaction
                    .replace(R.id.container, fragmentManager.findFragmentByTag(TAG_WEATHER_FRAG))
                    .commit();
        }
    }


    @Override
    public void onForecastItemSelected(int position, double lat, double lon) {
        Intent intent = new Intent(this, MapActivity.class);
        intent.putExtra("lat", lat);
        intent.putExtra("lon", lon);
        startActivity(intent);
    }

}
