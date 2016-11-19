package ru.solandme.washwait;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.Places;
import com.squareup.picasso.Picasso;

public class AboutPlace extends DialogFragment implements View.OnClickListener {
    private static final String TAG = "AboutPlaceDialog";
    private GoogleApiClient mGoogleApiClient;
    Bundle args;

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
        setStyle(DialogFragment.STYLE_NORMAL, R.style.PlaceDialog);

        args = getArguments();
        openHours = args.getString("openHours");
        placeName = args.getString("name");
        placePhone = args.getString("phone");
        placeAddress = args.getString("address");
        photoRef = args.getString("photoRef");
        webUrl = args.getString("webUrl");
        placeRating = args.getFloat("rating");

        mGoogleApiClient = new GoogleApiClient
                .Builder(getActivity())
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .build();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View layout = inflater.inflate(R.layout.about_place_dialog, container, false);

        photoAboutPlace = (ImageView) layout.findViewById(R.id.place_photo);
        ratingAboutPlace = (ImageView) layout.findViewById(R.id.rating);
        nameAboutPlace = (TextView) layout.findViewById(R.id.place_name);
        phoneAboutPlace = (TextView) layout.findViewById(R.id.place_phone);
        addressAboutPlace = (TextView) layout.findViewById(R.id.place_address);
        descriptionAboutPlace = (TextView) layout.findViewById(R.id.place_description);

        Button btnCancel = (Button) layout.findViewById(R.id.btnCancelPlace);
        btnCancel.setOnClickListener(this);
        Button btnMakeCall = (Button) layout.findViewById(R.id.btnMakeCallPlace);
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
                ratingAboutPlace.setImageResource(R.mipmap.rating1);
                break;
            case 2:
                ratingAboutPlace.setImageResource(R.mipmap.rating2);
                break;
            case 3:
                ratingAboutPlace.setImageResource(R.mipmap.rating3);
                break;
            case 4:
                ratingAboutPlace.setImageResource(R.mipmap.rating4);
                break;
            case 5:
                ratingAboutPlace.setImageResource(R.mipmap.medal);
                break;
            default:
                ratingAboutPlace.setImageResource(R.mipmap.rating0);
                break;
        }

        Picasso.with(getContext()).load(
                "https://maps.googleapis.com/maps/api/place/photo?maxwidth=1000&photoreference="
                        + photoRef + "&key="
                        + getResources().getString(R.string.google_maps_key))
                .placeholder(R.mipmap.city3).fit()
                .into(photoAboutPlace);

        return layout;
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
        dismiss();
    }

    @Override
    public void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    public void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }
}
