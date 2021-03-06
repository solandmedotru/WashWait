package ru.solandme.washwait.ui;

import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.SwitchPreference;

import ru.solandme.washwait.R;

public class SettingsActivity extends PreferenceActivity {
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getFragmentManager().beginTransaction().replace(android.R.id.content, new MyPreferenceFragment()).commit();
    }

    public static class MyPreferenceFragment extends PreferenceFragment implements Preference.OnPreferenceChangeListener {
        @Override
        public void onCreate(final Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.my_preference_screen);

            bindPreferenceSummaryToValue(findPreference(getString(R.string.pref_units_key)));
            bindPreferenceSummaryToValue(findPreference(getString(R.string.pref_limit_key)));
            bindPreferenceSummaryToValue(findPreference(getString(R.string.pref_task_key)));
            bindPreferenceSummaryToValue(findPreference(getString(R.string.pref_forecast_providers_key)));
        }

        private void bindPreferenceSummaryToValue(Preference preference) {
            preference.setOnPreferenceChangeListener(this);

            if (preference instanceof ListPreference) {
                onPreferenceChange(preference,
                        PreferenceManager
                                .getDefaultSharedPreferences(preference.getContext())
                                .getString(preference.getKey(), ""));
            } else if (preference instanceof SwitchPreference) {
                onPreferenceChange(preference,
                        PreferenceManager
                                .getDefaultSharedPreferences(preference.getContext())
                                .getBoolean(preference.getKey(), false));
            }
        }

        @Override
        public boolean onPreferenceChange(Preference preference, Object value) {

            String stringValue = value.toString();

            if (preference instanceof ListPreference) {
                ListPreference listPreference = (ListPreference) preference;
                int prefIndex = listPreference.findIndexOfValue(stringValue);
                if (prefIndex >= 0) {
                    preference.setSummary(listPreference.getEntries()[prefIndex]);
                }
            } else if (preference instanceof SwitchPreference) {
                SwitchPreference swPref = (SwitchPreference) preference;
                boolean prefValue = swPref.isEnabled();
                preference.setEnabled(prefValue);
            } else {
                preference.setSummary(stringValue);
            }
            return true;
        }
    }
}