package com.linroid.radio.ui.base;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.linroid.radio.R;
import com.readystatesoftware.systembartint.SystemBarTintManager;

import butterknife.ButterKnife;
import butterknife.InjectView;
import timber.log.Timber;

/**
 * Created by linroid on 1/14/15.
 */
public abstract class BaseActivity extends ActionBarActivity {
    @InjectView(R.id.toolbar)
    protected Toolbar toolbar;

    SystemBarTintManager tintManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Timber.i("onCreate");
        setContentView(provideContentViewId());
        ButterKnife.inject(this);
        if(toolbar!=null){
            setSupportActionBar(toolbar);
        }
        if(Build.VERSION.SDK_INT <= 19){
            tintManager = new SystemBarTintManager(this);
            tintManager.setStatusBarTintEnabled(true);
            tintManager.setNavigationBarTintEnabled(true);
        }
    }
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public void setStatusColor(int color){
        if(Build.VERSION.SDK_INT > 19){
            getWindow().setStatusBarColor(color);
        }else{
            tintManager.setStatusBarTintColor(color);
        }
    }
    public void setNavigationColor(int color){

    }
    protected abstract int provideContentViewId();

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            NavUtils.navigateUpTo(getParent(), NavUtils.getParentActivityIntent(this));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

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
