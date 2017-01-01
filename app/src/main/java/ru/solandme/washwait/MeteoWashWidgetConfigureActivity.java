package ru.solandme.washwait;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.ColorInt;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.enrico.colorpicker.colorDialog;

public class MeteoWashWidgetConfigureActivity extends AppCompatActivity
        implements colorDialog.ColorSelectedListener {

    int mAppWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;
    Context context;
    View textColorBox;
    View bgColorBox;
    SharedPreferences sharedPref;

    View.OnClickListener mOnClickListener = new View.OnClickListener() {
        public void onClick(View v) {
            // It is the responsibility of the configuration activity to update the app widget
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            MeteoWashWidget.updateAppWidget(context, appWidgetManager, mAppWidgetId);

            // Make sure we pass back the original appWidgetId
            Intent resultValue = new Intent();
            resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId);
            setResult(RESULT_OK, resultValue);
            finish();
        }
    };

    public MeteoWashWidgetConfigureActivity() {
        super();
    }


    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        context = MeteoWashWidgetConfigureActivity.this;
        sharedPref = PreferenceManager.getDefaultSharedPreferences(context);

        // Set the result to CANCELED.  This will cause the widget host to cancel
        // out of the widget placement if the user presses the back button.
        setResult(RESULT_CANCELED);

        setContentView(R.layout.meteo_wash_widget_configure);
        findViewById(R.id.add_button).setOnClickListener(mOnClickListener);

        textColorBox = findViewById(R.id.textColorBox);
        textColorBox.setBackgroundColor(sharedPref.getInt("pref_textColor_key", Color.GRAY));

        bgColorBox = findViewById(R.id.bgColorBox);
        bgColorBox.setBackgroundColor(sharedPref.getInt("pref_bgColorBox_key", Color.BLACK));

        textColorBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final MeteoWashWidgetConfigureActivity activity = MeteoWashWidgetConfigureActivity.this;
                colorDialog.showColorPicker(activity, 1);
            }
        });

        bgColorBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final MeteoWashWidgetConfigureActivity activity = MeteoWashWidgetConfigureActivity.this;
                colorDialog.showColorPicker(activity, 2);
            }
        });

        // Find the widget id from the intent.
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if (extras != null) {
            mAppWidgetId =
                    extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
        }

        // If this activity was started with an intent without an app widget ID, finish with an error.
        if (mAppWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            finish();
            return;
        }
    }

    @Override
    public void onColorSelection(DialogFragment dialogFragment, @ColorInt int selectedColor) {
        int tag;
        tag = Integer.valueOf(dialogFragment.getTag());
        switch (tag) {
            case 1:
                sharedPref.edit().putInt("pref_textColor_key", selectedColor).apply();
                textColorBox.setBackgroundColor(selectedColor);
                break;
            case 2:
                sharedPref.edit().putInt("pref_bgColor_key", selectedColor).apply();
                bgColorBox.setBackgroundColor(selectedColor);
                break;
            case 3:
        }
    }
}