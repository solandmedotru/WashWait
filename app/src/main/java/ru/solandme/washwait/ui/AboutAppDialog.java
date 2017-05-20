package ru.solandme.washwait.ui;

import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import ru.solandme.washwait.BuildConfig;
import ru.solandme.washwait.R;

public class AboutAppDialog extends DialogFragment implements
        DialogInterface.OnClickListener {
    private TextView appName;
    private TextView appVersion;
    private TextView twitter;
    private TextView facebook;
    private TextView instagram;
    private TextView support;
    private TextView review;
    private TextView share;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.activity_about_app_dialog, null);

        appName = (TextView) view.findViewById(R.id.aboutAppName);
        appVersion = (TextView) view.findViewById(R.id.aboutAppVersion);
        twitter = (TextView) view.findViewById(R.id.aboutTwitter);
        facebook = (TextView) view.findViewById(R.id.aboutFacebook);
        instagram = (TextView) view.findViewById(R.id.aboutInstagram);
        support = (TextView) view.findViewById(R.id.aboutSupport);
        review = (TextView) view.findViewById(R.id.aboutReview);
        share = (TextView) view.findViewById(R.id.aboutShare);

        appName.setText(getString(R.string.app_name));
        appVersion.setText("Version " + BuildConfig.VERSION_NAME);

        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openShare();
            }
        });

        review.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openReview();
            }
        });

        support.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openEmail();
            }
        });

        twitter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openTwitter();
            }
        });

        facebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFacebook();
            }
        });

        instagram.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openInstagram();
            }
        });

        builder.setView(view)
                .setPositiveButton(android.R.string.ok, this).create();
        return builder.create();
    }

    private void openShare() {
        try {
            int applicationNameId = getActivity().getApplicationInfo().labelRes;
            final String appPackageName = getActivity().getPackageName();
            Intent i = new Intent(Intent.ACTION_SEND);
            i.setType("text/plain");
            i.putExtra(Intent.EXTRA_SUBJECT, getString(applicationNameId));
            String text = getString(R.string.recommendText);
            String link = "https://play.google.com/store/apps/details?id=" + appPackageName;
            i.putExtra(Intent.EXTRA_TEXT, text + " " + link);
            startActivity(Intent.createChooser(i, getString(R.string.share_link_text)));
        } catch (Exception e) {
            Toast.makeText(getActivity(), "Sorry. I can not do it.", Toast.LENGTH_SHORT).show();
        }
    }

    private void openReview() {
        Uri uri = Uri.parse("market://details?id=" + getActivity().getPackageName());
        Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
        goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY |
                Intent.FLAG_ACTIVITY_NEW_DOCUMENT |
                Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
        try {
            startActivity(goToMarket);
        } catch (ActivityNotFoundException e) {
            startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("http://play.google.com/store/apps/details?id=" + getActivity().getPackageName())));
        }
    }

    private void openEmail() {
        Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:" + "solomin.andrey@gmail.com"));
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "To MeteoWash support");
        emailIntent.putExtra(Intent.EXTRA_TEXT, "Body");
        startActivity(Intent.createChooser(emailIntent, "Send email..."));
    }

    private void openInstagram() {
        Intent intent;
        try {
            getActivity().getPackageManager().getPackageInfo("com.instagram.android", 0);
            intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://instagram.com/_u/devsoland"));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        } catch (Exception e) {
            intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://instagram.com/devsoland"));
        }
        startActivity(intent);
    }

    private void openFacebook() {
        Intent intent;
        try {
            getActivity().getPackageManager().getPackageInfo("com.facebook.katana", 0);
            intent = new Intent(Intent.ACTION_VIEW, Uri.parse("fb://profile/solomin.andrey"));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        } catch (Exception e) {
            intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.facebook.com/solomin.andrey"));
        }
        startActivity(intent);
    }

    private void openTwitter() {
        Intent intent;
        try {
            getActivity().getPackageManager().getPackageInfo("com.twitter.android", 0);
            intent = new Intent(Intent.ACTION_VIEW, Uri.parse("twitter://user?screen_name=solomin_andrey"));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        } catch (Exception e) {
            intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://twitter.com/solomin_andrey"));
        }
        startActivity(intent);
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
    }

    @Override
    public void onDismiss(DialogInterface unused) {
        super.onDismiss(unused);
    }

    @Override
    public void onCancel(DialogInterface unused) {
        super.onCancel(unused);
    }
}