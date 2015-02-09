package com.linroid.sky31radio.ui;

import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;

import com.linroid.sky31radio.R;
import com.linroid.sky31radio.module.SearchModule;
import com.linroid.sky31radio.ui.base.InjectableActivity;
import com.linroid.sky31radio.ui.fragment.SearchProgramListFragment;

import java.util.Arrays;
import java.util.List;

import timber.log.Timber;

public class SearchActivity extends InjectableActivity {
    SearchProgramListFragment resultFragment;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        resultFragment = SearchProgramListFragment.newInstance();
    }

    @Override
    protected int provideContentViewId() {
        return R.layout.activity_search;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_search, menu);
        MenuItem menuItem = menu.findItem(R.id.action_search);
        final SearchView searchView = (SearchView) menuItem.getActionView();
        searchView.setQueryHint(getString(R.string.hint_action_search));
        searchView.setFocusable(true);
        searchView.setIconified(false);
        searchView.requestFocusFromTouch();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                Timber.d("onQueryTextSubmit, Query: %s", s);
                if(getSupportFragmentManager().findFragmentById(R.id.program_list_container)==null){
                    getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.program_list_container, resultFragment)
                            .commit();
                    getSupportFragmentManager().executePendingTransactions();
                }
                resultFragment.setKeyword(s);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @Override
    public List<Object> getModules() {
        return Arrays.<Object>asList(new SearchModule(this));
    }
}
