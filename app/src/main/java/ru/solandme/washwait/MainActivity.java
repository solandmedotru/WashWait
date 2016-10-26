package ru.solandme.washwait;

import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "ru.solandme.washwait";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new WeatherFragment())
                    .commit();
        } else {
            Fragment weatherFragment = getSupportFragmentManager().findFragmentById(R.id.container);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, weatherFragment)
                    .commit();
        }
    }
}
