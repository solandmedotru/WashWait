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

    public ReviewsAdapter(Context context, int res, List<Review> reviews) {
        super(context, res, reviews);
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
            convertView = ((Activity) getContext())
                    .getLayoutInflater()
                    .inflate(R.layout.review_row, parent, false);

            ViewHolder viewHolder = new ViewHolder();

            viewHolder.reviewer = (TextView) convertView.findViewById(R.id.reviewer);
            viewHolder.reviewText = (TextView) convertView.findViewById(R.id.reviewText);
            viewHolder.reviewNumber = (TextView) convertView.findViewById(R.id.reviewNumber);
            viewHolder.reviewData = (TextView) convertView.findViewById(R.id.reviewData);
            viewHolder.reviewBar = (RatingBar) convertView.findViewById(R.id.reviewBar);
            viewHolder.profilePhoto = (ImageView) convertView.findViewById(R.id.profilePhoto);
            convertView.setTag(viewHolder);

        }
        Review review = getItem(position);
        ViewHolder holder = (ViewHolder) convertView.getTag();
        if (review != null) {
            holder.reviewer.setText(review.getAuthorName());
            holder.reviewText.setText(review.getText());
            holder.reviewData.setText(review.getRelativeTimeDescription());
            holder.reviewNumber.setText("(" + String.valueOf(review.getRating()) + ")");
            holder.reviewBar.setRating(review.getRating());

            Picasso.with(getContext()).load(review.getProfilePhotoUrl())
                    .placeholder(R.drawable.round_shape)
                    .into(holder.profilePhoto);
        }
        return convertView;
    }
}
