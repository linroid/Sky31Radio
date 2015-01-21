package com.linroid.radio.ui;

import android.os.Bundle;

import com.linroid.radio.R;
import com.linroid.radio.ui.base.BaseActivity;


public class SettingsActivity extends BaseActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    protected int provideContentViewId() {
        return R.layout.activity_setting;
    }
}