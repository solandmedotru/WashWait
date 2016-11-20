package ru.solandme.washwait.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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
        ImageView placeRating;
        TextView placeVicinity;

        View placesContainer;


        ViewHolder(View v) {
            super(v);
            placeName = (TextView) v.findViewById(R.id.place_name);
            placeRating = (ImageView) v.findViewById(R.id.place_rating);
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

        switch ((int) results.get(position).getRating()) {
            case 1:
                holder.placeRating.setImageResource(R.drawable.ic_rating1);
                break;
            case 2:
                holder.placeRating.setImageResource(R.drawable.ic_rating2);
                break;
            case 3:
                holder.placeRating.setImageResource(R.drawable.ic_rating3);
                break;
            case 4:
                holder.placeRating.setImageResource(R.drawable.ic_rating4);
                break;
            case 5:
                holder.placeRating.setImageResource(R.drawable.ic_rating5);
                break;
            default:
                holder.placeRating.setImageResource(R.drawable.ic_rating0);
                break;
        }

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