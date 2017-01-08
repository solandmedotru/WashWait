package ru.solandme.washwait.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Locale;

import ru.solandme.washwait.POJO.forecast.WeatherForecast;
import ru.solandme.washwait.R;

public class MyForecastRVAdapter extends RecyclerView.Adapter<MyForecastRVAdapter.ViewHolder> {

    private OnItemClickListener listener;

    // Define the listener interface
    public interface OnItemClickListener {
        void onItemClick(View itemView, int position);
    }

    // Define the method that allows the parent activity or fragment to define the listener
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }
    private WeatherForecast weatherForecast;


    class ViewHolder extends RecyclerView.ViewHolder {
        TextView date1, temp;
        ImageView image1;

        ViewHolder(View v) {
            super(v);
            date1 = (TextView) v.findViewById(R.id.date);
            image1 = (ImageView) v.findViewById(R.id.weather_icon_day);
            temp = (TextView) v.findViewById(R.id.smallTempText);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Triggers click upwards to the adapter on click
                    if (listener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            listener.onItemClick(itemView, position);
                        }
                    }
                }
            });
        }
    }

    public MyForecastRVAdapter(WeatherForecast weatherForecast) {
        this.weatherForecast = weatherForecast;
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
        holder.date1.setText(dateFormat.format(weatherForecast.getList().get(position).getDt() * 1000));
        holder.image1.setImageResource(weatherForecast.getList().get(position).getImageRes());
        holder.temp.setText(getMiddleTemp(position));

    }

    private String getMiddleTemp(int position) {
        double max = weatherForecast.getList().get(position).getTemp().getMax();
        double min = weatherForecast.getList().get(position).getTemp().getMin();
        return String.valueOf((int)(max + min)/2);
    }

    @Override
    public int getItemCount() {
        if (null == weatherForecast) return 0;

        return weatherForecast.getList().size();
    }
}