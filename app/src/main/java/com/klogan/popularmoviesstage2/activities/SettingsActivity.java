package com.klogan.popularmoviesstage2.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.support.annotation.Nullable;

import com.klogan.popularmoviesstage2.fragments.SettingsFragment;

/**
 * Activity for User's Popular Movie settings.
 */
public class SettingsActivity extends PreferenceActivity {

    public static Intent getIntent(Context context) {
        return new Intent(context, SettingsActivity.class);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(android.R.id.content, new SettingsFragment())
                    .commit();
        }
    }
}
