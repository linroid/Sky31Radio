package com.linroid.radio.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.linroid.radio.R;
import com.linroid.radio.model.Anchor;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by linroid on 1/15/15.
 */
public class AnchorAdapter extends RecyclerView.Adapter<AnchorAdapter.ViewHolder> {
    List<Anchor> anchorList = new ArrayList<>();
    Picasso picasso;
    String programCountTpl;
    OnAnchorSelectedListener listener;

    public AnchorAdapter(Context ctx, Picasso picasso) {
        this.picasso = picasso;
        programCountTpl = ctx.getResources().getString(R.string.tpl_program_count);
    }

    public void setOnAnchorSelectedListener(OnAnchorSelectedListener listener) {
        this.listener = listener;
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        View view = inflater.inflate(R.layout.list_anchor, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int i) {
        Anchor anchor = anchorList.get(i);
        holder.nicknameTV.setText(anchor.getNickname());
        holder.programCountTV.setText(String.format(programCountTpl, anchor.getProgramCount()));
        picasso.load(anchor.getAvatar()).placeholder(R.drawable.holde_image).into(holder.avatarIV);
    }

    @Override
    public int getItemCount() {
        return anchorList==null ? 0 : anchorList.size();
    }

    public void setListData(List<Anchor> listData) {
        this.anchorList.clear();
        this.anchorList = listData;
    }

    public List<Anchor> getAnchorList() {
        return anchorList;
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @InjectView(R.id.anchor_avatar)
        ImageView avatarIV;
        @InjectView(R.id.anchor_nickname)
        TextView nicknameTV;
        @InjectView(R.id.anchor_program_count)
        TextView programCountTV;
        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.inject(this, itemView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int position = getPosition();
            if(listener!=null){
                Anchor anchor = anchorList.get(position);
                listener.onAnchorSelected(anchor);
            }
        }
    }

    public static interface OnAnchorSelectedListener {
        void onAnchorSelected(Anchor anchor);
    }
}
