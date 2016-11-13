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

class MyForecastRVAdapter extends RecyclerView.Adapter<MyForecastRVAdapter.ViewHolder> {

    private ArrayList<Forecast> forecasts;

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView date1;
        ImageView image1;

        ViewHolder(View v) {
            super(v);
            date1 = (TextView) v.findViewById(R.id.date);
            image1 = (ImageView) v.findViewById(R.id.weather_icon_day);
        }
    }

    MyForecastRVAdapter(ArrayList<Forecast> forecasts) {
        this.forecasts = forecasts;
    }

    @Override
    public MyForecastRVAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.forecast_row, parent, false);

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("EE, dd", Locale.getDefault());
        holder.date1.setText(dateFormat.format(forecasts.get(position).getDate()));
        holder.image1.setImageResource(forecasts.get(position).getImageRes());
    }

    @Override
    public int getItemCount() {
        return forecasts.size();
    }
}