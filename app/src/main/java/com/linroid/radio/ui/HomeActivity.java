package com.linroid.radio.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;

import com.astuetz.PagerSlidingTabStrip;
import com.linroid.radio.R;
import com.linroid.radio.model.Album;
import com.linroid.radio.model.Anchor;
import com.linroid.radio.module.HomeModule;
import com.linroid.radio.ui.adapter.AlbumAdapter;
import com.linroid.radio.ui.adapter.AnchorAdapter;
import com.linroid.radio.ui.adapter.HomePagerAdapter;
import com.linroid.radio.ui.base.InjectableActivity;
import com.linroid.radio.ui.fragment.ProgramListFragment;
import com.linroid.radio.utils.RadioUtils;
import com.linroid.radio.widgets.SlidingUpPanelLayout;

import java.util.Arrays;
import java.util.List;

import butterknife.InjectView;
import timber.log.Timber;

/**
 * Created by linroid on 1/14/15.
 */
public class HomeActivity extends InjectableActivity implements AlbumAdapter.OnAlbumSelectedListener, AnchorAdapter.OnAnchorSelectedListener{
    public static final String INTENT_RECEIVER_UPDATE_PROGRESS = "com.linroid.radio.action.intent.receiver.update.progress";

    public static final String INTENT_RECEIVER_GET_PLAYING_SONG = "com.xtuers.android.radio.receiver.GetPlayingSong";

    public static final String KEY_PROGRESS = "progress";
    public static final String KEY_CACHE_PROGRESS = "cache_progress";
    public static final String KEY_DURATION_STR = "duration_str";
    public static final String KEY_PROGRESS_STR = "progress_str";
    public static final String KEY_PROGRAM =  "program";
    public static final String KEY_PERCENT =  "percent";
    public static final String KEY_CONTROL =  "control";

    public static final String STATE_PAGER_CURRENT_ITEM = "pager_current_item";
    public static final int DEFAULT_PAGER_CURRENT_ITEM = 2;
    @InjectView(R.id.tab_strip)
    PagerSlidingTabStrip tabStrip;
    @InjectView(R.id.view_pager)
    ViewPager viewPager;
    @InjectView(R.id.home_container)
    ViewGroup homeContainer;
    @InjectView(R.id.sliding_layout)
    SlidingUpPanelLayout slidingUpPanelLayout;

    HomePagerAdapter pagerAdapter;

    boolean detail = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setUpPager();
        int currentItem = DEFAULT_PAGER_CURRENT_ITEM;
        if(savedInstanceState!=null){
            currentItem = savedInstanceState.getInt(STATE_PAGER_CURRENT_ITEM);
        }
        viewPager.setCurrentItem(currentItem);
    }

    @Override
    protected int provideContentViewId() {
        return R.layout.activity_home;
    }

    private void setUpPager() {
        pagerAdapter = new HomePagerAdapter(this, getSupportFragmentManager());
        viewPager.setAdapter(pagerAdapter);
        tabStrip.setViewPager(viewPager);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(STATE_PAGER_CURRENT_ITEM, viewPager.getCurrentItem());
    }

    @Override
    public List<Object> getModules() {
        return Arrays.<Object>asList(new HomeModule(this));
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_settings:
                openSettingsActivity();
                break;
            case android.R.id.home:
            case R.id.home:
                onBackPressed();
                return true;
            case R.id.action_exit:
                onExit();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void onExit() {
        RadioUtils.stop(this);
        super.onBackPressed();
    }

    private void openSettingsActivity() {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.abc_slide_in_bottom, R.anim.abc_slide_out_top);
    }

    @Override
    public void onAlbumSelected(Album album) {
        Timber.d("onAlbumSelected: %s", album.getName());
        detail = true;
        ViewCompat.setElevation(homeContainer, 0);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(album.getName());
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.home_container, ProgramListFragment.newInstance(album), "album")
                .addToBackStack(album.getName())
                .setCustomAnimations(android.R.anim.fade_out, android.R.anim.fade_out)
                .commit();
    }

    @Override
    public void onAnchorSelected(Anchor anchor) {
        Timber.d("onAnchorSelected: %s", anchor.getNickname());
        detail = true;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ViewCompat.setElevation(homeContainer, 0);
        getSupportActionBar().setTitle(anchor.getNickname());
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.home_container, ProgramListFragment.newInstance(anchor), "anchor")
                .addToBackStack(anchor.getNickname())
                .setCustomAnimations(android.R.anim.fade_out, android.R.anim.fade_out)
                .commit();
    }

    @Override
    public void onBackPressed() {
        if(slidingUpPanelLayout.isPanelExpanded()){
            slidingUpPanelLayout.collapsePanel();
            return;
        }else if(detail){
            restoreActionBar();
        }
        super.onBackPressed();
    }

    private void restoreActionBar(){
        detail = false;
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setTitle(getTitle());
        ViewCompat.setElevation(homeContainer, getSupportActionBar().getElevation());
    }
}
