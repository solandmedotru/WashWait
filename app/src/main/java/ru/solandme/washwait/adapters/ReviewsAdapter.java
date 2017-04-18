package ru.solandme.washwait.adapters;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import ru.solandme.washwait.R;
import ru.solandme.washwait.model.pojo.places.Review;

public class ReviewsAdapter extends ArrayAdapter<Review> {

    private final List<Review> reviews;

    public ReviewsAdapter(Context context, int res, List<Review> reviews) {
        super(context, res, reviews);
        this.reviews = reviews;
    }

    private static class ViewHolder {
        TextView reviewer, reviewText, reviewNumber, reviewData;
        RatingBar reviewBar;
        ImageView profilePhoto;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = ((Activity)getContext()).getLayoutInflater().inflate(R.layout.review_row, parent, false);
            final ViewHolder viewHolder = new ViewHolder();

            viewHolder.reviewer = (TextView) convertView.findViewById(R.id.reviewer);
            viewHolder.reviewText = (TextView) convertView.findViewById(R.id.reviewText);
            viewHolder.reviewNumber = (TextView) convertView.findViewById(R.id.reviewNumber);
            viewHolder.reviewData = (TextView) convertView.findViewById(R.id.reviewData);
            viewHolder.reviewBar = (RatingBar) convertView.findViewById(R.id.reviewBar);
            viewHolder.profilePhoto = (ImageView) convertView.findViewById(R.id.profilePhoto);
            convertView.setTag(viewHolder);

        }

        ViewHolder holder = (ViewHolder) convertView.getTag();
        holder.reviewer.setText(reviews.get(position).getAuthorName());
        holder.reviewText.setText(reviews.get(position).getText());
        holder.reviewData.setText(reviews.get(position).getRelativeTimeDescription());
        holder.reviewNumber.setText("("+String.valueOf(reviews.get(position).getRating())+")");
        holder.reviewBar.setRating(reviews.get(position).getRating());

        Picasso.with(getContext()).load(reviews.get(position).getProfilePhotoUrl())
                .placeholder(R.drawable.round_shape)
                .into(holder.profilePhoto);

        return convertView;
    }
}
