package ru.solandme.washwait.ui;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import ru.solandme.washwait.BuildConfig;
import ru.solandme.washwait.R;

public class AboutAppDialog extends DialogFragment implements
        DialogInterface.OnClickListener {
    TextView appName;
    TextView appVersion;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.activity_about_app_dialog, null);

        appName = (TextView) view.findViewById(R.id.aboutAppName);
        appVersion = (TextView) view.findViewById(R.id.aboutAppVersion);

        appName.setText(getString(R.string.app_name));
        appVersion.setText("Version " + BuildConfig.VERSION_NAME);

        builder.setView(view)
                .setPositiveButton(android.R.string.ok, this).create();
        return builder.create();
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