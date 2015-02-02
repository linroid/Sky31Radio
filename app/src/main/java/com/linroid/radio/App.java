package com.linroid.radio;

import android.app.Application;

import com.avos.avoscloud.AVAnalytics;
import com.avos.avoscloud.AVInstallation;
import com.avos.avoscloud.AVOSCloud;
import com.avos.avoscloud.PushService;
import com.linroid.radio.module.AppModule;
import com.linroid.radio.module.Injector;
import com.linroid.radio.ui.HomeActivity;

import java.util.Arrays;
import java.util.List;

import dagger.ObjectGraph;
import timber.log.Timber;

/**
 * Created by linroid on 1/14/15.
 */

public class App extends Application
        implements Injector {
    ObjectGraph mObjectGraph;

    public void onCreate() {
        super.onCreate();
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
}