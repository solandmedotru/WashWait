package ru.solandme.washwait.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import ru.solandme.washwait.R;
import ru.solandme.washwait.map.POJO.Result;


public class MyPlacesRVAdapter extends RecyclerView.Adapter<MyPlacesRVAdapter.ViewHolder> {

    private List<Result> results = new ArrayList<>();
    private OnPlaceSelectedListener listener;


    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView placeName;
        RatingBar placeRating;
        TextView placeVicinity;

        View placesContainer;


        ViewHolder(View v) {
            super(v);
            placeName = (TextView) v.findViewById(R.id.place_name);
            placeRating = (RatingBar) v.findViewById(R.id.ratingBar);
            placeVicinity = (TextView) v.findViewById(R.id.place_vicinity);

            placesContainer = v.findViewById(R.id.places_container);

        }
    }

    public MyPlacesRVAdapter(List<Result> results, OnPlaceSelectedListener listener) {
        this.results = results;
        this.listener = listener;
    }

    @Override
    public MyPlacesRVAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.place_row, parent, false);

        final MyPlacesRVAdapter.ViewHolder h = new MyPlacesRVAdapter.ViewHolder(v) {
        };
        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int adapterPosition = h.getAdapterPosition();
                if (adapterPosition != RecyclerView.NO_POSITION) {
                    listener.onPlaceItemSelected(adapterPosition, results.get(adapterPosition));
                }
            }
        });
        return h;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        holder.placeName.setText(results.get(position).getName());
        holder.placeRating.setRating((float) results.get(position).getRating());
        holder.placeVicinity.setText(results.get(position).getVicinity());

//        holder.placesContainer.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                listener.onPlaceItemSelected(holder.getAdapterPosition(), results.get(holder.getAdapterPosition()));
//            }
//        });
    }


    @Override
    public int getItemCount() {
        if (null == results) {
            return 0;
        } else {
            return results.size();
        }
    }


    public interface OnPlaceSelectedListener {
        void onPlaceItemSelected(int position, Result result);
    }

}