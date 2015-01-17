package com.linroid.radio.ui.fragment;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.linroid.radio.R;
import com.linroid.radio.data.ApiService;
import com.linroid.radio.model.Album;
import com.linroid.radio.model.Anchor;
import com.linroid.radio.model.Pagination;
import com.linroid.radio.model.Program;
import com.linroid.radio.ui.adapter.ProgramAdapter;
import com.linroid.radio.ui.base.InjectableFragment;
import com.linroid.radio.widgets.DividerItemDecoration;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import timber.log.Timber;


public class ProgramListFragment extends InjectableFragment implements SwipeRefreshLayout.OnRefreshListener, ProgramAdapter.OnLoadModeListener {
    public static final String KEY_PROGRAM = "program";
    public static final String EXTRA_ALBUM = "album";
    public static final String EXTRA_ANCHOR = "anchor";
    @InjectView(R.id.recycler)
    RecyclerView recyclerView;
    @InjectView(R.id.refresher)
    SwipeRefreshLayout refreshLayout;

    @Inject
    ApiService apiService;
    @Inject
    Picasso picasso;

    ProgramAdapter adapter;

    int page = 1;
    Pagination pagination;

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

    Map<String, String> conditions;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        conditions = new HashMap<>();
        Bundle data = getArguments();
        if(data!=null){
            if(data.containsKey(EXTRA_ALBUM)){
                Album album = data.getParcelable(EXTRA_ALBUM);
                conditions.put("album_id", String.valueOf(album.getId()));
            }
            if(data.containsKey(EXTRA_ANCHOR)){
                Anchor anchor = data.getParcelable(EXTRA_ANCHOR);
                conditions.put("user_id", String.valueOf(anchor.getId()));
            }
        }
        adapter = new ProgramAdapter(picasso);
        if(savedInstanceState!=null){
            List<Program> programList = savedInstanceState.getParcelableArrayList(KEY_PROGRAM);
            adapter.setListData(programList);
        }
        adapter.setOnLoadMoreListener(this);
        loadData(page);
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
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_program_list, container, false);
        ButterKnife.inject(this, view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setHasFixedSize(true);
        recyclerView.addItemDecoration(new DividerItemDecoration(recyclerView.getContext(), DividerItemDecoration.VERTICAL_LIST));
        recyclerView.setAdapter(adapter);
        refreshLayout.setColorSchemeResources(
                android.R.color.holo_blue_light,
                android.R.color.holo_purple,
                android.R.color.holo_green_light,
                android.R.color.holo_red_light,
                android.R.color.holo_orange_light
        );
        refreshLayout.setOnRefreshListener(this);
        return view;
    }

    @Override
    public void onRefresh() {
        page = 1;
        loadData(1);
    }

    private void loadData(int page) {
        if(refreshLayout!=null && !refreshLayout.isRefreshing()){
            refreshLayout.setRefreshing(true);
        }
        apiService.listPrograms(page, conditions)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Pagination<Program>>() {
                    @Override
                    public void onCompleted() {
                        Timber.i("listPrograms onCompleted");
                        refreshLayout.setRefreshing(false);
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        Timber.e(throwable, "listPrograms onError");
                        onCompleted();
                    }

                    @Override
                    public void onNext(Pagination<Program> data) {
                        Timber.i("listPrograms onNext, total:%d", data.getTotal());
                        if(data.getCurrentPage() > 1){
                            int previousIndex = adapter.getProgramList().size();
                            adapter.addMoreData(data.getData());
                            adapter.notifyItemRangeInserted(previousIndex, data.getData().size());
                        }else{
                            adapter.setListData(data.getData());
                            adapter.notifyDataSetChanged();
                        }
                        data.setData(null);
                        pagination = data;

                    }
                });
    }
    @Override
    public boolean hasMore() {
        return pagination!=null&&(pagination.getCurrentPage()<pagination.getLastPage());
    }

    @Override
    public void onLoadMore() {
        loadData(pagination.getCurrentPage()+1);
    }
}
