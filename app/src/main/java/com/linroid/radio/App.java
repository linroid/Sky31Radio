package com.linroid.radio;

import android.app.Application;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.avos.avoscloud.AVAnalytics;
import com.avos.avoscloud.AVInstallation;
import com.avos.avoscloud.AVOSCloud;
import com.avos.avoscloud.PushService;
import com.linroid.radio.module.AppModule;
import com.linroid.radio.module.Injector;
import com.linroid.radio.ui.HomeActivity;

import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

import dagger.ObjectGraph;
import timber.log.Timber;

/**
 * Created by linroid on 1/14/15.
 */

public class App extends Application
        implements Injector, SharedPreferences.OnSharedPreferenceChangeListener {
    ObjectGraph mObjectGraph;

    @Inject
    Timber.Tree tree;
    public void onCreate() {
        super.onCreate();
        PreferenceManager.getDefaultSharedPreferences(this).registerOnSharedPreferenceChangeListener(this);
        Timber.Tree tree = BuildConfig.DEBUG ? new Timber.DebugTree() : new Timber.HollowTree();
        Timber.plant(tree);
        mObjectGraph = ObjectGraph.create(getModules().toArray());
        inject(this);
        initLeancloud();
    }

    private void initLeancloud() {
        AVOSCloud.initialize(this, BuildConfig.LEANCLOUD_APP_ID,BuildConfig.LEANCLOUD_APP_KEY);
        AVInstallation.getCurrentInstallation().saveInBackground();
        PushService.setDefaultPushCallback(this, HomeActivity.class);
        AVAnalytics.enableCrashReport(this, true);
        setNotification(PreferenceManager.getDefaultSharedPreferences(this));
    }

    public List<Object> getModules() {
        return Arrays.<Object>asList(new AppModule(this));
    }

    public void inject(Object target) {
        this.mObjectGraph.inject(target);
    }

    public ObjectGraph plus(Injector injector) {
        return this.mObjectGraph.plus(injector.getModules().toArray());
    }

    public ObjectGraph plus(Object[] modules) {
        return this.mObjectGraph.plus(modules);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if(key.equals(getString(R.string.pref_allow_new_program_notification))){
            setNotification(sharedPreferences);
        }
    }
    private void setNotification(SharedPreferences sharedPreferences){
        boolean enableNotification = sharedPreferences.getBoolean(getString(R.string.pref_allow_new_program_notification), true);
        if(enableNotification){
            PushService.subscribe(this, Constants.PREF_NEW_RADIO, HomeActivity.class);
        }else{
            PushService.unsubscribe(this, Constants.PREF_NEW_RADIO);
        }
    }
}