package ru.solandme.washwait.adapters;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RatingBar;
import android.widget.TextView;

import java.util.List;

import ru.solandme.washwait.R;
import ru.solandme.washwait.model.pojo.places.Review;

public class ReviewsAdapter extends ArrayAdapter<Review> {

    private final List<Review> reviews;
    private final Activity context;

    public ReviewsAdapter(Activity context, List<Review> reviews) {
        super(context, R.layout.review_row, reviews);
        this.context = context;
        this.reviews = reviews;
    }

    private static class ViewHolder {
        TextView reviewer, reviewText, reviewNumber, reviewData;
        RatingBar reviewBar;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        View view;
        if (convertView == null) {
            LayoutInflater inflater = context.getLayoutInflater();
            view = inflater.inflate(R.layout.review_row, null);
            final ViewHolder viewHolder = new ViewHolder();

            viewHolder.reviewer = (TextView) view.findViewById(R.id.reviewer);
            viewHolder.reviewText = (TextView) view.findViewById(R.id.reviewText);
            viewHolder.reviewNumber = (TextView) view.findViewById(R.id.reviewNumber);
            viewHolder.reviewData = (TextView) view.findViewById(R.id.reviewData);
            viewHolder.reviewBar = (RatingBar) view.findViewById(R.id.reviewBar);
            view.setTag(viewHolder);

        } else {
            view = convertView;
        }
        ViewHolder holder = (ViewHolder) view.getTag();
        holder.reviewer.setText(reviews.get(position).getAuthorName());
        holder.reviewText.setText(reviews.get(position).getText());
        holder.reviewData.setText(reviews.get(position).getRelativeTimeDescription());
        holder.reviewNumber.setText("("+String.valueOf(reviews.get(position).getRating())+")");
        holder.reviewBar.setRating(reviews.get(position).getRating());
        return view;
    }
}
