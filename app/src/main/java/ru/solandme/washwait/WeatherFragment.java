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
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import ru.solandme.washwait.data.WashCar;
import ru.solandme.washwait.POJO.CurrentWeather;
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
    private TextView weatherIcon;
    private TextView forecast;

    private TextView forecastDay1;
    private TextView forecastDay2;
    private TextView forecastDay3;
    private TextView forecastDay4;

    private TextView forecastDate1;
    private TextView forecastDate2;
    private TextView forecastDate3;
    private TextView forecastDate4;

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
        setRetainInstance(true);

        weatherFont = Typeface.createFromAsset(getActivity().getAssets(), "fonts/weather.ttf");
        sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity());
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_weather, container, false);

        swipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.refresh);
        swipeRefreshLayout.setOnRefreshListener(this);

        cityField = (TextView) rootView.findViewById(R.id.city_field);
        updatedField = (TextView) rootView.findViewById(R.id.updated_field);
        detailsField = (TextView) rootView.findViewById(R.id.details_field);
        currentTemperatureField = (TextView) rootView.findViewById(R.id.current_temperature_field);
        forecast = (TextView) rootView.findViewById(R.id.forecast);

        forecastDay1 = (TextView) rootView.findViewById(R.id.forecast_1);
        forecastDay1.setTypeface(weatherFont);
        forecastDay2 = (TextView) rootView.findViewById(R.id.forecast_2);
        forecastDay2.setTypeface(weatherFont);
        forecastDay3 = (TextView) rootView.findViewById(R.id.forecast_3);
        forecastDay3.setTypeface(weatherFont);
        forecastDay4 = (TextView) rootView.findViewById(R.id.forecast_4);
        forecastDay4.setTypeface(weatherFont);

        forecastDate1 = (TextView) rootView.findViewById(R.id.date1);
        forecastDate2 = (TextView) rootView.findViewById(R.id.date2);
        forecastDate3 = (TextView) rootView.findViewById(R.id.date3);
        forecastDate4 = (TextView) rootView.findViewById(R.id.date4);

        weatherIcon = (TextView) rootView.findViewById(R.id.weather_icon);
        weatherIcon.setTypeface(weatherFont);

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
        cityCode = sharedPref.getString("city", defaultCityCode);
        units = sharedPref.getString("units", defaultUnits);

        swipeRefreshLayout.setRefreshing(true);

        final ApiInterface apiService = ApiClient.getClient(getContext()).create(ApiInterface.class);

        Call<CurrentWeather> currentWeatherCall = apiService.getCurrentWeatherByCityName(cityCode, units, lang, appid);

        currentWeatherCall.enqueue(new Callback<CurrentWeather>() {
            @Override
            public void onResponse(Call<CurrentWeather> call, Response<CurrentWeather> response) {
                swipeRefreshLayout.setRefreshing(false);
                if (response.isSuccessful()) {
                    updateWeatherUI(response.body());
                }
            }

            @Override
            public void onFailure(Call<CurrentWeather> call, Throwable t) {
                swipeRefreshLayout.setRefreshing(false);
                Log.e(TAG, "onError: " + t);
                Toast.makeText(getContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        Call<WeatherFiveDays> weatherFiveDaysCall = apiService.getWeatherFiveDaysByCityName(cityCode, units, lang, appid);
        weatherFiveDaysCall.enqueue(new Callback<WeatherFiveDays>() {
            @Override
            public void onResponse(Call<WeatherFiveDays> call, Response<WeatherFiveDays> response) {
                if (response.isSuccessful()) {
                    updateWashForecastUI(response.body());
                    updateFiveDaysForecastUI(response.body());
                }
            }

            @Override
            public void onFailure(Call<WeatherFiveDays> call, Throwable t) {
                Log.e(TAG, "onError: " + t);
                Toast.makeText(getActivity(), t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateFiveDaysForecastUI(WeatherFiveDays body) {
        String icon1 = body.getList().get(3).getWeather().get(0).getIcon();
        String icon2 = body.getList().get(11).getWeather().get(0).getIcon();
        String icon3 = body.getList().get(19).getWeather().get(0).getIcon();
        String icon4 = body.getList().get(27).getWeather().get(0).getIcon();

        Long data1 = body.getList().get(3).getDt();
        Log.e(TAG, "onError: " + data1);
        Long data2 = body.getList().get(11).getDt();
        Log.e(TAG, "onError: " + data2);
        Long data3 = body.getList().get(19).getDt();
        Log.e(TAG, "onError: " + data3);
        Long data4 = body.getList().get(27).getDt();
        Log.e(TAG, "onError: " + data4);

        SimpleDateFormat dateFormat = new SimpleDateFormat("EE, dd", Locale.getDefault());
        String data11 = dateFormat.format(data1 * 1000);
        String data21 = dateFormat.format(data2 * 1000);
        String data31 = dateFormat.format(data3 * 1000);
        String data41 = dateFormat.format(data4 * 1000);

        forecastDay1.setText(getIconFont(icon1));
        forecastDay2.setText(getIconFont(icon2));
        forecastDay3.setText(getIconFont(icon3));
        forecastDay4.setText(getIconFont(icon4));

        forecastDate1.setText(data11);
        forecastDate2.setText(data21);
        forecastDate3.setText(data31);
        forecastDate4.setText(data41);
    }

    private void updateWashForecastUI(WeatherFiveDays weatherFiveDays) {
        String forecastText = WashCar.getForecastText(getContext(), weatherFiveDays, FORECAST_DISTANCE);
        forecast.setText(forecastText);
    }

    private void updateWeatherUI(CurrentWeather currentWeather) {

        String cityName = currentWeather.getName();
        String country = currentWeather.getSys().getCountry();
        Long dt = (long) currentWeather.getDt();
        Long humidity = currentWeather.getMain().getHumidity();
        double temp = currentWeather.getMain().getTemp();

        cityField.setText(cityName + ", " + country);
        detailsField.setTypeface(weatherFont);
        currentTemperatureField.setTypeface(weatherFont);
        detailsField.setText(currentWeather.getWeather().get(0).getDescription().toUpperCase() +
                "\n" + getString(R.string.wi_humidity) + " " + getString(R.string.humidity) + humidity + "%");

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

        currentTemperatureField.setText(String.format("%s%s",
                String.format("%.1f", temp),
                unitTemperature));

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMMM yyyy, HH:mm", Locale.getDefault());
        String updatedOn = dateFormat.format(new Date(dt * 1000));

        updatedField.setText(String.format("%s%s",
                getString(R.string.last_update),
                updatedOn));
        Log.e(TAG, "updateWeatherUI: " + currentWeather.getWeather().get(0).getIcon());

        weatherIcon.setText(getIconFont(currentWeather.getWeather().get(0).getIcon()));


    }

    @Override
    public void onRefresh() {
        swipeRefreshLayout.setRefreshing(true);
        getWeather();
        swipeRefreshLayout.setRefreshing(false);
    }

    public int getIconFont(String icon) {

        switch (icon) {
            case "01d":
                return R.string.wi_day_sunny;
            case "01n":
                return R.string.wi_night_clear;
            case "02d":
                return R.string.wi_day_cloudy;
            case "02n":
                return R.string.wi_night_cloudy;
            case "03d":
                return R.string.wi_cloud;
            case "03n":
                return R.string.wi_cloud;
            case "04d":
                return R.string.wi_cloudy;
            case "04n":
                return R.string.wi_cloudy;
            case "09d":
                return R.string.wi_day_showers;
            case "09n":
                return R.string.wi_night_showers;
            case "10d":
                return R.string.wi_day_rain_mix;
            case "10n":
                return R.string.wi_night_rain_mix;
            case "11d":
                return R.string.wi_day_rain;
            case "11n":
                return R.string.wi_night_rain;
            case "13d":
                return R.string.wi_day_snow;
            case "13n":
                return R.string.wi_night_snow;
            case "50d":
                return R.string.wi_day_fog;
            case "50n":
                return R.string.wi_night_fog;
            default:
                return R.string.wi_day_sunny;
        }
    }
}
