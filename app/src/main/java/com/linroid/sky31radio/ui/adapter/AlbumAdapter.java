package com.linroid.sky31radio.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.linroid.sky31radio.R;
import com.linroid.sky31radio.model.Album;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by linroid on 1/15/15.
 */
public class AlbumAdapter extends RecyclerView.Adapter<AlbumAdapter.ViewHolder> {
    List<Album> albumList = new ArrayList<>();
    Picasso picasso;
    String programCountTpl;
    OnAlbumSelectedListener listener;

    public AlbumAdapter(Context ctx, Picasso picasso) {
        this.picasso = picasso;
        programCountTpl = ctx.getResources().getString(R.string.tpl_program_count);
    }

    public void setOnAlbumSelectedListener(OnAlbumSelectedListener listener) {
        this.listener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        View view = inflater.inflate(R.layout.list_album, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int i) {
        Album album = albumList.get(i);
        holder.nameTV.setText(album.getName());
        holder.programCountTV.setText(String.format(programCountTpl, album.getProgramCount()));
        picasso.load(album.getCover()).placeholder(R.drawable.holde_image).into(holder.thumbnailIV);
    }

    @Override
    public int getItemCount() {
        return albumList==null ? 0 : albumList.size();
    }

    public void setListData(List<Album> listData) {
        this.albumList.clear();
        this.albumList = listData;
    }

    public List<Album> getAlbumList() {
        return albumList;
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @InjectView(R.id.album_thumbnail)
        ImageView thumbnailIV;
        @InjectView(R.id.album_name)
        TextView nameTV;
        @InjectView(R.id.album_program_count)
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
                Album album = albumList.get(position);
                listener.onAlbumSelected(album);
            }
        }
    }
    public static interface OnAlbumSelectedListener{
        void onAlbumSelected(Album album);
    }
}
