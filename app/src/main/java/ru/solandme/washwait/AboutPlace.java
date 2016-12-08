package ru.solandme.washwait;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.Spanned;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import ru.solandme.washwait.utils.Utils;

public class AboutPlace extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "AboutPlaceDialog";
    public static final String OPEN_HOURS_KEY = "openHours";
    public static final String PLACE_NAME_KEY = "name";
    public static final String PHONE_KEY = "phone";
    public static final String ADDRESS_KEY = "address";
    public static final String PHOTO_REF_KEY = "photoRef";
    public static final String WEB_URL_KEY = "webUrl";
    public static final String RATING_KEY = "rating";
    public static final String PHOTO_ATTRIBUTES_KEY = "photoAttributions";

    TextView nameAboutPlace;
    TextView phoneAboutPlace;
    TextView addressAboutPlace;
    TextView openHourAboutPlace;
    ImageView photoAboutPlace;
    RatingBar ratingBarAboutPlace;

    private String openHours;
    private String placeName;
    private String placePhone;
    private String placeAddress;
    private String photoRef;
    private String webUrl;
    private float placeRating;
    private TextView ratingNumberAboutPlace;
    private TextView photoAttributes;
    private String placePhotoAttributes;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Utils.onActivityCreateSetTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.about_place_dialog);

        Bundle args = getIntent().getExtras();
        openHours = args.getString(OPEN_HOURS_KEY);
        placeName = args.getString(PLACE_NAME_KEY);
        placePhone = args.getString(PHONE_KEY);
        placeAddress = args.getString(ADDRESS_KEY);
        photoRef = args.getString(PHOTO_REF_KEY);
        webUrl = args.getString(WEB_URL_KEY);
        placeRating = args.getFloat(RATING_KEY);
        placePhotoAttributes = args.getString(PHOTO_ATTRIBUTES_KEY);

        photoAboutPlace = (ImageView) findViewById(R.id.place_photo);
        ratingBarAboutPlace = (RatingBar) findViewById(R.id.ratingBar);
        ratingNumberAboutPlace = (TextView) findViewById(R.id.ratingNumber);
        nameAboutPlace = (TextView) findViewById(R.id.place_name);
        phoneAboutPlace = (TextView) findViewById(R.id.place_phone);
        addressAboutPlace = (TextView) findViewById(R.id.place_address);
        openHourAboutPlace = (TextView) findViewById(R.id.place_open_hours);
        photoAttributes = (TextView) findViewById(R.id.photoAttributes);


        ImageButton btnMakeCall = (ImageButton) findViewById(R.id.btnMakeCallPlace);
        btnMakeCall.setOnClickListener(this);
        ImageButton btnWebSite = (ImageButton) findViewById(R.id.btnWebSite);
        btnWebSite.setOnClickListener(this);


        if (null == openHours) openHourAboutPlace.setVisibility(View.GONE);
        if (null == placeAddress) addressAboutPlace.setVisibility(View.GONE);
        if (null == placePhotoAttributes || placePhotoAttributes.equals(""))
            photoAttributes.setVisibility(View.GONE);
        if (null == placePhone || placePhone.equals("") || placePhone.equals("null")) {
            phoneAboutPlace.setVisibility(View.GONE);
            btnMakeCall.setClickable(false);
        }
        if (null == webUrl) btnWebSite.setClickable(false);

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
                .placeholder(R.mipmap.city8).fit()
                .into(photoAboutPlace);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnMakeCallPlace:
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:" + placePhone.replaceAll("[^0-9|\\+]", "")));
                startActivity(intent);
                break;
            case R.id.btnWebSite:
                if (!webUrl.startsWith("http://") && !webUrl.startsWith("https://"))
                    webUrl = "http://" + webUrl;
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(webUrl));
                startActivity(browserIntent);
                break;
        }
        finish();
    }
}
