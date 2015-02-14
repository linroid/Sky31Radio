package com.linroid.sky31radio.ui.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.reflect.TypeToken;
import com.linroid.sky31radio.R;
import com.linroid.sky31radio.data.ApiService;
import com.linroid.sky31radio.data.DiskCacheManager;
import com.linroid.sky31radio.model.Album;
import com.linroid.sky31radio.model.Pagination;
import com.linroid.sky31radio.ui.adapter.AlbumAdapter;
import com.linroid.sky31radio.ui.base.InjectableFragment;
import com.linroid.sky31radio.view.ContentLoaderView;
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


public class AlbumListFragment extends InjectableFragment implements ContentLoaderView.OnRefreshListener {
    public static final String KEY_ALBUM = "key_album";

    @InjectView(R.id.content_loader)
    ContentLoaderView loaderView;
    @InjectView(R.id.recycler)
    RecyclerView recyclerView;

    @Inject
    Picasso picasso;
    @Inject
    ApiService apiService;
    @Inject
    DiskCacheManager cacheManager;
    boolean hasLoaded = false;
    AlbumAdapter adapter;
    AlbumAdapter.OnAlbumSelectedListener listener;
    public static AlbumListFragment newInstance() {
        AlbumListFragment fragment = new AlbumListFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public AlbumListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        adapter = new AlbumAdapter(getActivity(), picasso);
        adapter.setOnAlbumSelectedListener(listener);

        if(savedInstanceState!=null){
            List<Album> albumList = savedInstanceState.getParcelableArrayList(KEY_ALBUM);
            adapter.setListData(albumList);
        }else{
            loadData(1);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(KEY_ALBUM, (ArrayList<Album>) adapter.getAlbumList());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_album_list, container, false);
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
        if(activity instanceof AlbumAdapter.OnAlbumSelectedListener){
            listener = (AlbumAdapter.OnAlbumSelectedListener) activity;
        }else{
            throw new IllegalArgumentException(" activity must implement AlbumAdapter.OnAlbumSelectedListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        adapter.setOnAlbumSelectedListener(null);
    }
    public void loadData(int page){
        if(!hasLoaded){
            AppObservable.bindFragment(this,
                    Observable.create(new Observable.OnSubscribe<Pagination<Album>>() {
                @Override
                public void call(Subscriber<? super Pagination<Album>> subscriber) {
                    if (cacheManager.exits(DiskCacheManager.KEY_ALBUM)) {
                        Type type = new TypeToken<Pagination<Album>>() {}.getType();
                        Pagination<Album> cachedData = cacheManager.get(DiskCacheManager.KEY_ALBUM, type);
                        if(cachedData!=null){
                            subscriber.onNext(cachedData);
                        }
                    }
                }
            })).subscribe(observer);
        }
        AppObservable.bindFragment(this, apiService.listAlbums(page))
                .map(new Func1<Pagination<Album>, Pagination<Album>>() {
                    @Override
                    public Pagination<Album> call(Pagination<Album> pagination) {
                        cacheManager.put(DiskCacheManager.KEY_ALBUM, pagination);
                        return pagination;
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
    }

    Observer<Pagination<Album>> observer = new Observer<Pagination<Album>>() {
        @Override
        public void onCompleted() {
            Timber.i("listAlbum onCompleted");
        }

        @Override
        public void onError(Throwable throwable) {
            Timber.e(throwable, "发生错误: %s", throwable.getMessage());
            loaderView.notifyLoadFailed(throwable);
        }

        @Override
        public void onNext(Pagination<Album> pagination) {
            hasLoaded = true;
            loaderView.setPage(pagination.getCurrentPage(), pagination.getLastPage());
            adapter.setListData(pagination.getData());
            adapter.notifyDataSetChanged();
        }
    };

    @Override
    public void onRefresh(boolean fromSwipe) {
        loadData(1);
    }
}
