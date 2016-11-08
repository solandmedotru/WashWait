package ru.solandme.washwait;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Locale;
import ru.solandme.washwait.data.Forecast;

public class MyRecyclerViewAdapter extends RecyclerView.Adapter<MyRecyclerViewAdapter.ViewHolder> {

    private Forecast[] forecasts;


    // класс view holder-а с помощью которого мы получаем ссылку на каждый элемент
    // отдельного пункта списка
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // наш пункт состоит только из одного TextView
        public TextView date1;
        public ImageView image1;

        public ViewHolder(View v) {
            super(v);
            date1 = (TextView) v.findViewById(R.id.date);
            image1 = (ImageView) v.findViewById(R.id.weather_icon_day);
        }
    }

    // Конструктор
    public MyRecyclerViewAdapter(Forecast[] forecasts) {
        this.forecasts = forecasts;
    }

    // Создает новые views (вызывается layout manager-ом)
    @Override
    public MyRecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                               int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_row, parent, false);

        // тут можно программно менять атрибуты лэйаута (size, margins, paddings и др.)

        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    // Заменяет контент отдельного view (вызывается layout manager-ом)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("EE, dd", Locale.getDefault());

        holder.date1.setText(forecasts[position].getDate());
//        holder.image1.setImageResource(forecasts[position].getImageRes());

    }

    // Возвращает размер данных (вызывается layout manager-ом)
    @Override
    public int getItemCount() {
        return forecasts.length;
    }
}