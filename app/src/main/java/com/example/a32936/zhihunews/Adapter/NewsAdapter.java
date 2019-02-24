package com.example.a32936.zhihunews.Adapter;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.a32936.zhihunews.News.News_Activity;
import com.example.a32936.zhihunews.R;
import com.example.a32936.zhihunews.SQLiteDatabase.Login_Activity;
import com.example.a32936.zhihunews.SQLiteDatabase.MyDatabaseHelper;

import java.util.List;
import java.util.Map;

public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.ViewHolder> {
    //private List<News> mNewsList;
    private Context context;
    private List<Map<String, Object>> list;
    public static String news_id,getNews_id_17;
    private MyDatabaseHelper dbHelper;
    private int flag;
    private String id;
    private String thumbnail;
    private String title;


    public NewsAdapter(Context context, List<Map<String, Object>> list) {
        this.context = context;
        this.list = list;
    }


    @NonNull
    @Override
    public NewsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, final int i) {
        final View view = LayoutInflater.from(context).inflate(R.layout.item_news, viewGroup, false);
        final ViewHolder holder = new ViewHolder(view);
        dbHelper = new MyDatabaseHelper(context);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = holder.getAdapterPosition();
                getNews_id_17 = (String) list.get(position).get("id");
                news_id = "https://news-at.zhihu.com/api/4/news/"+getNews_id_17;
                Log.d("hhh",news_id);
                Intent intent = new Intent();
                intent.putExtra("flag","recently");
                intent.setClass(context,News_Activity.class);
                context.startActivity(intent);
            }
        });
        holder.Goodimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ImageButton imageButton;
                imageButton = view.findViewById(R.id.news_like);

                flag = 0;
                if (Login_Activity.signer == null){
                    Toast.makeText(context,"请先登录",Toast.LENGTH_SHORT).show();
                }else {
                    int position = holder.getAdapterPosition();
                    Map map = list.get(position);               //获取所点击的item
                    String id = map.get("id").toString();
                    String title = map.get("title").toString();
                    String thumbnail = map.get("thumbnail").toString().replace("[","");
//                    string = string.replace("]","");
//                    string = string.replace("\"","");
//                    string = string.replace("\\","");
                    SQLiteDatabase db = dbHelper.getWritableDatabase();
                    Cursor cursor = db.query("Like_news",new String[]{"Owner","News_id"},"Owner = ?",new String[]{Login_Activity.signer},null,null,"id");
                    if (cursor.moveToFirst()){
                        do {
                            String News_id = cursor.getString(cursor.getColumnIndex("News_id"));
                            if (News_id.equals(id)){
                                flag ++;
                                break;
                            }
                        }while (cursor.moveToNext());
                    }
                    if (flag != 0) {
                        db.delete("Like_news", "Owner = ? and News_id = ?", new String[]{Login_Activity.signer,id});
                        Toast.makeText(context, "取消收藏成功", Toast.LENGTH_SHORT).show();

                        imageButton.setBackgroundResource(R.drawable.dislike);

                    }else {

                        ContentValues values = new ContentValues();
                        values.put("Owner", Login_Activity.signer);
                        values.put("News_id", id);
                        values.put("News_image", thumbnail);
                        values.put("News_title", title);
                        db.insert("Like_news", null, values);
                        values.clear();
                        Toast.makeText(context, "收藏成功", Toast.LENGTH_SHORT).show();
                        imageButton.setBackgroundResource(R.drawable.like);


                    }
                }
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull NewsAdapter.ViewHolder holder, int position) {
        //News news = mNewsList.get(position);

        Map map = list.get(position);
        title = map.get("title").toString();
        thumbnail = map.get("thumbnail").toString();
        id = map.get("id").toString();
        Glide.with(context).load(map.get("thumbnail").toString()).placeholder(R.drawable.logo).error(R.drawable.logo).into(holder.NewsPicture);
        holder.NewsName.setText(map.get("title").toString());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        TextView NewsName;
        ImageView NewsPicture;
        ImageView Goodimage;
        View Newsview;

        public ViewHolder(@NonNull View view) {
            super(view);
            Newsview = view;
            NewsName = (TextView) view.findViewById(R.id.NewsName);
            NewsPicture = (ImageView) view.findViewById(R.id.NewsPicture);
            Goodimage = (ImageView) view.findViewById(R.id.news_like);

        }
    }


}
