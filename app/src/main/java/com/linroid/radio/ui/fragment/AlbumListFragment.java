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

import com.linroid.radio.R;
import com.linroid.radio.data.ApiService;
import com.linroid.radio.model.Album;
import com.linroid.radio.ui.adapter.AlbumAdapter;
import com.linroid.radio.ui.base.InjectableFragment;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import timber.log.Timber;


public class AlbumListFragment extends InjectableFragment
        implements SwipeRefreshLayout.OnRefreshListener {
    public static final String KEY_ALBUM = "key_album";
    @InjectView(R.id.recycler)
    RecyclerView recyclerView;
    @InjectView(R.id.refresher)
    SwipeRefreshLayout refreshLayout;

    @Inject
    ApiService apiService;
    @Inject
    Picasso picasso;
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
        if (getArguments() != null) {
        }
        adapter = new AlbumAdapter(getActivity(), picasso);
        adapter.setOnAlbumSelectedListener(listener);
        if(savedInstanceState!=null){
            List<Album> albumList = savedInstanceState.getParcelableArrayList(KEY_ALBUM);
            adapter.setListData(albumList);
        }
        loadData();
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

    @Override
    public void onRefresh() {
        Timber.i("onRefresh");
        loadData();
    }
    public void loadData(){
        if(refreshLayout!=null && !refreshLayout.isRefreshing()){
            refreshLayout.setRefreshing(true);
        }
        apiService.listAlbums()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<List<Album>>() {
                    @Override
                    public void onCompleted() {
                        refreshLayout.setRefreshing(false);
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        onCompleted();
                        Timber.e(throwable, "发生错误: %s", throwable.getMessage());
                    }

                    @Override
                    public void onNext(List<Album> albums) {
                        adapter.setListData(albums);
                        adapter.notifyDataSetChanged();
                    }
                });
    }
}
