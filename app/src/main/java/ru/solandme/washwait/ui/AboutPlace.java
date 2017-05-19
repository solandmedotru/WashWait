package ru.solandme.washwait.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.text.Html;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import ru.solandme.washwait.R;
import ru.solandme.washwait.adapters.ReviewsAdapter;
import ru.solandme.washwait.network.map.model.places.Result;
import ru.solandme.washwait.utils.Utils;

public class AboutPlace extends AppCompatActivity implements View.OnClickListener {
    public static final String RESULT_KEY = "result";

    private String openHours = "";
    private String placePhotoAttributes = "";
    private String placeName;
    private String placePhone;
    private String placeAddress;
    private String photoRef;
    private String webUrl;
    private float placeRating;
    private Result result;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Utils.onActivityCreateSetTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.about_place_dialog);

        ImageView photoAboutPlace = (ImageView) findViewById(R.id.place_photo);
        RatingBar ratingBarAboutPlace = (RatingBar) findViewById(R.id.ratingBar);
        TextView ratingNumberAboutPlace = (TextView) findViewById(R.id.ratingNumber);
        TextView nameAboutPlace = (TextView) findViewById(R.id.place_name);
        TextView phoneAboutPlace = (TextView) findViewById(R.id.place_phone);
        TextView addressAboutPlace = (TextView) findViewById(R.id.place_address);
        TextView openHourAboutPlace = (TextView) findViewById(R.id.place_open_hours);
        TextView photoAttributes = (TextView) findViewById(R.id.photoAttributes);
        LinearLayout reviews_list = (LinearLayout) findViewById(R.id.reviews_list);
        ImageButton btnMakeCall = (ImageButton) findViewById(R.id.btnMakeCallPlace);
        btnMakeCall.setOnClickListener(this);
        ImageButton btnWebSite = (ImageButton) findViewById(R.id.btnWebSite);
        btnWebSite.setOnClickListener(this);
        ImageButton btnRoute = (ImageButton) findViewById(R.id.btnRoute);
        btnRoute.setOnClickListener(this);

        Bundle args = getIntent().getExtras();
        if (null != args) {
            result = new Gson().fromJson(args.getString(RESULT_KEY), Result.class);
            placeName = result.getName();
            placePhone = result.getInternationalPhoneNumber();
            placeAddress = result.getVicinity();
            placeRating = (float) result.getRating();

            if (null != result.getWebsite()) webUrl = result.getWebsite();

            if (null != result.getPhotos()) {
                for (String attr : result.getPhotos().get(0).getHtmlAttributions()) {
                    placePhotoAttributes = placePhotoAttributes + attr + "\n";
                }
                photoRef = result.getPhotos().get(0).getPhotoReference();
            }

            if (null != result.getOpeningHours()) {
                for (String openText : result.getOpeningHours().getWeekdayText()) {
                    openHours = openHours + openText + "\n";
                }
            }
        }

        if (null != result.getReviews()) {
            ReviewsAdapter reviewsAdapter = new ReviewsAdapter(this, R.layout.review_row, result.getReviews());
            for (int i = 0; i < reviewsAdapter.getCount(); i++) {
                View view = reviewsAdapter.getView(i, null, reviews_list);
                reviews_list.addView(view);
            }
        }

        if (null == openHours || openHours.equals("")) openHourAboutPlace.setVisibility(View.GONE);
        if (null == placeAddress || placeAddress.equals(""))
            addressAboutPlace.setVisibility(View.GONE);
        if (null == placePhotoAttributes || placePhotoAttributes.equals(""))
            photoAttributes.setVisibility(View.GONE);
        if (null == placePhone || placePhone.equals("") || placePhone.equals("null")) {
            phoneAboutPlace.setVisibility(View.GONE);
            btnMakeCall.setClickable(false);
        }
        if (null == webUrl || webUrl.equals("")) btnWebSite.setClickable(false);

        nameAboutPlace.setText(placeName);
        phoneAboutPlace.setText(placePhone);
        addressAboutPlace.setText(placeAddress);
        openHourAboutPlace.setText(openHours);
        ratingBarAboutPlace.setRating(placeRating);
        ratingNumberAboutPlace.setText(String.valueOf(placeRating));

        if (null != placePhotoAttributes && !placePhotoAttributes.equals("")) {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                photoAttributes.setText(Html.fromHtml(placePhotoAttributes, Html.FROM_HTML_MODE_LEGACY));
            } else {
                photoAttributes.setText(Html.fromHtml(placePhotoAttributes));
            }
        }

        Picasso.with(this).load(
                "https://maps.googleapis.com/maps/api/place/photo?maxwidth=1000&photoreference="
                        + photoRef + "&key="
                        + getResources().getString(R.string.google_maps_key))
                .placeholder(R.mipmap.city8)
                .into(photoAboutPlace);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnMakeCallPlace:
                if (isTelephonyEnabled()) {
                    Intent callIntent = new Intent(Intent.ACTION_DIAL);
                    callIntent.setData(Uri.parse("tel:" + placePhone.replaceAll("[^0-9|\\+]", "")));
                    startActivity(callIntent);
                }
                break;
            case R.id.btnWebSite:
                if (!webUrl.startsWith("http://") && !webUrl.startsWith("https://"))
                    webUrl = "http://" + webUrl;
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(webUrl));
                startActivity(browserIntent);
                break;
            case R.id.btnRoute:
                String lat = String.valueOf(result.getGeometry().getLocation().getLat());
                String lon = String.valueOf(result.getGeometry().getLocation().getLng());
                Uri gmmIntentUri = Uri.parse("google.navigation:q=" + lat + "," + lon + "&mode=d");
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                mapIntent.setPackage("com.google.android.apps.maps");
                startActivity(mapIntent);
                break;
        }
        finish();
    }

    private boolean isTelephonyEnabled() {
        TelephonyManager tm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        return tm != null && tm.getSimState() == TelephonyManager.SIM_STATE_READY;
    }
}
