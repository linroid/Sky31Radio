package com.linroid.sky31radio.ui.base;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.linroid.sky31radio.module.Injector;

/**
 * Created by linroid on 1/14/15.
 */
public class InjectableFragment extends BaseFragment {
    boolean injected = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if(!injected){
            injected = true;
            Injector injector = (Injector)getActivity();
            injector.inject(this);
        }
    }
}
