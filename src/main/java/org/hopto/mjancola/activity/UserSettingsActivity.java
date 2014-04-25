package org.hopto.mjancola.activity;

import android.os.Bundle;
import android.preference.PreferenceActivity;
import org.hopto.mjancola.R;

public class UserSettingsActivity extends PreferenceActivity
{

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource( R.xml.settings);

    }
}