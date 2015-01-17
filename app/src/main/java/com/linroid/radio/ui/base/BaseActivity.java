package com.linroid.radio.ui.base;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.linroid.radio.R;

import butterknife.ButterKnife;
import butterknife.InjectView;
import timber.log.Timber;

/**
 * Created by linroid on 1/14/15.
 */
public abstract class BaseActivity extends ActionBarActivity {
    @InjectView(R.id.toolbar)
    protected Toolbar toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Timber.i("onCreate");
        setContentView(provideContentViewId());
        ButterKnife.inject(this);
        if(toolbar!=null){
            setSupportActionBar(toolbar);
        }
    }
    protected abstract int provideContentViewId();

    @Override
    protected void onStop() {
        super.onStop();
        Timber.i("onStop");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Timber.i("onDestroy");
    }

    @Override
    public void onPause() {
        super.onPause();
        Timber.i("onPause");
    }

    @Override
    public void onStart() {
        super.onStart();
        Timber.i("onStart");
    }
}
