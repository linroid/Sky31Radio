package com.linroid.radio.ui.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.reflect.TypeToken;
import com.linroid.radio.R;
import com.linroid.radio.data.ApiService;
import com.linroid.radio.data.DiskCacheManager;
import com.linroid.radio.model.Anchor;
import com.linroid.radio.ui.adapter.AnchorAdapter;
import com.linroid.radio.ui.base.InjectableFragment;
import com.linroid.radio.view.ContentLoaderView;
import com.squareup.picasso.Picasso;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.android.app.AppObservable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import timber.log.Timber;


public class AnchorListFragment extends InjectableFragment implements ContentLoaderView.OnRefreshListener {

    @Override
    public void onDetach() {
        super.onDetach();
    }

    public static final String KEY_USER = "key_user";

    @InjectView(R.id.content_loader)
    ContentLoaderView loaderView;
    @InjectView(R.id.recycler)
    RecyclerView recyclerView;

    @Inject
    ApiService apiService;
    @Inject
    Picasso picasso;
    @Inject
    DiskCacheManager cacheManager;

    boolean hasLoaded = false;
    AnchorAdapter adapter;
    AnchorAdapter.OnAnchorSelectedListener listener;

    public static AnchorListFragment newInstance() {
        AnchorListFragment fragment = new AnchorListFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public AnchorListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        adapter = new AnchorAdapter(getActivity(), picasso);
        adapter.setOnAnchorSelectedListener(listener);
        if(savedInstanceState!=null){
            List<Anchor> anchorList = savedInstanceState.getParcelableArrayList(KEY_USER);
            adapter.setListData(anchorList);
        }else{
            loadData();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(KEY_USER, (ArrayList<Anchor>) adapter.getAnchorList());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_anchor_list, container, false);
        ButterKnife.inject(this, view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2));

        loaderView.setAdapter(adapter);
        loaderView.setOnRefreshListener(this);

        return  view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if(activity instanceof AnchorAdapter.OnAnchorSelectedListener){
            listener = (AnchorAdapter.OnAnchorSelectedListener) activity;
        }else{
            throw new IllegalArgumentException("activity must implement AnchorAdapter.OnAnchorSelectedListener");
        }
    }

    public void loadData(){
        if(!hasLoaded){
            AppObservable.bindFragment(this,
                    Observable.create(new Observable.OnSubscribe<List<Anchor>>() {
                    @Override
                    public void call(Subscriber<? super List<Anchor>> subscriber) {
                        if (cacheManager.exits(DiskCacheManager.KEY_ANCHOR)) {
                            Type type = new TypeToken<List<Anchor>>() { }.getType();
                            List<Anchor> cachedData = cacheManager.get(DiskCacheManager.KEY_ANCHOR, type);
                            Timber.d("load data from cached file successful!");
                            if(cachedData!=null && cachedData.size()>0) {
                                subscriber.onNext(cachedData);
                            }
                        }
                    }
                })
            )
            .subscribe(observer);
        }
        AppObservable.bindFragment(this, apiService.listAnchor())
                .map(new Func1<List<Anchor>, List<Anchor>>() {
                    @Override
                    public List<Anchor> call(List<Anchor> anchors) {
                        cacheManager.put(DiskCacheManager.KEY_ANCHOR, anchors);
                        return anchors;
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
    }
    Observer<List<Anchor>> observer = new Observer<List<Anchor>>() {
        @Override
        public void onCompleted() {
            Timber.i("listAnchor onCompleted");
        }

        @Override
        public void onError(Throwable throwable) {
            Timber.e(throwable, "发生错误: %s", throwable.getMessage());
            loaderView.notifyLoadFailed(throwable);
        }

        @Override
        public void onNext(List<Anchor> anchors) {
            Timber.d("onNext %s", anchors.toString());
            hasLoaded = true;
            adapter.setListData(anchors);
            adapter.notifyDataSetChanged();
        }
    };

    @Override
    public void onRefresh(boolean fromSwipe) {
        loadData();
    }
}
