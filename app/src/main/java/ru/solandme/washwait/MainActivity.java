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

import ru.solandme.washwait.POJO.forecast.WeatherForecast;
import ru.solandme.washwait.POJO.weather.CurrWeather;
import ru.solandme.washwait.adapters.MyForecastRVAdapter;
import ru.solandme.washwait.data.WeatherContract;
import ru.solandme.washwait.data.WeatherDbHelper;
import ru.solandme.washwait.utils.Utils;
import ru.solandme.washwait.utils.WeatherUtils;

public class MainActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {

    public static final int PERIODICAL_TIMER = 43200; //21600
    public static final int FIRST_DAY_POSITION = 0;
    private static final String TAG_ABOUT = "about";
    private static final String DEFAULT_UNITS = "metric";
    private Toolbar toolbar;
    private SwipeRefreshLayout swipeRefreshLayout;
    private TextView updatedField, detailsField, curMaxTempField, curMinTempField, humidityField, barometerField, speedWindField, forecastMessage;
    private ImageView weatherIconDay0, carImage, cityImage;
    private ProgressBar dirtyMeter;
    private SharedPreferences sharedPref;
    private RecyclerView forecastRecyclerView;
    private WeatherForecast weatherForecast;
    private CurrWeather currWeather;
    private GcmNetworkManager mGcmNetworkManager;
    private String units, city;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Utils.onActivityCreateSetTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = (Toolbar) findViewById(R.id.toolbar_actionbar);
        setSupportActionBar(toolbar);

        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.refresh);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorAccent);
        swipeRefreshLayout.setOnRefreshListener(this);

        updatedField = (TextView) findViewById(R.id.updated_field);
        detailsField = (TextView) findViewById(R.id.details_field);
        forecastMessage = (TextView) findViewById(R.id.forecast_message);
        weatherIconDay0 = (ImageView) findViewById(R.id.weather_icon_day0);
        carImage = (ImageView) findViewById(R.id.car_image);
        cityImage = (ImageView) findViewById(R.id.city_image);
        dirtyMeter = (ProgressBar) findViewById(R.id.precipitation_meter);
        forecastRecyclerView = (RecyclerView) findViewById(R.id.rwForecast);
        forecastRecyclerView.setHasFixedSize(true);

        Typeface weatherFont = Typeface.createFromAsset(getAssets(), "fonts/weatherFont.ttf");
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

        int orientation = getResources().getConfiguration().orientation;
        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            forecastRecyclerView.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        } else {
            forecastRecyclerView.setLayoutManager(new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false));
        }

        mGcmNetworkManager = GcmNetworkManager.getInstance(this);
        sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        getLastWeatherFromDB(sharedPref.getInt(getString(R.string.pref_city_id_key), 2643743));
    }

    @Override
    public void onResume() {
        city = sharedPref.getString(getString(R.string.pref_city_key), getResources().getString(R.string.choose_location));
        units = sharedPref.getString(getString(R.string.pref_units_key), DEFAULT_UNITS);

        String currentData = getFormattedDate(new Date());
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

    private void getLastWeatherFromDB(int cityId) {
        WeatherDbHelper dbHelper = new WeatherDbHelper(this);
        Cursor cursor = dbHelper.getLastWeather(cityId);

        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            detailsField.setText(cursor.getString(cursor.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_SHORT_DESC)));
            curMaxTempField.setText(cursor.getString(cursor.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_MAX_TEMP)));
            double maxTemp = Double.parseDouble(cursor.getString(cursor.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_MAX_TEMP)));
            String description = cursor.getString(cursor.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_SHORT_DESC));
            long dt = Long.parseLong(cursor.getString(cursor.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_DATE)));

            toolbar.setSubtitle(getFormattedDate(new Date(dt)));
            curMaxTempField.setText(WeatherUtils.getStringTemperature(maxTemp, units, this));
            detailsField.setText(description);
        }
        cursor.close();
        dbHelper.close();
    }

    private String getFormattedDate(Date date) {
        return new SimpleDateFormat("EEEE, dd MMM",
                java.util.Locale.getDefault()).format(date).toUpperCase();
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

    private void updateCarImage(int position) {

        carImage.setImageResource(getCarPicture(weatherForecast.getList().get(position).getDirtyCounter(), weatherForecast.getList().get(position).getTemp().getMax()));
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


    private int getCarPicture(Double dirtyCounter, Double temp) {

        if (temp < -7) return R.drawable.car10;
        if (dirtyCounter > 0 && dirtyCounter < 2) return R.drawable.car1;
        if (dirtyCounter >= 2 && dirtyCounter < 14) return R.drawable.car2;
        if (dirtyCounter >= 14 && dirtyCounter < 40) return R.drawable.car3;
        if (dirtyCounter >= 40) return R.drawable.car4;

        return R.drawable.car10;
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
        float lat = sharedPref.getFloat(getString(R.string.pref_lat_key), (float) ForecastService.DEFAULT_LATITUDE);
        float lon = sharedPref.getFloat(getString(R.string.pref_lon_key), (float) ForecastService.DEFAULT_LONGITUDE);
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
            boolean isForecastResultOK = intent.getBooleanExtra("isForecastResultOK", false);
            boolean isCurrWeatherResultOK = intent.getBooleanExtra("isCurrWeatherResultOK", false);

            if (isForecastResultOK && isCurrWeatherResultOK) {
                String textForecast = bundle.getString("TextForecast");
                forecastMessage.setText(textForecast);

                weatherForecast = (WeatherForecast) bundle.get("Weather");
                currWeather = (CurrWeather) bundle.get("CurrWeather");

                fillWeatherCard(FIRST_DAY_POSITION);

                MyForecastRVAdapter adapter = new MyForecastRVAdapter(weatherForecast);
                forecastRecyclerView.setAdapter(adapter);

                adapter.setOnItemClickListener(new MyForecastRVAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        fillWeatherCard(position);
                    }
                });

                adapter.notifyDataSetChanged();
                updateCarImage(FIRST_DAY_POSITION);
            } else {
                Toast.makeText(getApplicationContext(), R.string.error_from_response, Toast.LENGTH_SHORT).show();
            }
        }
    };

    private void fillWeatherCard(int position) {
        double maxTemp;
        double minTemp;
        String description;
        int icon;
        long dt;
        long dtLast;
        int humidity;
        double barometer;
        double speedWind;
        int speedDirection;

        if (position == FIRST_DAY_POSITION) {
            maxTemp = currWeather.getMain().getTempMax();
            minTemp = currWeather.getMain().getTempMin();
            description = currWeather.getWeather().get(0).getDescription();
            icon = currWeather.getImageRes();
            dt = currWeather.getDt() * 1000;
            humidity = currWeather.getMain().getHumidity();
            barometer = currWeather.getMain().getPressure();
            speedWind = currWeather.getWind().getSpeed();
            speedDirection = (int) currWeather.getWind().getDeg();
            dtLast = currWeather.getDt() * 1000;

        } else {
            maxTemp = weatherForecast.getList().get(position).getTemp().getMax();
            minTemp = weatherForecast.getList().get(position).getTemp().getMin();
            description = weatherForecast.getList().get(position).getWeather().get(0).getDescription();
            icon = weatherForecast.getList().get(position).getImageRes();
            dt = weatherForecast.getList().get(position).getDt() * 1000;
            humidity = weatherForecast.getList().get(position).getHumidity();
            barometer = weatherForecast.getList().get(position).getPressure();
            speedWind = weatherForecast.getList().get(position).getSpeed();
            speedDirection = weatherForecast.getList().get(position).getDeg();
            dtLast = weatherForecast.getList().get(0).getDt() * 1000;

        }

        fillTitle(city, getFormattedDate(new Date(dt)));

        curMaxTempField.setText(WeatherUtils.getStringTemperature(maxTemp, units, this));
        curMinTempField.setText(WeatherUtils.getStringTemperature(minTemp, units, this));
        humidityField.setText(getString(R.string.wi_humidity) + " " + humidity + "%");
        barometerField.setText(WeatherUtils.getStringBarometer(barometer, units, this));
        speedWindField.setText(WeatherUtils.getStringWind(speedDirection, speedWind, units, this));

        double dirtyCounter = weatherForecast.getList().get(position).getDirtyCounter();
        dirtyMeter.setMax(50);
        dirtyMeter.setProgress((int) (dirtyCounter * 2));

        detailsField.setText(description);
        weatherIconDay0.setImageResource(icon);

        updateCarImage(position);
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
}
