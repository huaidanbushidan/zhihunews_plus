package com.example.a32936.zhihunews.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.a32936.zhihunews.R;

import java.util.List;
import java.util.Map;

public class commentsAdapter extends RecyclerView.Adapter<commentsAdapter.ViewHolder> {

    private Context context;
    private List<Map<String, Object>> list;


    public commentsAdapter(Context context, List<Map<String, Object>> list) {
        this.context = context;
        this.list = list;
    }
    @NonNull
    @Override
    public commentsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_short_comments_xml, viewGroup, false);
        final ViewHolder holder = new ViewHolder(view);
        return holder;
    }



    static class ViewHolder extends RecyclerView.ViewHolder {

        TextView user_name;
        ImageView head_image;
        TextView comment_content;
        TextView comments_time;
        TextView comments_likes_number;
        View CommentsView;

        public ViewHolder(@NonNull View view) {
            super(view);
            CommentsView = view;
            user_name = view.findViewById(R.id.user_name);
            head_image = view.findViewById(R.id.head_image);
            comment_content = view.findViewById(R.id.comment_content);
            comments_time = view.findViewById(R.id.comments_time);
            comments_likes_number = view.findViewById(R.id.comments_likes_number);

        }
    }

    @Override
    public void onBindViewHolder(@NonNull commentsAdapter.ViewHolder holder, int position) {
        Map map = list.get(position);

        Glide.with(context).load(map.get("avatar").toString()).placeholder(R.drawable.logo).error(R.drawable.logo).into(holder.head_image);
        holder.user_name.setText(map.get("author").toString());
        holder.comment_content.setText(map.get("content").toString());
        holder.comments_likes_number.setText(map.get("likes").toString());
        holder.comments_time.setText(map.get("time").toString());

    }


    @Override
    public int getItemCount() {
        return list.size();
    }
}
