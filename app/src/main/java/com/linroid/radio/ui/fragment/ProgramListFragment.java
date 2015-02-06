package com.linroid.radio.ui.fragment;

import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.reflect.TypeToken;
import com.linroid.radio.R;
import com.linroid.radio.data.ApiService;
import com.linroid.radio.data.DiskCacheManager;
import com.linroid.radio.model.Album;
import com.linroid.radio.model.Anchor;
import com.linroid.radio.model.Pagination;
import com.linroid.radio.model.Program;
import com.linroid.radio.ui.adapter.ProgramAdapter;
import com.linroid.radio.ui.base.InjectableFragment;
import com.linroid.radio.view.ContentLoaderView;
import com.linroid.radio.view.DividerItemDecoration;
import com.squareup.picasso.Picasso;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.android.app.AppObservable;
import rx.functions.Func1;
import timber.log.Timber;


public class ProgramListFragment extends InjectableFragment implements ContentLoaderView.OnRefreshListener, ContentLoaderView.OnMoreListener {
    public static final String KEY_PROGRAM = "program";
    public static final String EXTRA_ALBUM = "album";
    public static final String EXTRA_ANCHOR = "anchor";
    @InjectView(R.id.recycler)
    RecyclerView recyclerView;
    @InjectView(R.id.content_loader)
    ContentLoaderView loaderView;

    @Inject
    Picasso picasso;
    @Inject
    DiskCacheManager cacheManager;
    @Inject
    ApiService apiService;

    ProgramAdapter adapter;
    protected Map<String, String> conditions;
    boolean hasLoaded = false;

    public static ProgramListFragment newInstance() {
        ProgramListFragment fragment = new ProgramListFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public static ProgramListFragment newInstance(Anchor anchor) {
        ProgramListFragment fragment = new ProgramListFragment();
        Bundle args = new Bundle();
        args.putParcelable(EXTRA_ANCHOR, anchor);
        fragment.setArguments(args);
        return fragment;
    }

    public static ProgramListFragment newInstance(Album album) {
        ProgramListFragment fragment = new ProgramListFragment();
        Bundle args = new Bundle();
        args.putParcelable(EXTRA_ALBUM, album);
        fragment.setArguments(args);
        return fragment;
    }

    public ProgramListFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        conditions = new HashMap<>();
        Bundle data = getArguments();
        if (data != null) {
            if (data.containsKey(EXTRA_ALBUM)) {
                Album album = data.getParcelable(EXTRA_ALBUM);
                conditions.put("album_id", String.valueOf(album.getId()));
            }
            if (data.containsKey(EXTRA_ANCHOR)) {
                Anchor anchor = data.getParcelable(EXTRA_ANCHOR);
                conditions.put("user_id", String.valueOf(anchor.getId()));
            }
        }
        adapter = new ProgramAdapter(picasso);

        if (savedInstanceState != null) {
            List<Program> programList = savedInstanceState.getParcelableArrayList(KEY_PROGRAM);
            adapter.setListData(programList);
        }else{
            loadData(1);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(KEY_PROGRAM, (ArrayList<Program>) adapter.getProgramList());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Timber.i("onCreateView");
        ViewGroup view = (ViewGroup) inflater.inflate(R.layout.fragment_program_list, container, false);
        ButterKnife.inject(this, view);

        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setHasFixedSize(true);
        recyclerView.addItemDecoration(new DividerItemDecoration(recyclerView.getContext(), DividerItemDecoration.VERTICAL_LIST));

        loaderView.setAdapter(adapter);
        loaderView.setOnRefreshListener(this);
        loaderView.setMoreListener(this);

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    public void loadData(int page) {
        if(!hasLoaded && conditions.size()==0){
            AppObservable.bindFragment(this,
                    Observable.create(new Observable.OnSubscribe<Pagination<Program>>() {
                        @Override
                        public void call(Subscriber<? super Pagination<Program>> subscriber) {
                            if (cacheManager.exits(DiskCacheManager.KEY_PROGRAM)) {
                                Type type = new TypeToken<Pagination<Program>>() {
                                }.getType();
                                Pagination<Program> cachedData = cacheManager.get(DiskCacheManager.KEY_PROGRAM, type);
                                Timber.d("load data from cached file successful!");
                                if (cachedData != null) {
                                    subscriber.onNext(cachedData);
                                }
                            }
                        }
                    }));
        }
        AppObservable.bindFragment(this, apiService.listPrograms(page, conditions))
                .map(new Func1<Pagination<Program>, Pagination<Program>>() {
                    @Override
                    public Pagination<Program> call(Pagination<Program> pagination) {
                        if(pagination.getCurrentPage() == 1  && conditions.size()==0){
                            cacheManager.put(DiskCacheManager.KEY_PROGRAM, pagination);
                        }
                        return pagination;
                    }
                }).subscribe(observer);
    }
    Observer<Pagination<Program>> observer = new Observer<Pagination<Program>>() {
        @Override
        public void onCompleted() {
            Timber.i("listPrograms onCompleted");
        }

        @Override
        public void onError(Throwable throwable) {
            Timber.e(throwable, "listPrograms onError");
            loaderView.notifyLoadFailed(throwable);
        }

        @Override
        public void onNext(Pagination<Program> pagination) {
            hasLoaded = true;
            Timber.i("listPrograms onNext, currentPage:%d, total:%d", pagination.getCurrentPage(), pagination.getTotal());
            if (pagination.getCurrentPage() > 1) {
                int previousIndex = adapter.getProgramList().size();
                adapter.addMoreData(pagination.getData());
                adapter.notifyItemRangeInserted(previousIndex, pagination.getData().size());
            } else {
                adapter.setListData(pagination.getData());
                adapter.notifyDataSetChanged();
            }
            loaderView.setPage(pagination.getCurrentPage(), pagination.getLastPage());
        }
    };

    @Override
    public void onRefresh(boolean fromSwipe) {
        loadData(1);
    }

    @Override
    public void onMore(int page) {
        loadData(page);
    }
}
