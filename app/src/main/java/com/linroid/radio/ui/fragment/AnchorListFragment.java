package com.linroid.radio.ui.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
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
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.subjects.PublishSubject;
import timber.log.Timber;


public class AnchorListFragment extends InjectableFragment implements SwipeRefreshLayout.OnRefreshListener {

    @Override
    public void onDetach() {
        super.onDetach();
    }

    public static final String KEY_USER = "key_user";
    @InjectView(R.id.recycler)
    RecyclerView recyclerView;
    @InjectView(R.id.refresher)
    SwipeRefreshLayout refreshLayout;

    @Inject
    ApiService apiService;
    @Inject
    Picasso picasso;
    @Inject
    DiskCacheManager cacheManager;

    boolean hasLoaded = false;
    Subscription subscription;
    PublishSubject<List<Anchor>> anchorRequest;
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
        }
        loadData();
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
        recyclerView.setAdapter(adapter);
        refreshLayout.setColorSchemeResources(
                android.R.color.holo_blue_light,
                android.R.color.holo_purple,
                android.R.color.holo_green_light,
                android.R.color.holo_red_light,
                android.R.color.holo_orange_light
        );
        refreshLayout.setOnRefreshListener(this);

        return  view;
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
            throw new IllegalArgumentException(" activity must implement AnchorAdapter.OnAnchorSelectedListener");
        }
    }

    @Override
    public void onRefresh() {
        Timber.i("onRefresh");
        loadData();
    }
    public void loadData(){
        if(refreshLayout!=null && !refreshLayout.isRefreshing()){
            refreshLayout.setRefreshing(true);
        }
        if(anchorRequest==null || subscription.isUnsubscribed()){
            anchorRequest = PublishSubject.create();
            subscription = anchorRequest.subscribe(observer);
        }
        if(!hasLoaded){
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
                    subscriber.onCompleted();
                }
            }).subscribe(anchorRequest);
        }
        apiService.listAnchor()
                .map(new Func1<List<Anchor>, List<Anchor>>() {
                    @Override
                    public List<Anchor> call(List<Anchor> anchors) {
                        cacheManager.put(DiskCacheManager.KEY_ANCHOR, anchors);
                        return anchors;
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(anchorRequest);
    }
    Observer<List<Anchor>> observer = new Observer<List<Anchor>>() {
        @Override
        public void onCompleted() {
            if(refreshLayout!=null && !refreshLayout.isRefreshing()){
                refreshLayout.setRefreshing(false);
            }
        }

        @Override
        public void onError(Throwable throwable) {
            onCompleted();
            Timber.e(throwable, "发生错误: %s", throwable.getMessage());
        }

        @Override
        public void onNext(List<Anchor> anchors) {
            Timber.d("onNext %s", anchors.toString());
            hasLoaded = true;
            adapter.setListData(anchors);
            adapter.notifyDataSetChanged();
        }
    };
}
