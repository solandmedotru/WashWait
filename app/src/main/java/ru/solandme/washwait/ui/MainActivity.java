package ru.solandme.washwait.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.graphics.Typeface;
import android.os.Bundle;
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
import com.google.firebase.analytics.FirebaseAnalytics;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import ru.solandme.washwait.Constants;
import ru.solandme.washwait.MeteoWashService;
import ru.solandme.washwait.PeriodicalMeteoWashTask;
import ru.solandme.washwait.R;
import ru.solandme.washwait.adapters.MyForecastRVAdapter;
import ru.solandme.washwait.model.washForecast.MyWeatherForecast;
import ru.solandme.washwait.utils.FormatUtils;
import ru.solandme.washwait.utils.SharedPrefsUtils;
import ru.solandme.washwait.utils.Utils;

public class MainActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {

    private Toolbar toolbar;
    private SwipeRefreshLayout swipeRefreshLayout;
    private TextView updatedField;
    private TextView detailsField;
    private TextView curMaxTempField;
    private TextView curMinTempField;
    private TextView humidityField;
    private TextView barometerField;
    private TextView speedWindField;
    private TextView forecastMessage;
    private ImageView weatherIconDay0;
    private ImageView carImage;
    private ImageView cityImage;
    private ProgressBar dirtyMeter;
    private RecyclerView forecastRecyclerView;
    private MyWeatherForecast myWeatherForecast;

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

        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            forecastRecyclerView.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        } else {
            forecastRecyclerView.setLayoutManager(new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false));
        }

        FirebaseAnalytics mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        mFirebaseAnalytics.setAnalyticsCollectionEnabled(true);
        mFirebaseAnalytics.setMinimumSessionDuration(Constants.MINIMUM_SESSION_DURATION);

        mGcmNetworkManager = GcmNetworkManager.getInstance(this);
    }

    @Override
    public void onResume() {
        city = SharedPrefsUtils.getStringPreference(this, getString(R.string.pref_city_key), getResources().getString(R.string.choose_location));
        units = SharedPrefsUtils.getStringPreference(this, getString(R.string.pref_units_key), Constants.DEFAULT_UNITS);

        String currentData = getFormattedDate(new Date());
        fillTitle(city, currentData);

        LocalBroadcastManager.getInstance(this).registerReceiver(receiver, new IntentFilter(
                Constants.NOTIFICATION));
        MeteoWashService.startActionGetForecast(this, Constants.RUN_FROM_ACTIVITY);
        checkTask();
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver);
    }

    private String getFormattedDate(Date date) {
        return new SimpleDateFormat("EEEE, dd MMM",
                java.util.Locale.getDefault()).format(date).toUpperCase();
    }

    private void checkTask() {
        if (SharedPrefsUtils.getBooleanPreference(this, getString(R.string.pref_task_key), false)) {
            Task task = new PeriodicTask.Builder()
                    .setService(PeriodicalMeteoWashTask.class)
                    .setRequiredNetwork(PeriodicTask.NETWORK_STATE_CONNECTED)
                    .setPeriod(Constants.PERIODICAL_TIMER)
                    .setTag(Constants.TAG_TASK_PERIODIC)
                    .setPersisted(true)
                    .setUpdateCurrent(true)
                    .build();
            mGcmNetworkManager.schedule(task);
        } else {
            mGcmNetworkManager.cancelAllTasks(PeriodicalMeteoWashTask.class);
        }
    }

    private void updateCarImage(int position) {

        carImage.setImageResource(myWeatherForecast.getMyWeatherList().get(position).getCarPicture());
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

    @Override
    public void onRefresh() {
        swipeRefreshLayout.setRefreshing(true);
        MeteoWashService.startActionGetForecast(this, Constants.RUN_FROM_ACTIVITY);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.settings:
                startActivity(new Intent(this, SettingsActivity.class));
                break;
            case R.id.about_app_menu_item:
                new AboutAppDialog().show(getSupportFragmentManager(), Constants.TAG_ABOUT);
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
        }
        return true;
    }

    private void startWashCarActivity() {
        float lat = SharedPrefsUtils.getFloatPreference(this, getString(R.string.pref_lat_key), (float) Constants.DEFAULT_LATITUDE);
        float lon = SharedPrefsUtils.getFloatPreference(this, getString(R.string.pref_lon_key), (float) Constants.DEFAULT_LONGITUDE);
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


                myWeatherForecast = (MyWeatherForecast) bundle.get("Weather");

                fillWeatherCard(Constants.FIRST_DAY_POSITION);

                MyForecastRVAdapter adapter = new MyForecastRVAdapter(myWeatherForecast);
                forecastRecyclerView.setAdapter(adapter);

                adapter.setOnItemClickListener(new MyForecastRVAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        fillWeatherCard(position);
                    }
                });

                adapter.notifyDataSetChanged();
                updateCarImage(Constants.FIRST_DAY_POSITION);
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

        maxTemp = Math.round(myWeatherForecast.getMyWeatherList().get(position).getTempMax());
        minTemp = Math.round(myWeatherForecast.getMyWeatherList().get(position).getTempMin());
        description = myWeatherForecast.getMyWeatherList().get(position).getDescription();
        icon = myWeatherForecast.getMyWeatherList().get(position).getImageRes();
        dt = myWeatherForecast.getMyWeatherList().get(position).getTime() * 1000;
        humidity = Math.round(myWeatherForecast.getMyWeatherList().get(position).getHumidity());
        barometer = myWeatherForecast.getMyWeatherList().get(position).getPressure();
        speedWind = Math.round(myWeatherForecast.getMyWeatherList().get(position).getWindSpeed());
        speedDirection = Math.round(myWeatherForecast.getMyWeatherList().get(position).getWindDirection());
        dtLast = myWeatherForecast.getMyWeatherList().get(0).getTime() * 1000;


        fillTitle(city, getFormattedDate(new Date(dt)));

        curMaxTempField.setText(FormatUtils.getStringTemperature(this, maxTemp, units));
        curMinTempField.setText(FormatUtils.getStringTemperature(this, minTemp, units));
        humidityField.setText(FormatUtils.getStringHumidity(this, humidity));
        barometerField.setText(FormatUtils.getStringBarometer(this, barometer, units));
        speedWindField.setText(FormatUtils.getStringWind(this, speedDirection, speedWind, units));

        float precipitation = myWeatherForecast.getMyWeatherList().get(position).getPrecipitation();
        dirtyMeter.setMax(300);
        dirtyMeter.setProgress(Math.round(precipitation * 100));

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
