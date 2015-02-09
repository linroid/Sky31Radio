package com.linroid.sky31radio.ui.base;

import android.os.Bundle;

import com.linroid.sky31radio.App;
import com.linroid.sky31radio.module.Injector;

import dagger.ObjectGraph;
import timber.log.Timber;

/**
 * Created by linroid on 1/14/15.
 */
public abstract class InjectableActivity extends BaseActivity implements Injector{
    protected ObjectGraph objectGraph;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        App app = (App) getApplication();
        objectGraph = app.plus(this);
        objectGraph.inject(this);
        super.onCreate(savedInstanceState);
    }
    public void inject(Object target){
        objectGraph.inject(target);
        Timber.i("inject to %s",  target.getClass().toString());
    }

    public ObjectGraph plus(Object[] modules){
        return objectGraph.plus(modules);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        objectGraph = null;
    }
}
