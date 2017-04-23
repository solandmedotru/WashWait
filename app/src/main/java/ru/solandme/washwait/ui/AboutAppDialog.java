package ru.solandme.washwait.ui;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;

import ru.solandme.washwait.BuildConfig;
import ru.solandme.washwait.R;

public class AboutAppDialog extends DialogFragment implements
        DialogInterface.OnClickListener {
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        builder.setView(inflater.inflate(R.layout.activity_about_app_dialog, null))
                .setTitle(getString(R.string.app_name) + ", v" + BuildConfig.VERSION_NAME)
                .setIcon(R.mipmap.ic_launcher)
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