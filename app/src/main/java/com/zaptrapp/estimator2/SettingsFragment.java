package com.zaptrapp.estimator2;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Map;

public class SettingsFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {

    SharedPreferences sharedPreferences;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    DatabaseReference gramRateReference;
    EditTextPreference gramRatePreference;
    String materialChoice = "gold";
    String TAG = SettingsFragment.class.getSimpleName();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.pref_main);
        sharedPreferences = getPreferenceManager().getSharedPreferences();
        initDatabase();

        gramRatePreference = (EditTextPreference) findPreference("gramRatePref");
        getMaterialChoice();
        EditTextPreference sgstRatePreference = (EditTextPreference) findPreference("sgstRatePref");
        sgstRatePreference.setSummary(sharedPreferences.getString("sgstRatePref", ""));

        EditTextPreference cgstRatePreference = (EditTextPreference) findPreference("cgstRatePref");
        cgstRatePreference.setSummary(sharedPreferences.getString("cgstRatePref", ""));

        EditTextPreference ipPreference = (EditTextPreference) findPreference("ipPref");
        ipPreference.setSummary(sharedPreferences.getString("ipPref", ""));

        gramRateFromFirebase();
    }

    private void gramRateFromFirebase() {
        gramRateReference = firebaseDatabase.getReference("estimator2/Gram Rate/" + materialChoice);

        gramRateReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String gramRate;
                gramRate = dataSnapshot.getValue(String.class);
                gramRatePreference.setSummary(gramRate);
                Log.d(TAG, "gramRate: " + gramRate);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    private void initDatabase() {
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("estimator2");
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
        gramRateFromFirebase();
    }

    @Override
    public void onPause() {
        sharedPreferences.unregisterOnSharedPreferenceChangeListener(this);
        super.onPause();
    }

    //Date: 22/12/2017
    //Getting the choice of the product and the gram rate set to enable firebase integration of the gram rate
    //for static gram rate across all connected devices
    private void getMaterialChoice() {
        String choice = sharedPreferences.getString("materialPref", "1");
        switch (choice) {
            case "1":
                materialChoice = "gold";
                break;
            case "2":
                materialChoice = "silver";
                break;
            default:
                materialChoice = "gold";
                break;

        }
        Log.d(TAG, "getMaterialChoice: " + materialChoice);
        Log.d(TAG, "getMaterialChoice: " + gramRatePreference.getText());
        //TODO Check this 23/12/2017
    }
    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        Log.d(TAG, "onSharedPreferenceChanged: ");
        for (int i = 0; i < getPreferenceScreen().getPreferenceCount(); i++) {
            Log.d(TAG, "onSharedPreferenceChanged: " + key);
            // and if it's an instance of EditTextPreference class, update its summary
            Preference preference = findPreference(key);
            if (preference instanceof EditTextPreference) {
                Log.d(TAG, "onSharedPreferenceChanged: Inside Loop");
                updateSummary((EditTextPreference) preference);
            } else if (preference instanceof ListPreference) {
                Log.d(TAG, "onSharedPreferenceChanged: Inside Loop");
                updateSummary((ListPreference) preference);

            }

        }


    }

    private void updateSummary(EditTextPreference preference) {
        // set the EditTextPreference's summary value to its current text
        Log.d(TAG, "updateSummary: edittextpreference " + preference.getText());
        preference.setSummary(preference.getText());
        if(preference ==gramRatePreference){
            Log.d(TAG, "onSharedPreferenceChanged: gramRatePreference");
            getMaterialChoice();
            databaseReference.child("Gram Rate").child(materialChoice).setValue(gramRatePreference.getText());
            gramRateFromFirebase();
        }

    }

    private void updateSummary(ListPreference preference) {
        // set the EditTextPreference's summary value to its current text
        Log.d(TAG, "updateSummary: listpreference " + preference.getEntry());
        preference.setSummary(preference.getEntry());
        gramRateFromFirebase();

    }

}

