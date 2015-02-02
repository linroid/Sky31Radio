package com.linroid.radio.ui.base;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import timber.log.Timber;

/**
 * Created by linroid on 1/14/15.
 */
public class BaseFragment extends Fragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Timber.i("onCreate");
    }
//    public void onPause() {
//        super.onPause();
//        AVAnalytics.onFragmentEnd("my-list-fragment");
//    }
//
//    public void onResume() {
//        super.onResume();
//        AVAnalytics.onFragmentStart("my-list-fragment");
//    }

    public void setStatusColor(int color){
        ((BaseActivity) getActivity()).setStatusColor(color);
    }
}
