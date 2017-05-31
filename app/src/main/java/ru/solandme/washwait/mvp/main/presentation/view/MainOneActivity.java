package ru.solandme.washwait.mvp.main.presentation.view;

import android.content.res.Configuration;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.gcm.GcmNetworkManager;
import com.google.firebase.analytics.FirebaseAnalytics;

import ru.solandme.washwait.Constants;
import ru.solandme.washwait.R;
import ru.solandme.washwait.mvp.main.presentation.presenter.IMainPresenter;
import ru.solandme.washwait.mvp.main.presentation.presenter.MainPresenter;
import ru.solandme.washwait.utils.Utils;

public class MainOneActivity extends AppCompatActivity implements IMainView, SwipeRefreshLayout.OnRefreshListener {
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
    private ImageView weatherIconToday;
    private ImageView carImage;
    private ImageView cityImage;
    private ProgressBar dirtyMeter;
    private RecyclerView forecastRecyclerView;

    private GcmNetworkManager mGcmNetworkManager;

    private IMainPresenter mainPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Utils.onActivityCreateSetTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_one);

        setUpToolbar();
        setUpViews();
        setUpAnalytics();

        if (mainPresenter == null) {
            mainPresenter = new MainPresenter(this);
        }
    }

    private void setUpAnalytics() {
        FirebaseAnalytics mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        mFirebaseAnalytics.setAnalyticsCollectionEnabled(true);
        mFirebaseAnalytics.setMinimumSessionDuration(Constants.MINIMUM_SESSION_DURATION);
        mGcmNetworkManager = GcmNetworkManager.getInstance(this);
    }

    private void setUpViews() {
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.refresh);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorAccent);
        swipeRefreshLayout.setOnRefreshListener(this);

        updatedField = (TextView) findViewById(R.id.updated_field);
        detailsField = (TextView) findViewById(R.id.details_field);
        forecastMessage = (TextView) findViewById(R.id.forecast_message);
        weatherIconToday = (ImageView) findViewById(R.id.weather_icon_today);
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
    }

    private void setUpToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar_actionbar);
        setSupportActionBar(toolbar);
    }

    @Override
    public void onRefresh() {
        mainPresenter.onRefresh();
    }

    @Override
    public void startProgress() {
        swipeRefreshLayout.setRefreshing(true);
    }

    @Override
    public void stopProgress() {
        swipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void showError(String errorMessage) {
        Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show();
    }

    @Override
    public void showCurrentMaxTemperature(String currentMaxTemp) {
        curMaxTempField.setText(currentMaxTemp);
    }

    @Override
    public void navigateToMap() {

    }

    @Override
    protected void onResume() {
        super.onResume();
        mainPresenter.attachView(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mainPresenter.detachView();
    }
}
