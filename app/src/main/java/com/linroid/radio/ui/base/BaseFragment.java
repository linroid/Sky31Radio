package com.linroid.radio.ui.base;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Timber.i("onCreateView");
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Timber.i("onActivityCreated");
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        Timber.i("onAttach");
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Timber.i("onDetach");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Timber.i("onDestroyView");
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

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Timber.i("onViewCreated");
    }
}
