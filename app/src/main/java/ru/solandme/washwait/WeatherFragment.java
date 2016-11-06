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
                Log.e(TAG, "onError: " + t);

                swipeRefreshLayout.setRefreshing(false);
                Toast.makeText(getContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        Call<WeatherFiveDays> weatherFiveDaysCall = apiService.getWeatherFiveDaysByCityName(cityCode, units, lang, appid);
        weatherFiveDaysCall.enqueue(new Callback<WeatherFiveDays>() {
            @Override
            public void onResponse(Call<WeatherFiveDays> call, Response<WeatherFiveDays> response) {
                if (response.isSuccessful()) {
                    updateWashForecastUI(response.body());
                }
            }

            @Override
            public void onFailure(Call<WeatherFiveDays> call, Throwable t) {
                Log.e(TAG, "onError: " + t);
                Toast.makeText(getActivity(), t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateWashForecastUI(WeatherFiveDays weatherFiveDays) {
        String forecastText = new WashCar(weatherFiveDays, FORECAST_DISTANCE)
                .getForecastText(getContext());
        forecast.setText(forecastText);
    }

    private void updateWeatherUI(CurrentWeather currentWeather) {

        String cityName = currentWeather.getName();
        Long dt = (long) currentWeather.getDt();
        Long humidity = currentWeather.getMain().getHumidity();
        double temp = currentWeather.getMain().getTemp();
        double pressure = currentWeather.getMain().getPressure();

        cityField.setText(cityName);
        detailsField.setTypeface(weatherFont);
        currentTemperatureField.setTypeface(weatherFont);
        detailsField.setText(currentWeather.getWeather().get(0).getDescription().toUpperCase() +
                "\n" + getString(R.string.wi_humidity) + " " + getString(R.string.humidity) + humidity + "%" +
                "\n" + getString(R.string.wi_barometer) + " " + getString(R.string.pressure) + pressure + " hPa");

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

        currentTemperatureField.setText(String.format("%s %s %s",
                getString(R.string.wi_thermometer),
                String.format("%.1f", temp),
                unitTemperature));

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMMM yyyy, HH:mm", Locale.getDefault());
        String updatedOn = dateFormat.format(new Date(dt * 1000));

        updatedField.setText(String.format("%s%s",
                getString(R.string.last_update),
                updatedOn));
        Log.e(TAG, "updateWeatherUI: " + currentWeather.getWeather().get(0).getIcon() );
        switch (currentWeather.getWeather().get(0).getIcon()) {
            case "01d":
                weatherIcon.setText(R.string.wi_day_sunny);
                break;
            case "01n":
                weatherIcon.setText(R.string.wi_night_clear);
                break;
            case "02d":
                weatherIcon.setText(R.string.wi_day_cloudy);
                break;
            case "02n":
                weatherIcon.setText(R.string.wi_night_cloudy);
                break;
            case "03d":
                weatherIcon.setText(R.string.wi_cloud);
                break;
            case "03n":
                weatherIcon.setText(R.string.wi_cloud);
                break;
            case "04d":
                weatherIcon.setText(R.string.wi_cloudy);
                break;
            case "04n":
                weatherIcon.setText(R.string.wi_cloudy);
                break;
            case "09d":
                weatherIcon.setText(R.string.wi_day_showers);
                break;
            case "09n":
                weatherIcon.setText(R.string.wi_night_showers);
                break;
            case "10d":
                weatherIcon.setText(R.string.wi_day_rain_mix);
                break;
            case "10n":
                weatherIcon.setText(R.string.wi_night_rain_mix);
                break;
            case "11d":
                weatherIcon.setText(R.string.wi_day_rain);
                break;
            case "11n":
                weatherIcon.setText(R.string.wi_night_rain);
                break;
            case "13d":
                weatherIcon.setText(R.string.wi_day_snow);
                break;
            case "13n":
                weatherIcon.setText(R.string.wi_night_snow);
                break;
            case "50d":
                weatherIcon.setText(R.string.wi_day_fog);
                break;
            case "50n":
                weatherIcon.setText(R.string.wi_night_fog);
                break;
        }
    }

    @Override
    public void onRefresh() {
        swipeRefreshLayout.setRefreshing(true);
        getWeather();
        swipeRefreshLayout.setRefreshing(false);
    }
}
