package ru.solandme.washwait;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class AboutPlace extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "AboutPlaceDialog";

    TextView nameAboutPlace;
    TextView phoneAboutPlace;
    TextView addressAboutPlace;
    TextView descriptionAboutPlace;
    ImageView photoAboutPlace;
    ImageView ratingAboutPlace;

    private String openHours;
    private String placeName;
    private String placePhone;
    private String placeAddress;
    private String photoRef;
    private String webUrl;
    private float placeRating;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.about_place_dialog);

        Bundle args = getIntent().getExtras();
        openHours = args.getString("openHours");
        placeName = args.getString("name");
        placePhone = args.getString("phone");
        placeAddress = args.getString("address");
        photoRef = args.getString("photoRef");
        webUrl = args.getString("webUrl");
        placeRating = args.getFloat("rating");

        photoAboutPlace = (ImageView) findViewById(R.id.place_photo);
        ratingAboutPlace = (ImageView) findViewById(R.id.rating);
        nameAboutPlace = (TextView) findViewById(R.id.place_name);
        phoneAboutPlace = (TextView) findViewById(R.id.place_phone);
        addressAboutPlace = (TextView) findViewById(R.id.place_address);
        descriptionAboutPlace = (TextView) findViewById(R.id.place_description);

        Button btnCancel = (Button) findViewById(R.id.btnCancelPlace);
        btnCancel.setOnClickListener(this);
        Button btnMakeCall = (Button) findViewById(R.id.btnMakeCallPlace);
        btnMakeCall.setOnClickListener(this);

        nameAboutPlace.setText(placeName);
        phoneAboutPlace.setText(placePhone);
        if (phoneAboutPlace.getText().equals("") || phoneAboutPlace.getText().equals("null")) {
            btnMakeCall.setVisibility(View.GONE);
        }

        addressAboutPlace.setText(placeAddress);

        if (openHours == null) openHours = "";
        if (webUrl == null) webUrl = "";
        descriptionAboutPlace.setText(openHours + "\n" + webUrl);

        switch ((int) placeRating) {
            case 1:
                ratingAboutPlace.setImageResource(R.drawable.ic_rating1);
                break;
            case 2:
                ratingAboutPlace.setImageResource(R.drawable.ic_rating2);
                break;
            case 3:
                ratingAboutPlace.setImageResource(R.drawable.ic_rating3);
                break;
            case 4:
                ratingAboutPlace.setImageResource(R.drawable.ic_rating4);
                break;
            case 5:
                ratingAboutPlace.setImageResource(R.drawable.ic_rating5);
                break;
            default:
                ratingAboutPlace.setImageResource(R.drawable.ic_rating0);
                break;
        }

        Picasso.with(this).load(
                "https://maps.googleapis.com/maps/api/place/photo?maxwidth=1000&photoreference="
                        + photoRef + "&key="
                        + getResources().getString(R.string.google_maps_key))
                .placeholder(R.mipmap.city6).fit()
                .into(photoAboutPlace);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnCancelPlace:
                break;
            case R.id.btnMakeCallPlace:
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:" + placePhone.replaceAll("[^0-9|\\+]", "")));
                startActivity(intent);
                break;
        }
        finish();
    }
}
