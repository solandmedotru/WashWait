package ru.solandme.washwait;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

import ru.solandme.washwait.data.Forecast;

class MyRecyclerViewAdapter extends RecyclerView.Adapter<MyRecyclerViewAdapter.ViewHolder> {

    private ArrayList<Forecast> forecasts;
    private WeatherFragment.OnForecastSelectedListener callback;

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView date1;
        ImageView image1;
        View container;

        ViewHolder(View v) {
            super(v);
            date1 = (TextView) v.findViewById(R.id.date);
            image1 = (ImageView) v.findViewById(R.id.weather_icon_day);
            container = v.findViewById(R.id.container_days_forecast);
        }
    }

    MyRecyclerViewAdapter(ArrayList<Forecast> forecasts, WeatherFragment.OnForecastSelectedListener callback) {
        this.forecasts = forecasts;
        this.callback = callback;
    }

    @Override
    public MyRecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_row, parent, false);

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("EE, dd", Locale.getDefault());
        holder.date1.setText(dateFormat.format(forecasts.get(position).getDate()));
        holder.image1.setImageResource(forecasts.get(position).getImageRes());

        holder.container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callback.onForecastItemSelected(holder.getAdapterPosition(), forecasts.get(0).getLat(), forecasts.get(0).getLon());
            }
        });
    }

    @Override
    public int getItemCount() {
        return forecasts.size();
    }



}