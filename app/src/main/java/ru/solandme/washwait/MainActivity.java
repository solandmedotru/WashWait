package ru.solandme.washwait;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Typeface;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.gcm.GcmNetworkManager;
import com.google.android.gms.gcm.PeriodicTask;
import com.google.android.gms.gcm.Task;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import ru.solandme.washwait.adapters.MyForecastRVAdapter;
import ru.solandme.washwait.data.WeatherContract;
import ru.solandme.washwait.data.WeatherDbHelper;
import ru.solandme.washwait.forecast.POJO.WeatherForecast;
import ru.solandme.washwait.utils.Utils;

public class MainActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {

    private static final String TAG = "ru.solandme.washwait";
    private static final String TAG_ABOUT = "about";
    private static final String DEFAULT_UNITS = "metric";
    public static final int PERIODICAL_TIMER = 43200;
    public static final int FIRST_DAY_POSITION = 0;
    Toolbar toolbar;
    private SwipeRefreshLayout swipeRefreshLayout;

    private TextView updatedField;
    private TextView detailsField;
    private TextView curMaxTempField;
    private TextView curMinTempField;
    private TextView humidityField;
    private TextView barometerField;
    private TextView speedWindField;
    private ImageView weatherIconDay0;
    private ImageView carImage;
    private ImageView cityImage;
    private TextView forecastMessage;

    private ProgressBar dirtyMeter;

    private SharedPreferences sharedPref;

    private RecyclerView forecastRecyclerView;
    private MyForecastRVAdapter adapter;

    private WeatherForecast weatherForecast;
    private GcmNetworkManager mGcmNetworkManager;
    private String units;
    private String city;
    private int cityId;
    private Typeface weatherFont;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Utils.onActivityCreateSetTheme(this);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        weatherFont = Typeface.createFromAsset(getAssets(), "fonts/weatherFont.ttf");

        sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        toolbar = (Toolbar) findViewById(R.id.toolbar_actionbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        mGcmNetworkManager = GcmNetworkManager.getInstance(this);

        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.refresh);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorAccent);
        swipeRefreshLayout.setOnRefreshListener(this);

        updatedField = (TextView) findViewById(R.id.updated_field);
        detailsField = (TextView) findViewById(R.id.details_field);

        curMaxTempField = (TextView) findViewById(R.id.max_t_field);
        curMaxTempField.setTypeface(weatherFont);
        curMinTempField = (TextView) findViewById(R.id.min_t_field);
        curMinTempField.setTypeface(weatherFont);

        humidityField = (TextView) findViewById(R.id.humidity_field);
        humidityField.setTypeface(weatherFont);
        barometerField = (TextView) findViewById(R.id.barometer_field);
        barometerField.setTypeface(weatherFont);
        speedWindField = (TextView) findViewById(R.id.speed_wind_field);
        speedWindField.setTypeface(weatherFont);


        forecastMessage = (TextView) findViewById(R.id.forecast_message);

        weatherIconDay0 = (ImageView) findViewById(R.id.weather_icon_day0);

        carImage = (ImageView) findViewById(R.id.car_image);
        cityImage = (ImageView) findViewById(R.id.city_image);

        dirtyMeter = (ProgressBar) findViewById(R.id.precipitation_meter);

        forecastRecyclerView = (RecyclerView) findViewById(R.id.rwForecast);
        forecastRecyclerView.setHasFixedSize(true);

        int orientation = getResources().getConfiguration().orientation;
        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            forecastRecyclerView.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        } else {
            forecastRecyclerView.setLayoutManager(new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false));
        }

        cityId = sharedPref.getInt("cityId", 2643743);

        getLastWeatherFromDB(cityId);
    }

    @NonNull
    private String getDataWithFormat(Date date) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEEE, dd MMM",
                java.util.Locale.getDefault());
        return simpleDateFormat.format(date).toUpperCase();
    }

    private void getLastWeatherFromDB(int cityId) {
        WeatherDbHelper dbHelper = new WeatherDbHelper(this);
        Cursor cursor = dbHelper.getLastWeather(cityId);

        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            detailsField.setText(cursor.getString(cursor.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_SHORT_DESC)));
            curMaxTempField.setText(cursor.getString(cursor.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_MAX_TEMP)));

            units = sharedPref.getString("units", DEFAULT_UNITS);

            double maxTemp = Double.parseDouble(cursor.getString(cursor.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_MAX_TEMP)));
            String description = cursor.getString(cursor.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_SHORT_DESC));
            long dt = Long.parseLong(cursor.getString(cursor.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_DATE)));

            toolbar.setSubtitle(getDataWithFormat(new Date(dt)));
            curMaxTempField.setText(getStringTemperature(maxTemp, units));
            detailsField.setText(description);
        }

        cursor.close();
        dbHelper.close();
    }

    @Override
    public void onResume() {
        city = sharedPref.getString("city", getResources().getString(R.string.choose_location));
        String currentData = getDataWithFormat(new Date());
        fillTitle(city, currentData);

        LocalBroadcastManager.getInstance(this).registerReceiver(receiver, new IntentFilter(
                ForecastService.NOTIFICATION));
        ForecastService.startActionGetForecast(this, ForecastService.RUN_FROM_ACTIVITY);
        checkTask();
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver);
    }

    private void checkTask() {
        if (sharedPref.getBoolean(getString(R.string.pref_task_key), false)) {
            Task task = new PeriodicTask.Builder()
                    .setService(PeriodicalForecastTask.class)
                    .setRequiredNetwork(PeriodicTask.NETWORK_STATE_CONNECTED)
                    .setPeriod(PERIODICAL_TIMER) // two times per day period
                    .setTag(PeriodicalForecastTask.TAG_TASK_PERIODIC)
                    .setPersisted(true)
                    .setUpdateCurrent(true)
                    .build();
            mGcmNetworkManager.schedule(task);
        } else {
            mGcmNetworkManager.cancelAllTasks(PeriodicalForecastTask.class);
        }
    }

    private void updateWashForecastUI(double dirtyCounter) {

        dirtyMeter.setMax(40);
        dirtyMeter.setProgress((int) (dirtyCounter * 5));

        carImage.setImageResource(getCarPicture(dirtyCounter, weatherForecast.getList().get(0).getTemp().getMax()));
        Animation moveFromLeft = AnimationUtils.loadAnimation(this, R.anim.move_from_left);
        carImage.startAnimation(moveFromLeft);
        Animation moveFromRight = AnimationUtils.loadAnimation(this, R.anim.move_from_right);
        cityImage.startAnimation(moveFromRight);

        carImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startWashCarActivity();
            }
        });
    }

    private String getStringTemperature(double maxTemp, String units) {
        String unitTemperature;
        switch (units) {
            case "metric":
                unitTemperature = String.format("%s", getString(R.string.wi_celsius));
                break;
            case "imperial":
                unitTemperature = String.format("%s", getString(R.string.wi_fahrenheit));
                break;
            default:
                unitTemperature = String.format("%sK", "\u00b0");
                break;
        }
        return String.format("%s%s",
                (int) Math.round(maxTemp),
                unitTemperature);
    }

    private int getCarPicture(Double dirtyCounter, Double temp) {

        if (temp < -10) return R.mipmap.car10;
        if (dirtyCounter <= 0) return R.mipmap.car10;
        if (dirtyCounter > 0 && dirtyCounter < 1) return R.mipmap.car2;
        if (dirtyCounter >= 1 && dirtyCounter < 10) return R.mipmap.car3;
        if (dirtyCounter >= 10 && dirtyCounter < 30) return R.mipmap.car4;
        if (dirtyCounter >= 30) return R.mipmap.car5;

        return R.mipmap.car10;
    }

    @Override
    public void onRefresh() {
        swipeRefreshLayout.setRefreshing(true);
        ForecastService.startActionGetForecast(this, ForecastService.RUN_FROM_ACTIVITY);
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
                break;
            case R.id.action_theme_violet:
                Utils.changeToTheme(Utils.THEME_MATERIAL_VIOLET, this);
                break;
            case R.id.action_theme_green:
                Utils.changeToTheme(Utils.THEME_MATERIAL_GREEN, this);
                break;
            case R.id.action_theme_night:
                Utils.changeToTheme(Utils.THEME_MATERIAL_DAYNIGHT, this);
                break;
            case R.id.action_view_wash:
                startWashCarActivity();
                break;
            case R.id.clear_cache:
                WeatherDbHelper dbHelper = new WeatherDbHelper(this);
                dbHelper.clearCache();
                dbHelper.close();
                break;
        }
        return true;
    }

    private void startWashCarActivity() {
        float lat = sharedPref.getFloat("lat", (float) ForecastService.DEFAULT_LATITUDE);
        float lon = sharedPref.getFloat("lon", (float) ForecastService.DEFAULT_LONGITUDE);
        Intent intent = new Intent(MainActivity.this, MapActivity.class);
        intent.putExtra("lat", lat);
        intent.putExtra("lon", lon);
        intent.putExtra("lang", Locale.getDefault().getLanguage().toLowerCase());
        startActivity(intent);
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

                weatherForecast = (WeatherForecast) bundle.get("Weather");

                fillWeatherCard(FIRST_DAY_POSITION);

                adapter = new MyForecastRVAdapter(weatherForecast);
                forecastRecyclerView.setAdapter(adapter);

                adapter.setOnItemClickListener(new MyForecastRVAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        fillWeatherCard(position);
                    }
                });

                adapter.notifyDataSetChanged();
                updateWashForecastUI(bundle.getDouble("DirtyCounter"));
            } else {
                Toast.makeText(getApplicationContext(), R.string.error_from_response, Toast.LENGTH_SHORT).show();
            }
        }
    };

    private void fillWeatherCard(int position) {
        units = sharedPref.getString("units", DEFAULT_UNITS);
        city = sharedPref.getString("city", getResources().getString(R.string.choose_location));

        double maxTemp = weatherForecast.getList().get(position).getTemp().getMax();
        double minTemp = weatherForecast.getList().get(position).getTemp().getMin();
        String description = weatherForecast.getList().get(position).getWeather().get(0).getDescription();
        int icon = weatherForecast.getList().get(position).getImageRes();
        long dt = weatherForecast.getList().get(position).getDt() * 1000;
        int humidity = weatherForecast.getList().get(position).getHumidity();
        double barometer = weatherForecast.getList().get(position).getPressure();
        double speedWind = weatherForecast.getList().get(position).getSpeed();
        int speedDirection = weatherForecast.getList().get(position).getDeg();

        fillTitle(city, getDataWithFormat(new Date(dt)));

        curMaxTempField.setText(getStringTemperature(maxTemp, units));
        curMinTempField.setText(getStringTemperature(minTemp, units));
        humidityField.setText(getString(R.string.wi_humidity) + " " + humidity + "%");
        barometerField.setText(getStringBarometer(barometer, units));
        speedWindField.setText(getStringWind(speedDirection, speedWind, units));

        detailsField.setText(description);
        weatherIconDay0.setImageResource(icon);

        long dtLast = weatherForecast.getList().get(0).getDt() * 1000;
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMMM yyyy, HH:mm", Locale.getDefault());
        String updatedOn = dateFormat.format(new Date(dtLast));

        updatedField.setText(String.format("%s%s",
                getString(R.string.last_update),
                updatedOn));
    }

    private void fillTitle(String city, String dataWithFormat) {
        toolbar.setTitle(city);
        toolbar.setSubtitle(dataWithFormat);
    }

    @NonNull
    private String getStringBarometer(double barometer, String units) {
        String barUnits;
        switch (units) {
            case "metric":
                barUnits = String.format("%s", getString(R.string.m_hg));
                barometer = Math.round(barometer * 100 / 133.3224);
                break;
            case "imperial":
                barUnits = String.format("%s", getString(R.string.h_pa));
                break;
            default:
                barUnits = String.format("%s", getString(R.string.m_hg));
                break;
        }
        return String.format("%s %s %s",
                getString(R.string.wi_barometer),
                (int) barometer,
                barUnits);
    }

    @NonNull
    private String getStringWind(int speedDirection, double speedWind, String units) {
        String windUnits;
        switch (units) {
            case "metric":
                windUnits = String.format("%s", getString(R.string.meter_per_sec));
                break;
            case "imperial":
                windUnits = String.format("%s", getString(R.string.miles_per_h));
                break;
            default:
                windUnits = String.format("%s", getString(R.string.meter_per_sec));
                break;
        }
        return String.format("%s %s %s",
                getString(getWindRes(speedDirection)),
                (int) Math.round(speedWind),
                windUnits);
    }

    private int getWindRes(int direction) {
        int val = (int) Math.round(((double) direction % 360) / 45);
        switch (val % 16) {
            case 0:
                return R.string.wi_wind_north;
            case 1:
                return R.string.wi_wind_north_east;
            case 2:
                return R.string.wi_wind_east;
            case 3:
                return R.string.wi_wind_south_east;
            case 4:
                return R.string.wi_wind_south;
            case 5:
                return R.string.wi_wind_south_west;
            case 6:
                return R.string.wi_wind_west;
            case 7:
                return R.string.wi_wind_north_west;
            case 8:
                return R.string.wi_wind_north;
        }
        return R.string.wi_wind_east;
    }
}
