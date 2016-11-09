package ru.solandme.washwait;

import android.app.Dialog;
import android.content.DialogInterface;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;

public class AboutAppDialog extends DialogFragment implements
        DialogInterface.OnClickListener {
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        builder.setView(inflater.inflate(R.layout.activity_about_app_dialog, null))
                .setTitle(getString(R.string.app_name) + ", " + getString(R.string.app_version))
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