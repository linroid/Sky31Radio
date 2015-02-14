package com.linroid.sky31radio.ui;

import com.linroid.sky31radio.R;
import com.linroid.sky31radio.module.SettingModule;
import com.linroid.sky31radio.ui.base.BaseActivity;
import com.linroid.sky31radio.ui.base.InjectableActivity;

import java.util.Arrays;
import java.util.List;


public class SettingsActivity extends InjectableActivity{

    @Override
    protected int provideContentViewId() {
        return R.layout.activity_setting;
    }

    @Override
    public List<Object> getModules() {
        return Arrays.<Object>asList(new SettingModule());
    }
}