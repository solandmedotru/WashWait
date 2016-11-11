package ru.solandme.washwait;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import ru.solandme.washwait.forecast.POJO.BigWeatherForecast;
import ru.solandme.washwait.data.Forecast;
import ru.solandme.washwait.data.WashHelper;
import ru.solandme.washwait.rest.ForecastApiHelper;
import ru.solandme.washwait.rest.ForecastApiService;

public class WeatherFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    private static final String TAG = "ru.solandme.washwait";
    private static final String TAG_ABOUT = "about";
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

    private ProgressBar dirtyMeter;

    private String lat = "35";
    private String lon = "139";
    private String cnt = "16";

    private String appid = BuildConfig.OPEN_WEATHER_MAP_API_KEY;

    private String defaultCityCode = "428000";
    private String defaultUnits = "metric";
    private String defaultLimit = "1";
    private int forecastDistance;

    private String lang = Locale.getDefault().getLanguage();
    private String cityCode;
    private String units;
    private SharedPreferences sharedPref;

    private RecyclerView recyclerView;
    private MyRecyclerViewAdapter adapter;

    WashHelper washHelper = new WashHelper();
    ArrayList<Forecast> forecasts = new ArrayList<>();
    OnForecastSelectedListener callback;


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

        carImage = (ImageView) rootView.findViewById(R.id.car_image);
        cityImage = (ImageView) rootView.findViewById(R.id.city_image);

        dirtyMeter = (ProgressBar) rootView.findViewById(R.id.precipitation_meter);

        recyclerView = (RecyclerView) rootView.findViewById(R.id.recycler);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), RecyclerView.HORIZONTAL, false));
        adapter = new MyRecyclerViewAdapter(forecasts, callback);
        recyclerView.setAdapter(adapter);

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
                break;
            case R.id.about_app_menu_item:
                new AboutAppDialog().show(getChildFragmentManager(), TAG_ABOUT);
                break;
        }
        return true;
    }


    @Override
    public void onResume() {
        getWeather();
        super.onResume();
    }

    void getWeather() {
        swipeRefreshLayout.setRefreshing(true);

        cityCode = sharedPref.getString("city", defaultCityCode);
        units = sharedPref.getString("units", defaultUnits);
        forecastDistance = Integer.parseInt(sharedPref.getString("limit", defaultLimit));

        final ForecastApiService apiService = ForecastApiHelper.getClient(getContext()).create(ForecastApiService.class);

        Call<BigWeatherForecast> weatherCall = apiService.getForecastByCityName(cityCode, units, lang, cnt, appid);
        weatherCall.enqueue(new Callback<BigWeatherForecast>() {
            @Override
            public void onResponse(Call<BigWeatherForecast> call, Response<BigWeatherForecast> response) {
                if (response.isSuccessful()) {

                    swipeRefreshLayout.setRefreshing(false);
                    washHelper.generateForecast(response.body(), forecasts, forecastDistance);
                    forecasts = washHelper.getForecasts();

                    adapter.notifyDataSetChanged();

                    updateWeatherUI();
                    updateWashForecastUI();
                }
            }

            @Override
            public void onFailure(Call<BigWeatherForecast> call, Throwable t) {

                swipeRefreshLayout.setRefreshing(false);

                Log.e(TAG, "onError: " + t);
                Toast.makeText(getActivity(), t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateWashForecastUI() {

        String forecastText = getTextForWashForecast(washHelper.getWashDayNumber(), washHelper.getDataToWashCar());
        forecastMessage.setText(forecastText);

        Double dirtyCounter = washHelper.getDirtyCounter()*10;
        dirtyMeter.setMax(40);
        dirtyMeter.setProgress(dirtyCounter.intValue());

        carImage.setImageResource(getCarPicture(dirtyCounter, forecasts.get(0).getTemperature()));
        Animation moveFromLeft = AnimationUtils.loadAnimation(getActivity(), R.anim.move_from_left);
        carImage.startAnimation(moveFromLeft);
        Animation moveFromRight = AnimationUtils.loadAnimation(getActivity(), R.anim.move_from_right);
        cityImage.startAnimation(moveFromRight);
    }

    private String getTextForWashForecast(int washDayNumber, long dataToWash) {
        String dateToWashFormated = new SimpleDateFormat("dd MMMM, EE", Locale.getDefault()).format(dataToWash);
        switch (washDayNumber) {
            case 0:
                return getResources().getString(R.string.can_wash);
            case 1:
                return getResources().getString(R.string.wash, dateToWashFormated.toUpperCase());
            case 2:
                return getResources().getString(R.string.wash, dateToWashFormated.toUpperCase());
            case 3:
                return getResources().getString(R.string.wash, dateToWashFormated.toUpperCase());
            case 4:
                return getResources().getString(R.string.wash, dateToWashFormated.toUpperCase());
            case 5:
                return getResources().getString(R.string.wash, dateToWashFormated.toUpperCase());
            case 6:
                return getResources().getString(R.string.wash, dateToWashFormated.toUpperCase());
            case 7:
                return getResources().getString(R.string.wash, dateToWashFormated.toUpperCase());
            case 8:
                return getResources().getString(R.string.wash, dateToWashFormated.toUpperCase());
            case 9:
                return getResources().getString(R.string.wash, dateToWashFormated.toUpperCase());
            case 10:
                return getResources().getString(R.string.wash, dateToWashFormated.toUpperCase());
            case 11:
                return getResources().getString(R.string.wash, dateToWashFormated.toUpperCase());
            case 12:
                return getResources().getString(R.string.wash, dateToWashFormated.toUpperCase());
            case 13:
                return getResources().getString(R.string.wash, dateToWashFormated.toUpperCase());
            case 14:
                return getResources().getString(R.string.wash, dateToWashFormated.toUpperCase());
            case 15:
                return getResources().getString(R.string.wash, dateToWashFormated.toUpperCase());
            default:
                return getResources().getString(R.string.not_wash);
        }
    }

    private void updateWeatherUI() {

        String cityName = forecasts.get(0).getCityName();
        String country = forecasts.get(0).getCountry();
        long dt = forecasts.get(0).getDate();
        double temp = forecasts.get(0).getTemperature();
        String description = forecasts.get(0).getDescription().toUpperCase();
        int icon = forecasts.get(0).getImageRes();

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

//        if(temp > -10) return R.mipmap.car1;
        if (dirtyCounter <= 0) return R.mipmap.car1;
        if (dirtyCounter > 0 && dirtyCounter < 2) return R.mipmap.car2;
        if (dirtyCounter >= 2 && dirtyCounter < 15) return R.mipmap.car3;
        if (dirtyCounter >= 15 && dirtyCounter < 50) return R.mipmap.car4;
        if (dirtyCounter >= 50) return R.mipmap.car5;

        return R.mipmap.car1;
    }

    @Override
    public void onRefresh() {
        getWeather();
    }

    public interface OnForecastSelectedListener {
        void onForecastItemSelected(int position, double lat, double lon);
    }

    @Override
    public void onAttach(Context activity) {
        super.onAttach(activity);
        try {
            callback = (OnForecastSelectedListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnForecastSelectedListener");
        }
    }
}
