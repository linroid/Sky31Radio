package com.linroid.radio.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;

import com.astuetz.PagerSlidingTabStrip;
import com.avos.avoscloud.feedback.FeedbackAgent;
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
import com.linroid.radio.view.SlidingUpPanelLayout;
import com.linroid.radio.view.TitleSwitcher;

import java.util.Arrays;
import java.util.List;

import butterknife.InjectView;
import timber.log.Timber;

/**
 * Created by linroid on 1/14/15.
 */
public class HomeActivity extends InjectableActivity implements AlbumAdapter.OnAlbumSelectedListener, AnchorAdapter.OnAnchorSelectedListener{
    public static final String TAG_ANCHOR = "anchor";
    public static final String TAG_ALBUM = "album";
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

    TitleSwitcher titleSwitcher;
    boolean detail = false;
//    int layer = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        slidingUpPanelLayout.setSlidingEnabled(false);
        setUpPager();
        int currentItem = DEFAULT_PAGER_CURRENT_ITEM;
        if(savedInstanceState!=null){
            currentItem = savedInstanceState.getInt(STATE_PAGER_CURRENT_ITEM);
        }
        viewPager.setCurrentItem(currentItem);
        titleSwitcher = new TitleSwitcher(this);
        titleSwitcher.setToolbar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);


        FeedbackAgent agent = new FeedbackAgent(this);
        agent.sync();
    }

    @Override
    protected int provideContentViewId() {
        return R.layout.activity_home;
    }

    private void setUpPager() {
        pagerAdapter = new HomePagerAdapter(this, getSupportFragmentManager());
        viewPager.setAdapter(pagerAdapter);
        tabStrip.setViewPager(viewPager);
//        ViewPagerObserver vpo = new ViewPagerObserver(viewPager);
//        vpo.addObservableView(titleIndicator);
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
            case R.id.action_search:
                navigateToSearch();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void navigateToSearch() {
        Intent intent = new Intent(this, SearchActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    private void onExit() {
        RadioUtils.stop(this);
        finish();
    }

    private void openSettingsActivity() {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
//        overridePendingTransition(R.anim.slide_in_bottom, R.anim.slide_out_top);
    }

    @Override
    public void onAlbumSelected(Album album) {
        Timber.d("onAlbumSelected: %s", album.getName());
        if(detail){
            super.onBackPressed();
        }
        detail = true;
        ViewCompat.setElevation(homeContainer, 0);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        titleSwitcher.switchForward(album.getName());

//        Fragment fragment = getSupportFragmentManager().findFragmentByTag(TAG_ALBUM);
//        if(fragment!=null){
//            getSupportFragmentManager().beginTransaction()
//                    .remove(fragment)
//                    .commit();
//        }
        getSupportFragmentManager().beginTransaction()
                .add(R.id.home_container, ProgramListFragment.newInstance(album), TAG_ALBUM)
                .addToBackStack(album.getName())
                .setCustomAnimations(android.R.anim.fade_out, android.R.anim.fade_out)
                .commit();
    }

    @Override
    public void onAnchorSelected(Anchor anchor) {
        Timber.d("onAnchorSelected: %s", anchor.getNickname());
        if(detail){
            super.onBackPressed();
        }
        detail = true;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ViewCompat.setElevation(homeContainer, 0);
        titleSwitcher.switchForward(anchor.getNickname());

//        Fragment fragment = getSupportFragmentManager().findFragmentByTag(TAG_ANCHOR);
//        if(fragment!=null){
//            getSupportFragmentManager().beginTransaction()
//                    .remove(fragment)
//                    .commit();
//        }
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.home_container, ProgramListFragment.newInstance(anchor), TAG_ANCHOR)
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
        titleSwitcher.switchBack(getTitle());
        ViewCompat.setElevation(homeContainer, getSupportActionBar().getElevation());
    }

    @Override
    public void setTitle(CharSequence title) {
        if(toolbar!=null && titleSwitcher!=null){
            titleSwitcher.setCurrentTitle(title);
        }else{
            super.setTitle(title);
        }
    }
    @Override
    public void setTitle(int titleId) {
        setTitle(getText(titleId));
    }
}
