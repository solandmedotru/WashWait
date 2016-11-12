package ru.solandme.washwait;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.places.PlacePhotoMetadataBuffer;
import com.google.android.gms.location.places.PlacePhotoMetadataResult;
import com.google.android.gms.location.places.PlacePhotoResult;
import com.google.android.gms.location.places.Places;
import com.squareup.picasso.Picasso;

public class AboutPlace extends DialogFragment implements View.OnClickListener {
    private static final String TAG = "AboutPlaceDialog";
    TextView placeName;
    TextView placePhoneNumber;
    TextView placeAddress;
    TextView placeDescription;
    ImageView placePhoto;
    ImageView placeRating;

    private GoogleApiClient mGoogleApiClient;

    Bundle args;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.PlaceDialog);
        args = getArguments();

        mGoogleApiClient = new GoogleApiClient
                .Builder(getActivity())
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .build();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View layout = inflater.inflate(R.layout.about_place_dialog, container, false);

        layout.findViewById(R.id.btnCancelPlace).setOnClickListener(this);
        layout.findViewById(R.id.btnMakeCallPlace).setOnClickListener(this);


        placePhoto = (ImageView) layout.findViewById(R.id.place_photo);
        placeRating = (ImageView) layout.findViewById(R.id.rating);

        placeName = (TextView) layout.findViewById(R.id.place_name);
        placePhoneNumber = (TextView) layout.findViewById(R.id.place_phone);
        placeAddress = (TextView) layout.findViewById(R.id.place_address);
        placeDescription = (TextView) layout.findViewById(R.id.place_description);


        placeName.setText(args.getString("name"));
        placePhoneNumber.setText(args.getString("phone"));
        placeAddress.setText(args.getString("address"));
        placeDescription.setText(args.getString("openHours") + "\n" + args.getString("webUrl"));

        if (args.getFloat("rating") == 0) {
            placeRating.setImageResource(R.drawable.ic_star);
        }

//        placePhotosAsync();

        Picasso.with(getContext()).load(
                "https://maps.googleapis.com/maps/api/place/photo?maxwidth=2000&photoreference=" +
                        args.getString("photoRef") + "&key=AIzaSyDVkzWmncsH-7tkaIYl0SlRMPmL0NAkjOc"
        ).placeholder(R.mipmap.city)
                .into(placePhoto);

        return layout;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnCancelPlace:
                break;
            case R.id.btnMakeCallPlace:
                Toast.makeText(getActivity(), "MACKING CALL to" + args.getString("phone"), Toast.LENGTH_LONG).show();
                break;
        }
        dismiss();
    }

    private ResultCallback<PlacePhotoResult> mDisplayPhotoResultCallback
            = new ResultCallback<PlacePhotoResult>() {
        @Override
        public void onResult(PlacePhotoResult placePhotoResult) {
            if (!placePhotoResult.getStatus().isSuccess()) {
                return;
            }
            Log.e(TAG, "onResult: " + placePhotoResult.toString());
            placePhoto.setImageBitmap(placePhotoResult.getBitmap());
        }
    };

    /**
     * Load a bitmap from the photos API asynchronously
     * by using buffers and result callbacks.
     */
    private void placePhotosAsync() {
        String placeId = args.getString("placeId");

        Log.e(TAG, "placePhotosAsync: " + args.getString("placeId"));
        Places.GeoDataApi.getPlacePhotos(mGoogleApiClient, placeId)
                .setResultCallback(new ResultCallback<PlacePhotoMetadataResult>() {
                    @Override
                    public void onResult(PlacePhotoMetadataResult photos) {
                        if (!photos.getStatus().isSuccess()) {
                            Log.e(TAG, "onResult Error ");
                            return;
                        }


                        PlacePhotoMetadataBuffer photoMetadataBuffer = photos.getPhotoMetadata();
                        if (photoMetadataBuffer.getCount() > 0) {
                            Log.e(TAG, "onResult OK ");
                            photoMetadataBuffer.get(0)
                                    .getScaledPhoto(mGoogleApiClient, placePhoto.getWidth(),
                                            placePhoto.getHeight())
                                    .setResultCallback(mDisplayPhotoResultCallback);
                        }
                        photoMetadataBuffer.release();
                    }
                });
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
