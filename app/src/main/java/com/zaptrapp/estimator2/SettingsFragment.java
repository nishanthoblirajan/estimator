package com.zaptrapp.estimator2;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.support.annotation.Nullable;
import android.util.Log;

import java.util.Map;

public class SettingsFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {
    SharedPreferences sharedPreferences;



    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.pref_main);
        sharedPreferences = getPreferenceManager().getSharedPreferences();

        EditTextPreference gramRatePreference = (EditTextPreference) findPreference("gramRatePref");
        gramRatePreference.setSummary(sharedPreferences.getString("gramRatePref",""));

        EditTextPreference sgstRatePreference = (EditTextPreference) findPreference("sgstRatePref");
        sgstRatePreference.setSummary(sharedPreferences.getString("sgstRatePref",""));

        EditTextPreference cgstRatePreference = (EditTextPreference) findPreference("cgstRatePref");
        cgstRatePreference.setSummary(sharedPreferences.getString("cgstRatePref",""));


        EditTextPreference ipPreference = (EditTextPreference) findPreference("ipPref");
        ipPreference.setSummary(sharedPreferences.getString("ipPref",""));


    }


    @Override
    public void onResume() {
        super.onResume();

        sharedPreferences = getPreferenceManager().getSharedPreferences();

        // we want to watch the preference values' changes
        sharedPreferences.registerOnSharedPreferenceChangeListener(this);

        Map<String, ?> preferencesMap = sharedPreferences.getAll();
        // iterate through the preference entries and update their summary if they are an instance of EditTextPreference
        for (Map.Entry<String, ?> preferenceEntry : preferencesMap.entrySet()) {
            if (preferenceEntry instanceof EditTextPreference) {
                updateSummary((EditTextPreference) preferenceEntry);
            }
        }
    }

    @Override
    public void onPause() {
        sharedPreferences.unregisterOnSharedPreferenceChangeListener(this);
        super.onPause();
    }
        String TAG = SettingsFragment.class.getSimpleName();

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        Log.d(TAG, "onSharedPreferenceChanged: ");
        for(int i = 0; i < getPreferenceScreen().getPreferenceCount(); i++) {
            Log.d(TAG, "onSharedPreferenceChanged: " + key);
            // and if it's an instance of EditTextPreference class, update its summary
            Preference preference = findPreference(key);
            if (preference instanceof EditTextPreference) {
                Log.d(TAG, "onSharedPreferenceChanged: Inside Loop");
                updateSummary((EditTextPreference) preference);
            }else if (preference instanceof ListPreference) {
                Log.d(TAG, "onSharedPreferenceChanged: Inside Loop");
                updateSummary((ListPreference) preference);
            }
        }
    }

    private void updateSummary(EditTextPreference preference) {
        // set the EditTextPreference's summary value to its current text
        Log.d(TAG, "updateSummary: edittextpreference "+preference.getText());
        preference.setSummary(preference.getText());

    }

    private void updateSummary(ListPreference preference) {
        // set the EditTextPreference's summary value to its current text
        Log.d(TAG, "updateSummary: listpreference "+preference.getEntry());
        preference.setSummary(preference.getEntry());

    }
}
