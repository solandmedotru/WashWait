package ru.solandme.washwait;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;

import java.util.List;
import ru.solandme.washwait.map.POJO.Result;


public class MyPlacesRVAdapter extends RecyclerView.Adapter<MyPlacesRVAdapter.ViewHolder>{

    private List<Result> results;
    private LatLng mCurrentLatLng;

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView placeName;
        TextView placeRating;
        TextView placeVicinity;
        TextView placeDistance;

        View placesContainer;


        ViewHolder(View v) {
            super(v);
            placeName = (TextView) v.findViewById(R.id.place_name);
            placeRating = (TextView) v.findViewById(R.id.place_rating);
            placeVicinity = (TextView) v.findViewById(R.id.place_vicinity);
            placeDistance = (TextView) v.findViewById(R.id.place_distance);

            placesContainer = v.findViewById(R.id.places_container);

        }
    }

    MyPlacesRVAdapter(List<Result> results, LatLng mCurrentLatLng) {
        this.results = results;
        this.mCurrentLatLng = mCurrentLatLng;
    }

    @Override
    public MyPlacesRVAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.place_row, parent, false);

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        holder.placeName.setText(results.get(position).getName());
        holder.placeRating.setText(String.valueOf(results.get(position).getRating()));
        holder.placeVicinity.setText(results.get(position).getVicinity());
        holder.placeDistance.setText(getDistance(position));

        holder.placesContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }

    private String getDistance(int position) {
        double lat = mCurrentLatLng.latitude;
        double lon = mCurrentLatLng.longitude;
        double lat1 = results.get(position).getGeometry().getLocation().getLat();
        double lon1 = results.get(position).getGeometry().getLocation().getLng();

        return String.valueOf(Math.sqrt((lat - lat1)*(lat - lat1) + (lon - lon1)+(lon - lon1)) + "km");

    }

    @Override
    public int getItemCount() {
        if(null == results){
            return 0;
        } else {
            return results.size();
        }
    }

}