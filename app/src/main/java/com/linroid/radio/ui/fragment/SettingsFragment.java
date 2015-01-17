package com.linroid.radio.ui.fragment;

import android.os.Bundle;
import android.preference.PreferenceFragment;

import com.linroid.radio.R;

/**
 * Created by linroid on 1/17/15.
 */
public class SettingsFragment extends PreferenceFragment {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.preferences);
    }
}