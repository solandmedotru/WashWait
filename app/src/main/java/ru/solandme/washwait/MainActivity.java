package ru.solandme.washwait;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import ru.solandme.washwait.adapters.MyForecastRVAdapter;
import ru.solandme.washwait.forecast.POJO.Forecast;

public class MainActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {

    private static final String TAG = "ru.solandme.washwait";
    private static final String TAG_ABOUT = "about";
    private static final String DEFAULT_UNITS = "metric";

    private SwipeRefreshLayout swipeRefreshLayout;

    private TextView cityField;
    private TextView updatedField;
    private TextView detailsField;
    private TextView currentTemperatureField;
    private ImageView weatherIconDay0;
    private ImageView carImage;
    private ImageView cityImage;
    private TextView forecastMessage;
    private View actionWash;

    private ProgressBar dirtyMeter;

    private SharedPreferences sharedPref;

    private RecyclerView forecastRecyclerView;
    private MyForecastRVAdapter adapter;

    ArrayList<Forecast> forecasts = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Utils.onActivityCreateSetTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sharedPref = PreferenceManager.getDefaultSharedPreferences(this);

        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.refresh);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorAccent);
        swipeRefreshLayout.setOnRefreshListener(this);

        cityField = (TextView) findViewById(R.id.city_field);
        updatedField = (TextView) findViewById(R.id.updated_field);
        detailsField = (TextView) findViewById(R.id.details_field);
        currentTemperatureField = (TextView) findViewById(R.id.current_temperature_field);
        forecastMessage = (TextView) findViewById(R.id.forecast_message);
        actionWash = findViewById(R.id.action_wash);


        weatherIconDay0 = (ImageView) findViewById(R.id.weather_icon_day0);

        carImage = (ImageView) findViewById(R.id.car_image);
        cityImage = (ImageView) findViewById(R.id.city_image);

        dirtyMeter = (ProgressBar) findViewById(R.id.precipitation_meter);

        forecastRecyclerView = (RecyclerView) findViewById(R.id.rwForecast);
        forecastRecyclerView.setHasFixedSize(true);
        forecastRecyclerView.setLayoutManager(new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false));

        adapter = new MyForecastRVAdapter(forecasts);
        forecastRecyclerView.setAdapter(adapter);

        actionWash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                float lat = sharedPref.getFloat("lat", (float) ForecastService.DEFAULT_LATITUDE);
                float lon = sharedPref.getFloat("lon", (float) ForecastService.DEFAULT_LONGITUDE);
                Intent intent = new Intent(MainActivity.this, MapActivity.class);
                intent.putExtra("lat", lat);
                intent.putExtra("lon", lon);
                intent.putExtra("lang", Locale.getDefault().getLanguage().toLowerCase());
                startActivity(intent);
            }
        });
        cityField.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chooseCity();
            }
        });
    }

    @Override
    public void onResume() {
        LocalBroadcastManager.getInstance(this).registerReceiver(receiver, new IntentFilter(
                ForecastService.NOTIFICATION));
        ForecastService.startActionGetForecast(this, false);
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver);
    }

    private void updateWashForecastUI(double dirtyCounter) {

        dirtyMeter.setMax(40);
        dirtyMeter.setProgress((int) (dirtyCounter * 10));

        carImage.setImageResource(getCarPicture(dirtyCounter, forecasts.get(0).getTemperature()));
        Animation moveFromLeft = AnimationUtils.loadAnimation(this, R.anim.move_from_left);
        carImage.startAnimation(moveFromLeft);
        Animation moveFromRight = AnimationUtils.loadAnimation(this, R.anim.move_from_right);
        cityImage.startAnimation(moveFromRight);
    }

    private void updateWeatherUI() {
        String units = sharedPref.getString("units", DEFAULT_UNITS);
        String city = sharedPref.getString("city", getResources().getString(R.string.choose_location));

        long dt = forecasts.get(0).getDate();
        double temp = forecasts.get(0).getTemperature();
        String description = forecasts.get(0).getDescription().toUpperCase();
        int icon = forecasts.get(0).getImageRes();

        cityField.setText(city);

        detailsField.setText(description);

        String unitTemperature;
        switch (units) {
            case "metric":
                unitTemperature = String.format("%sC", "\u00b0");
                break;
            case "imperial":
                unitTemperature = String.format("%sF", "\u00b0");
                break;
            default:
                unitTemperature = String.format("%sK", "\u00b0");
                break;
        }

        currentTemperatureField.setText(String.format("%s%s",
                String.format(Locale.getDefault(), "%.1f", temp),
                unitTemperature));

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMMM yyyy, HH:mm", Locale.getDefault());
        String updatedOn = dateFormat.format(new Date(dt));

        updatedField.setText(String.format("%s%s",
                getString(R.string.last_update),
                updatedOn));

        weatherIconDay0.setImageResource(icon);
    }

    private int getCarPicture(Double dirtyCounter, Double temp) {

        if (temp < -10) return R.mipmap.car1;
        if (dirtyCounter <= 0) return R.mipmap.car1;
        if (dirtyCounter > 0 && dirtyCounter < 2) return R.mipmap.car2;
        if (dirtyCounter >= 2 && dirtyCounter < 15) return R.mipmap.car3;
        if (dirtyCounter >= 15 && dirtyCounter < 50) return R.mipmap.car4;
        if (dirtyCounter >= 50) return R.mipmap.car5;

        return R.mipmap.car1;
    }

    @Override
    public void onRefresh() {
        swipeRefreshLayout.setRefreshing(true);
        ForecastService.startActionGetForecast(this, false);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.settings:
                startActivity(new Intent(this, SettingsActivity.class));
                break;
            case R.id.about_app_menu_item:
                new AboutAppDialog().show(getSupportFragmentManager(), TAG_ABOUT);
                break;
            case R.id.choose_location_action:
                chooseCity();
                break;
            case R.id.action_theme_blue:
                Utils.changeToTheme(Utils.THEME_MATERIAL_BLUE, this);
                return true;
            case R.id.action_theme_violet:
                Utils.changeToTheme(Utils.THEME_MATERIAL_VIOLET, this);
                return true;
            case R.id.action_theme_green:
                Utils.changeToTheme(Utils.THEME_MATERIAL_GREEN, this);
                return true;
        }
        return true;
    }

    private void chooseCity() {
        startActivity(new Intent(this, ChooseCityActivity.class));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            swipeRefreshLayout.setRefreshing(false);
            Bundle bundle = intent.getExtras();
            boolean isResultOK = intent.getBooleanExtra("isResultOK", false);
            if (isResultOK) {
                String textForecast = bundle.getString("TextForecast");
                forecastMessage.setText(textForecast);
                if (null != forecasts) {
                    forecasts.clear();
                }
                forecasts.addAll((ArrayList<Forecast>) bundle.get("Weather"));
                updateWeatherUI();
                adapter.notifyDataSetChanged();
                updateWashForecastUI(bundle.getDouble("DirtyCounter"));
            } else {
                Toast.makeText(getApplicationContext(), R.string.error_from_response, Toast.LENGTH_SHORT).show();
            }
        }
    };
}
