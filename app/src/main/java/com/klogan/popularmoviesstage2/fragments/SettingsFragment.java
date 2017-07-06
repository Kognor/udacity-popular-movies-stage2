package com.klogan.popularmoviesstage2.fragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.util.Log;

import com.klogan.popularmoviesstage2.R;

/**
 * Fragment for User's Popular Movies settings.
 */
public class SettingsFragment extends PreferenceFragment
        implements Preference.OnPreferenceChangeListener {

    private static final String LOG_TAG = SettingsFragment.class.getSimpleName();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.pref_general);

        Preference preference = findPreference(getString(R.string.pref_sort_key));
        preference.setOnPreferenceChangeListener(this);

        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(preference.getContext());
        String newValue = sharedPreferences.getString(preference.getKey(), "");
        onPreferenceChange(preference, newValue);
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        String stringValue = newValue.toString();

        if (preference instanceof ListPreference) {
            ListPreference listPreference = (ListPreference)preference;
            int preferenceIndex = listPreference.findIndexOfValue(stringValue);
            if (preferenceIndex >= 0) {
                CharSequence[] listPreferenceEntries = listPreference.getEntries();
                preference.setSummary(listPreferenceEntries[preferenceIndex]);
            } else {
                Log.e(LOG_TAG, "Unknown preference.");
            }
        } else {
            preference.setSummary(stringValue);
        }
        return true;
    }

}
