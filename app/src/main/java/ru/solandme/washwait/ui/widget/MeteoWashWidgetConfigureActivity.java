package ru.solandme.washwait.ui.widget;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.enrico.colorpicker.colorDialog;

import ru.solandme.washwait.R;
import ru.solandme.washwait.utils.SharedPrefsUtils;

public class MeteoWashWidgetConfigureActivity extends AppCompatActivity
        implements colorDialog.ColorSelectedListener {

    public static final int TAG_TEXT_COLOR = 1;
    public static final int TAG_BG_COLOR = 2;
    int mAppWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;
    Context context;
    View textColorBox;
    View bgColorBox;

    View.OnClickListener mOnClickListener = new View.OnClickListener() {
        public void onClick(View v) {
            // It is the responsibility of the configuration activity to load the app widget
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

        // Set the result to CANCELED.  This will cause the widget host to cancel
        // out of the widget placement if the user presses the back button.
        setResult(RESULT_CANCELED);

        setContentView(R.layout.meteo_wash_widget_configure);
        findViewById(R.id.add_button).setOnClickListener(mOnClickListener);

        textColorBox = findViewById(R.id.textColorBox);
        textColorBox.setBackgroundColor(SharedPrefsUtils.getIntegerPreference(this, getString(R.string.pref_textColor_key), Color.GRAY));

        bgColorBox = findViewById(R.id.bgColorBox);
        bgColorBox.setBackgroundColor(SharedPrefsUtils.getIntegerPreference(this, getString(R.string.pref_bgColor_key), Color.BLACK));

        textColorBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final MeteoWashWidgetConfigureActivity activity = MeteoWashWidgetConfigureActivity.this;
                colorDialog.showColorPicker(activity, TAG_TEXT_COLOR);
            }
        });

        bgColorBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final MeteoWashWidgetConfigureActivity activity = MeteoWashWidgetConfigureActivity.this;
                colorDialog.showColorPicker(activity, TAG_BG_COLOR);
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
        }
    }

    @Override
    public void onColorSelection(DialogFragment dialogFragment, @ColorInt int selectedColor) {
        int tag;
        tag = Integer.valueOf(dialogFragment.getTag());
        switch (tag) {
            case TAG_TEXT_COLOR:
                SharedPrefsUtils.setIntegerPreference(this, getString(R.string.pref_textColor_key), selectedColor);
                textColorBox.setBackgroundColor(selectedColor);
                break;
            case TAG_BG_COLOR:
                SharedPrefsUtils.setIntegerPreference(this, getString(R.string.pref_bgColor_key), selectedColor);
                bgColorBox.setBackgroundColor(selectedColor);
                break;
        }
    }
}