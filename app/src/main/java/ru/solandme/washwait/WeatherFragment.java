package ru.solandme.washwait;


import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import ru.solandme.washwait.POJO.List;
import ru.solandme.washwait.POJO.WeatherResponse;
import ru.solandme.washwait.rest.ApiClient;
import ru.solandme.washwait.rest.ApiInterface;

/**
 * A simple {@link Fragment} subclass.
 */
public class WeatherFragment extends Fragment {

    private static final String TAG = "ru.solandme.washwait";
    Typeface weatherFont;

    TextView cityField;
    TextView updatedField;
    TextView detailsField;
    TextView currentTemperatureField;
    TextView weatherIcon;

    String lat = "35";
    String lon = "139";
    String cnt = "10";
    String appid = BuildConfig.OPEN_WEATHER_MAP_API_KEY;
    private String city = "428000";
    private String units = "metric";
    private String lang = Locale.getDefault().getLanguage();


    public WeatherFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        weatherFont = Typeface.createFromAsset(getActivity().getAssets(), "fonts/weather.ttf");
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_weather, container, false);
        cityField = (TextView) rootView.findViewById(R.id.city_field);
        updatedField = (TextView) rootView.findViewById(R.id.updated_field);
        detailsField = (TextView) rootView.findViewById(R.id.details_field);
        currentTemperatureField = (TextView) rootView.findViewById(R.id.current_temperature_field);
        weatherIcon = (TextView) rootView.findViewById(R.id.weather_icon);

        weatherIcon.setTypeface(weatherFont);

        getWeather();

        return rootView;
    }


    void getWeather() {
        final ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);

        Call<WeatherResponse> call = apiService.getWeatherByCityName(city, cnt, units, lang, appid);
        call.enqueue(new Callback<WeatherResponse>() {
            @Override
            public void onResponse(Call<WeatherResponse> call, Response<WeatherResponse> response) {
                Log.e(TAG, "onResponse: ");

                List list = response.body().getList().get(0);
                String cityName = response.body().getCity().getName();
                Long dt = (long) list.getDt();

                cityField.setText(cityName);

                detailsField.setText(
                        list.getWeather().get(0).getDescription().toUpperCase() +
                                "\n" + "Humidity: " + list.getHumidity() + "%" +
                                "\n" + "Pressure: " + list.getPressure() + " hPa");

                currentTemperatureField.setText(String.format("%.1f", list.getTemp().getDay()) + " ℃");

                SimpleDateFormat dateFormat = new SimpleDateFormat("EEEE dd MMM yyyy", Locale.getDefault());
                String updatedOn = dateFormat.format(new Date(dt * 1000));
                updatedField.setText("Last update: " + updatedOn);

                switch (response.body().getList().get(0).getWeather().get(0).getIcon()) {
                    case "01d":
                        weatherIcon.setText(R.string.wi_day_sunny);
                        break;
                    case "02d":
                        weatherIcon.setText(R.string.wi_cloudy_gusts);
                        break;
                    case "03d":
                        weatherIcon.setText(R.string.wi_cloud_down);
                        break;
                    case "10d":
                        weatherIcon.setText(R.string.wi_day_rain_mix);
                        break;
                    case "11d":
                        weatherIcon.setText(R.string.wi_day_thunderstorm);
                        break;
                    case "13d":
                        weatherIcon.setText(R.string.wi_day_snow);
                        break;
                    case "01n":
                        weatherIcon.setText(R.string.wi_night_clear);
                        break;
                    case "04d":
                        weatherIcon.setText(R.string.wi_cloudy);
                        break;
                    case "04n":
                        weatherIcon.setText(R.string.wi_night_cloudy);
                        break;
                    case "02n":
                        weatherIcon.setText(R.string.wi_night_cloudy);
                        break;
                    case "03n":
                        weatherIcon.setText(R.string.wi_night_cloudy_gusts);
                        break;
                    case "10n":
                        weatherIcon.setText(R.string.wi_night_cloudy_gusts);
                        break;
                    case "11n":
                        weatherIcon.setText(R.string.wi_night_rain);
                        break;
                    case "13n":
                        weatherIcon.setText(R.string.wi_night_snow);
                        break;
                }
            }

            @Override
            public void onFailure(Call<WeatherResponse> call, Throwable t) {
                Log.e(TAG, "onError: " + t);
            }
        });
    }

}
