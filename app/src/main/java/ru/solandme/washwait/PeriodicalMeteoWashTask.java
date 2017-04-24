package ru.solandme.washwait;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.android.gms.gcm.GcmNetworkManager;
import com.google.android.gms.gcm.GcmTaskService;
import com.google.android.gms.gcm.TaskParams;


public class PeriodicalMeteoWashTask extends GcmTaskService {
    private static final String TAG = PeriodicalMeteoWashTask.class.getSimpleName();

    boolean isForecastResultOK = false;

    @Override
    public int onRunTask(TaskParams taskParams) {

        Log.i(TAG, "onRunTask");

        LocalBroadcastManager.getInstance(this).registerReceiver(receiver, new IntentFilter(
                Constants.NOTIFICATION));

        MeteoWashService.startActionGetForecast(this, Constants.RUN_FROM_BACKGROUND);

        if (!isForecastResultOK) {
            return GcmNetworkManager.RESULT_FAILURE;
        } else {
            return GcmNetworkManager.RESULT_SUCCESS;
        }
    }

    BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            isForecastResultOK = intent.getBooleanExtra("isForecastResultOK", false);
        }
    };
}
