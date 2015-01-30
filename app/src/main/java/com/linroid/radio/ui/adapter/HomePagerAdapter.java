package com.linroid.radio.ui.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.SparseArray;

import com.linroid.radio.R;
import com.linroid.radio.ui.fragment.AlbumListFragment;
import com.linroid.radio.ui.fragment.ProgramListFragment;
import com.linroid.radio.ui.fragment.AnchorListFragment;

/**
 * Created by linroid on 1/14/15.
 */
public class HomePagerAdapter extends FragmentPagerAdapter {
    String[] titles;
    public HomePagerAdapter(Context ctx, FragmentManager fm) {
        super(fm);
        titles = ctx.getResources().getStringArray(R.array.pager_titles);
    }

    @Override
    public Fragment getItem(int position) {
        Fragment fragment;
        switch (position){
            case 0:
                fragment = AnchorListFragment.newInstance();
                break;
            case 1:
                fragment = AlbumListFragment.newInstance();
                break;
            default:
                fragment = ProgramListFragment.newInstance();
                break;
        }
        return fragment;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return titles[position];
    }

    @Override
    public int getCount() {
        return 3;
    }
}
