package com.linroid.radio.ui.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.linroid.radio.R;
import com.linroid.radio.model.Program;
import com.linroid.radio.utils.RadioUtils;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by linroid on 1/14/15.
 */
public class ProgramAdapter extends RecyclerView.Adapter<ProgramAdapter.ViewHolder>{
    List<Program> programList = new ArrayList<>();
    Picasso picasso;
    OnLoadModeListener moreListener;
    boolean isLoading = true;
    public ProgramAdapter(Picasso picasso) {
        this.picasso = picasso;
    }

    public void setOnLoadMoreListener(OnLoadModeListener moreListener) {
        this.moreListener = moreListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        View view = inflater.inflate(R.layout.list_program, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int i) {
        Program program = programList.get(i);
        holder.titleTV.setText(program.getTitle());
        holder.authorTV.setText(program.getAuthor());
        picasso.load(program.getThumbnail()).into(holder.thumbnailIV);
        if(i==programList.size()-1 && moreListener!=null && moreListener.hasMore() && !isLoading){
            moreListener.onLoadMore();
        }
    }

    @Override
    public int getItemCount() {
        return programList.size();
    }

    public void setListData(List listData) {
        this.programList.clear();
        this.programList = listData;
        isLoading = false;
    }
    public void addMoreData(List<Program> moreData){
        this.programList.addAll(moreData);
        isLoading = false;
    }

    public List<Program> getProgramList() {
        return programList;
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @InjectView(R.id.thumbnail)
        ImageView thumbnailIV;

        @InjectView(R.id.title)
        TextView titleTV;
        @InjectView(R.id.author)
        TextView authorTV;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.inject(this, itemView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int position = getPosition();
            RadioUtils.sendPlayList(v.getContext(), programList, position);
        }
    }
    public static interface OnLoadModeListener{
        boolean hasMore();
        void onLoadMore();
    }
}
