package ru.solandme.washwait;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import ru.solandme.washwait.data.WashCar;
import ru.solandme.washwait.POJO.WeatherFiveDays;
import ru.solandme.washwait.rest.ApiClient;
import ru.solandme.washwait.rest.ApiInterface;

public class WeatherFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    private static final String TAG = "ru.solandme.washwait";
    private static final int FORECAST_DISTANCE = 8;
    private Typeface weatherFont;

    private SwipeRefreshLayout swipeRefreshLayout;

    private TextView cityField;
    private TextView updatedField;
    private TextView detailsField;
    private TextView currentTemperatureField;
    private ImageView weatherIconDay0;
    private ImageView carImage;
    private ImageView cityImage;
    private TextView forecastMessage;

    private ImageView weatherIconDay1;
    private ImageView weatherIconDay2;
    private ImageView weatherIconDay3;
    private ImageView weatherIconDay4;

    private TextView forecastDate1;
    private TextView forecastDate2;
    private TextView forecastDate3;
    private TextView forecastDate4;

    private ProgressBar dirtyMeter;

    private String lat = "35";
    private String lon = "139";
    private String cnt = "10";

    private String appid = BuildConfig.OPEN_WEATHER_MAP_API_KEY;

    private String defaultCityCode = "428000";
    private String defaultUnits = "metric";

    private String lang = Locale.getDefault().getLanguage();
    private String cityCode;
    private String units;
    private SharedPreferences sharedPref;

    public WeatherFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        weatherFont = Typeface.createFromAsset(getActivity().getAssets(), "fonts/weather.ttf");
        sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity());
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_weather, container, false);

        swipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.refresh);
        swipeRefreshLayout.setOnRefreshListener(this);

        cityField = (TextView) rootView.findViewById(R.id.city_field);
        updatedField = (TextView) rootView.findViewById(R.id.updated_field);
        detailsField = (TextView) rootView.findViewById(R.id.details_field);
        currentTemperatureField = (TextView) rootView.findViewById(R.id.current_temperature_field);
        forecastMessage = (TextView) rootView.findViewById(R.id.forecast_message);

        weatherIconDay0 = (ImageView) rootView.findViewById(R.id.weather_icon_day0);
        weatherIconDay1 = (ImageView) rootView.findViewById(R.id.weather_icon_day1);
        weatherIconDay2 = (ImageView) rootView.findViewById(R.id.weather_icon_day2);
        weatherIconDay3 = (ImageView) rootView.findViewById(R.id.weather_icon_day3);
        weatherIconDay4 = (ImageView) rootView.findViewById(R.id.weather_icon_day4);

        forecastDate1 = (TextView) rootView.findViewById(R.id.date1);
        forecastDate2 = (TextView) rootView.findViewById(R.id.date2);
        forecastDate3 = (TextView) rootView.findViewById(R.id.date3);
        forecastDate4 = (TextView) rootView.findViewById(R.id.date4);

        carImage = (ImageView) rootView.findViewById(R.id.car_image);
        cityImage = (ImageView) rootView.findViewById(R.id.city_image);

        dirtyMeter = (ProgressBar) rootView.findViewById(R.id.dirty_meter);

        detailsField.setTypeface(weatherFont);

        setHasOptionsMenu(true);
        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.settings:
                startActivity(new Intent(getActivity(), SettingsActivity.class));
        }
        return true;
    }

    @Override
    public void onResume() {
        super.onResume();
        getWeather();
    }

    void getWeather() {
        swipeRefreshLayout.setRefreshing(true);

        cityCode = sharedPref.getString("city", defaultCityCode);
        units = sharedPref.getString("units", defaultUnits);

        final ApiInterface apiService = ApiClient.getClient(getContext()).create(ApiInterface.class);

        Call<WeatherFiveDays> weatherFiveDaysCall = apiService.getWeatherFiveDaysByCityName(cityCode, units, lang, appid);
        weatherFiveDaysCall.enqueue(new Callback<WeatherFiveDays>() {
            @Override
            public void onResponse(Call<WeatherFiveDays> call, Response<WeatherFiveDays> response) {
                if (response.isSuccessful()) {

                    swipeRefreshLayout.setRefreshing(false);

                    updateWeatherUI(response.body());
                    updateWashForecastUI(response.body());
                    updateFiveDaysForecastUI(response.body());
                }
            }

            @Override
            public void onFailure(Call<WeatherFiveDays> call, Throwable t) {

                swipeRefreshLayout.setRefreshing(false);

                Log.e(TAG, "onError: " + t);
                Toast.makeText(getActivity(), t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateFiveDaysForecastUI(WeatherFiveDays body) {
        String icon1 = body.getList().get(8).getWeather().get(0).getIcon();
        String icon2 = body.getList().get(16).getWeather().get(0).getIcon();
        String icon3 = body.getList().get(24).getWeather().get(0).getIcon();
        String icon4 = body.getList().get(32).getWeather().get(0).getIcon();

        SimpleDateFormat dateFormat = new SimpleDateFormat("EE, dd", Locale.getDefault());
        String data11 = dateFormat.format(body.getList().get(8).getDt() * 1000);
        String data21 = dateFormat.format(body.getList().get(16).getDt() * 1000);
        String data31 = dateFormat.format(body.getList().get(24).getDt() * 1000);
        String data41 = dateFormat.format(body.getList().get(32).getDt() * 1000);

        weatherIconDay1.setImageResource(getWeatherPicture(icon1));
        weatherIconDay2.setImageResource(getWeatherPicture(icon2));
        weatherIconDay3.setImageResource(getWeatherPicture(icon3));
        weatherIconDay4.setImageResource(getWeatherPicture(icon4));

        forecastDate1.setText(data11);
        forecastDate2.setText(data21);
        forecastDate3.setText(data31);
        forecastDate4.setText(data41);
    }

    private void updateWashForecastUI(WeatherFiveDays weatherFiveDays) {
        String forecastText = WashCar.getForecastText(getContext(), weatherFiveDays, FORECAST_DISTANCE);
        forecastMessage.setText(forecastText);

        Double dirtyCounter = WashCar.getDirtyCounter(weatherFiveDays)*100;
        dirtyMeter.setMax(60);
        dirtyMeter.setProgress(dirtyCounter.intValue()+10);

        carImage.setImageResource(getCarPicture(dirtyCounter));
        Animation moveFromLeft = AnimationUtils.loadAnimation(getActivity(), R.anim.move_from_left);
        carImage.startAnimation(moveFromLeft);
        Animation moveFromRight = AnimationUtils.loadAnimation(getActivity(), R.anim.move_from_right);
        cityImage.startAnimation(moveFromRight);
    }

    private void updateWeatherUI(WeatherFiveDays currentWeather) {

        String cityName = currentWeather.getCity().getName();
        String country = currentWeather.getCity().getCountry();
        Long dt = currentWeather.getList().get(0).getDt();
        double temp = currentWeather.getList().get(0).getMain().getTemp();
        String description = currentWeather.getList().get(0).getWeather().get(0).getDescription().toUpperCase();
        String iconString = currentWeather.getList().get(0).getWeather().get(0).getIcon();

        cityField.setText(cityName + ", " + country);

        currentTemperatureField.setTypeface(weatherFont);
        detailsField.setText(description);

        String unitTemperature;
        switch (units) {
            case "metric":
                unitTemperature = String.format("%sC", getString(R.string.wi_degrees));
                break;
            case "imperial":
                unitTemperature = String.format("%sF", getString(R.string.wi_degrees));
                break;
            default:
                unitTemperature = String.format("%sK", getString(R.string.wi_degrees));
                break;
        }

        currentTemperatureField.setText(String.format("%s %s%s",
                getResources().getString(R.string.wi_thermometer),
                String.format("%.1f", temp),
                unitTemperature));

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMMM yyyy, HH:mm", Locale.getDefault());
        String updatedOn = dateFormat.format(new Date(dt * 1000));

        updatedField.setText(String.format("%s%s",
                getString(R.string.last_update),
                updatedOn));

        Log.e(TAG, "updateWeatherUI: " + iconString);
        weatherIconDay0.setImageResource(getWeatherPicture(iconString));


    }

    private int getCarPicture(Double dirtyCounter) {

        if (dirtyCounter < 1) return R.mipmap.car1;
        if (dirtyCounter >= 1 && dirtyCounter < 5) return R.mipmap.car2;
        if (dirtyCounter >= 5 && dirtyCounter < 10) return R.mipmap.car3;
        if (dirtyCounter >= 10 && dirtyCounter < 50) return R.mipmap.car4;
        if (dirtyCounter >= 50) return R.mipmap.car5;

        return R.mipmap.car1;
    }

    @Override
    public void onRefresh() {
        getWeather();
    }

    public int getWeatherPicture(String icon) {

        switch (icon) {
            case "01d":
                return R.mipmap.clear_d;
            case "01n":
                return R.mipmap.clear_n;
            case "02d":
                return R.mipmap.few_clouds_d;
            case "02n":
                return R.mipmap.few_clouds_n;
            case "03d":
                return R.mipmap.scattered_clouds;
            case "03n":
                return R.mipmap.scattered_clouds;
            case "04d":
                return R.mipmap.broken_clouds;
            case "04n":
                return R.mipmap.broken_clouds;
            case "09d":
                return R.mipmap.shower_rain_d;
            case "09n":
                return R.mipmap.shower_rain_n;
            case "10d":
                return R.mipmap.rain_d;
            case "10n":
                return R.mipmap.rain_n;
            case "11d":
                return R.mipmap.thunder_d;
            case "11n":
                return R.mipmap.thunder_n;
            case "13d":
                return R.mipmap.snow_d;
            case "13n":
                return R.mipmap.snow_n;
            case "50d":
                return R.mipmap.fog;
            case "50n":
                return R.mipmap.fog;
            default:
                return R.mipmap.few_clouds_d;
        }
    }
}
