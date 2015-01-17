package com.linroid.radio.ui;

import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.MenuItem;

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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                NavUtils.navigateUpTo(this, NavUtils.getParentActivityIntent(this));
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}