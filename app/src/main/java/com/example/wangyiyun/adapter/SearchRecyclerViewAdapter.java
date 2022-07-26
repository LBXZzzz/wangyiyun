package com.example.wangyiyun.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.wangyiyun.contacts.ContactClass;
import com.example.wangyiyun.R;
import com.example.wangyiyun.entries.SongItem;
import com.squareup.picasso.Picasso;

import java.util.List;

public class SearchRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<SongItem> songItemArrayList;
    private Context mContext;

    public SearchRecyclerViewAdapter(Context context,List<SongItem> songItemArrayList){
        this.songItemArrayList=songItemArrayList;
        this.mContext=context;
    }
    private ContactClass.OnItemClickListener mOnItemClickListener;

    public void setOnItemClickListener(ContactClass.OnItemClickListener mOnItemClickListener){
        this.mOnItemClickListener=mOnItemClickListener;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView textView;
        TextView textView1;
        ImageView imageView;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textView=itemView.findViewById(R.id.tv_song_name);
            textView1=itemView.findViewById(R.id.tv_singer_name);
            imageView=itemView.findViewById(R.id.iv_music_photo);
        }
    }

    public class FooterHolder extends RecyclerView.ViewHolder{
        TextView footerText;
        ProgressBar progressBar;
        public FooterHolder(@NonNull View itemView) {
            super(itemView);
            footerText=itemView.findViewById(R.id.tv_footer);
            progressBar=itemView.findViewById(R.id.pb_footer);
            if (songItemArrayList.size()<30){
                progressBar.setVisibility(View.GONE);
                footerText.setText("已经到底了喔~~");
            }
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if(holder instanceof SearchRecyclerViewAdapter.ViewHolder){
            SongItem songItem=songItemArrayList.get(position);
            (((ViewHolder)holder).textView).setText(songItem.getSongName());
            (((ViewHolder)holder).textView1).setText(songItem.getSingerName());
            Picasso.with(mContext)
                    .load(songItem.getPicUrl()+"?param=150y150")
                    .resize(150,150)
                    .placeholder(R.drawable.ic_android_black_24dp)
                    .into(((ViewHolder) holder).imageView);
        }
        if(mOnItemClickListener!=null){
            holder.itemView.setOnClickListener(v -> {
                int position1 =holder.getLayoutPosition();
                mOnItemClickListener.onItemClick(holder.itemView, position1);
            });
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (position == songItemArrayList.size()) {
            //最后一个 是底部item
            return 1;
        } else {
            return 0;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == 0) {
            //你的item
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_view_item_search,parent,false);
            return new ViewHolder(view);
        } else {
            //底部“加载更多”item
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_footer, parent, false);
            return new FooterHolder(view);
        }
    }



    @Override
    public int getItemCount() {
        return songItemArrayList.size()+1;
    }

    public void updateData(List<SongItem> list){
        //再此处理获得的数据  list为传进来的数据
        //... list传进来的数据 添加到mList中
        songItemArrayList.addAll(list);
        notifyDataSetChanged();
    }

}
