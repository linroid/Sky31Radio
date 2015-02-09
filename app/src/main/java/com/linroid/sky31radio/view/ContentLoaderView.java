package com.linroid.sky31radio.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.linroid.sky31radio.R;

import timber.log.Timber;

/**
 * Created by linroid on 2/3/15.
 */
public class ContentLoaderView extends FrameLayout implements SwipeRefreshLayout.OnRefreshListener, View.OnClickListener {
    public static final int LOAD_MORE_ITEM_SLOP = 1;
    View loadingView;
    View emptyView;
    View errorView;

    Button emptyRetryBtn;
    Button errorRetryBtn;
    TextView emptyMessageTV;
    TextView errorMessageTV;

    SwipeRefreshLayout refreshLayout;
    RecyclerView recyclerView;
    private View contentView;

    private OnRefreshListener refreshListener;
    private OnMoreListener moreListener;

    private boolean loadMore = false;
    private int totalPage = 1;
    private int currentPage = 1;


    private int padding;
    private int paddingLeft;
    private int paddingRight;
    private int paddingTop;
    private int paddingBottom;
    private boolean clipToPadding;

    public static final String DISPLAY_STATE = "display_state";
    public static final int STATE_CONTENT = 0x1;
    public static final int STATE_LOADING = 0x2;
    public static final int STATE_EMPTY = 0x3;
    public static final int STATE_ERROR = 0x4;
    private int displayState = STATE_LOADING;

    public ContentLoaderView(Context context) {
        super(context);
        initViews();
    }

    public ContentLoaderView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initAttrs(context, attrs);
        initViews();
    }

    public ContentLoaderView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttrs(context, attrs);
        initViews();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public ContentLoaderView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    private void initAttrs(Context ctx, AttributeSet attr){
        TypedArray ta = ctx.obtainStyledAttributes(attr, R.styleable.ContentLoaderView);
        padding         =  ta.getDimensionPixelSize(R.styleable.ContentLoaderView_android_padding, -1);
        paddingLeft     = ta.getDimensionPixelSize(R.styleable.ContentLoaderView_android_paddingLeft, -1);
        paddingRight    = ta.getDimensionPixelSize(R.styleable.ContentLoaderView_android_paddingRight, -1);
        paddingTop      = ta.getDimensionPixelSize(R.styleable.ContentLoaderView_android_paddingTop, -1);
        paddingBottom   = ta.getDimensionPixelSize(R.styleable.ContentLoaderView_android_paddingBottom, -1);
        clipToPadding   = ta.getBoolean(R.styleable.ContentLoaderView_android_clipToPadding, true);
        ta.recycle();
    }
    private void initViews(){
        inflate(getContext(), R.layout.merge_content_loader, this);
        setPadding(0, 0, 0, 0);
        setClickable(true);
        errorView = findViewById(R.id.error_view);
        emptyView = findViewById(R.id.empty_view);
        loadingView = findViewById(R.id.loading_view);
        refreshLayout = (SwipeRefreshLayout) findViewById(R.id.refresher);
        recyclerView = (RecyclerView) findViewById(R.id.recycler);
        errorRetryBtn = (Button) findViewById(R.id.btn_error_retry);
        errorMessageTV = (TextView) findViewById(R.id.error_message);
        emptyRetryBtn = (Button) findViewById(R.id.btn_empty_retry);
        emptyMessageTV = (TextView) findViewById(R.id.empty_message);

        errorRetryBtn.setOnClickListener(this);
        emptyRetryBtn.setOnClickListener(this);
        contentView = refreshLayout;
        refreshLayout.setEnabled(false);
        refreshLayout.setColorSchemeResources(
                android.R.color.holo_blue_light,
                android.R.color.holo_purple,
                android.R.color.holo_green_light,
                android.R.color.holo_red_light,
                android.R.color.holo_orange_light);
        refreshLayout.setOnRefreshListener(this);
        recyclerView.setOnScrollListener(mRecyclerScrollListener);
        if(padding!=-1){
            recyclerView.setPadding(padding, padding, padding,padding);
        }else{
            recyclerView.setPadding(paddingLeft, paddingTop, paddingRight, paddingBottom);
        }
        recyclerView.setClipToPadding(clipToPadding);
        setDisplayState(STATE_LOADING);
    }

    RecyclerView.OnScrollListener mRecyclerScrollListener = new RecyclerView.OnScrollListener() {
        int totalItemCount;
        int visibleItemCount;
        int firstVisibleItemPosition;
        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);
        }

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
            RecyclerView.LayoutManager layoutManager;
            layoutManager = recyclerView.getLayoutManager();
            totalItemCount      = layoutManager.getItemCount();
            visibleItemCount    = layoutManager.getChildCount();
            View firstVisibleChild = recyclerView.getChildAt(0);
            firstVisibleItemPosition =  recyclerView.getChildPosition(firstVisibleChild);
            if(totalPage > currentPage &&
                !loadMore&&
                    (firstVisibleItemPosition+visibleItemCount+LOAD_MORE_ITEM_SLOP) >= totalItemCount ){

                loadMore = true;
                if(moreListener !=null){
                    moreListener.onMore(++currentPage);
                    refreshLayout.setEnabled(false);
                    Timber.d("load more page:%d", currentPage);
                }
            }
        }
    };
    public void setAdapter(RecyclerView.Adapter adapter){
        recyclerView.setAdapter(adapter);
        if(adapter.getItemCount()> 0){
            setDisplayState(STATE_CONTENT);
        }
        adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                update();
            }

            @Override
            public void onItemRangeChanged(int positionStart, int itemCount) {
                super.onItemRangeChanged(positionStart, itemCount);
                update();
            }

            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                update();
            }

            @Override
            public void onItemRangeRemoved(int positionStart, int itemCount) {
                super.onItemRangeRemoved(positionStart, itemCount);
                update();
            }

            @Override
            public void onItemRangeMoved(int fromPosition, int toPosition, int itemCount) {
                super.onItemRangeMoved(fromPosition, toPosition, itemCount);
                update();
            }
            private void update(){
                Timber.d("Adapter updated, itemCount:%d", recyclerView.getLayoutManager().getItemCount());
                int itemCount = recyclerView.getLayoutManager().getItemCount();
                if(itemCount>0){
                    if(loadMore){
                        loadMoreCompleted();
                    }
                    setDisplayState(STATE_CONTENT);
                }else{
                    setDisplayState(STATE_EMPTY);
                }
                if(refreshLayout.isRefreshing()){
                    refreshLayout.setRefreshing(false);
                }
            }
        });
    }
//    @OnClick(R.id.btn_retry)
    public void onRetryButtonClick(){
        refreshLayout.setRefreshing(true);
        setDisplayState(STATE_CONTENT);
        if(refreshListener !=null){
            refreshListener.onRefresh(false);
        }
    }

    public void notifyLoadFailed(Throwable error){
        if(refreshLayout.isRefreshing()){
            refreshLayout.setRefreshing(false);
        }
        loadMore = false;
        if(currentPage==1 && recyclerView.getLayoutManager().getChildCount() == 0){
            errorMessageTV.setText(error.getMessage());
            setDisplayState(STATE_ERROR);
        }else{
            setDisplayState(STATE_CONTENT);
            Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
    private void loadMoreCompleted(){
        loadMore = false;
        refreshLayout.setEnabled(true);
        setDisplayState(STATE_CONTENT);
    }

    public void setMoreListener(OnMoreListener moreListener) {
        this.moreListener = moreListener;
    }

    public void setPage(int currentPage, int totalPage) {
        this.currentPage = currentPage;
        this.totalPage = totalPage;
    }

    private void setDisplayState(int state){
        this.displayState = state;
        loadingView.setVisibility(state==STATE_LOADING ? VISIBLE : GONE);
        emptyView.setVisibility(state==STATE_EMPTY ? VISIBLE : GONE);
        errorView.setVisibility(state==STATE_ERROR ? VISIBLE : GONE);
        contentView.setVisibility(state == STATE_CONTENT ? VISIBLE : GONE);
    }
    public void setOnRefreshListener(OnRefreshListener refreshListener) {
        this.refreshListener = refreshListener;
        refreshLayout.setEnabled(true);
    }

    @Override
    public void onRefresh() {
        currentPage = 1;
        if(refreshListener !=null){
            refreshListener.onRefresh(true);
        }
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        Parcelable parcelable = super.onSaveInstanceState();
        SavedState savedState = new SavedState(parcelable);
        savedState.state = this.displayState;
        return savedState;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        SavedState savedState = (SavedState) state;
        super.onRestoreInstanceState(savedState.getSuperState());
        this.displayState = savedState.state;
        setDisplayState(this.displayState);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_error_retry:
            case R.id.btn_empty_retry:{
                setDisplayState(STATE_LOADING);
                onRefresh();
                break;
            }

        }
    }

    public static interface OnRefreshListener {
        void onRefresh(boolean fromSwipe);
    }
    public static interface OnMoreListener {
        void onMore(int page);
    }


    static class SavedState extends BaseSavedState{
        private int state;
        SavedState(Parcelable superState) {
            super(superState);
        }

        public SavedState(Parcel source) {
            super(source);
            try {
                state = source.readInt();
            } catch (IllegalArgumentException e) {
                state = STATE_LOADING;
            }
        }
        @Override
        public void writeToParcel(Parcel out, int flags) {
            super.writeToParcel(out, flags);
            out.writeInt(state);
        }

        public static final Creator<SavedState> CREATOR =
            new Creator<SavedState>() {
                @Override
                public SavedState createFromParcel(Parcel in) {
                    return new SavedState(in);
                }

                @Override
                public SavedState[] newArray(int size) {
                    return new SavedState[size];
                }
            };
    }
}
