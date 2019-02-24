package com.example.a32936.zhihunews.Adapter;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
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

public class Column_news_details_Adapter extends RecyclerView.Adapter<Column_news_details_Adapter.ViewHolder> {

    private List<Map<String, Object>> list;
    private Context context;
    public static String news_id,news_id_17;   //新闻的id（不是网址）
    private MyDatabaseHelper dbHelper;
    private String string;
    private String title;
    private int flag;


    public Column_news_details_Adapter(Context context, List<Map<String, Object>> list) {
        this.context = context;
        this.list = list;
    }

    static class ViewHolder extends RecyclerView.ViewHolder{
        ImageView webView;
        TextView textView1;
        TextView textView2;
        ImageButton imageButton;
        View Column_newsView;

        public ViewHolder(@NonNull View view) {
            super(view);
            Column_newsView = view;
            textView1 = view.findViewById(R.id.column_news_title);
            webView = view.findViewById(R.id.column_news_image);
            textView2 = view.findViewById(R.id.column_news_details);
            imageButton = view.findViewById(R.id.column_news_like);

        }
    }
    @NonNull
    @Override
    public Column_news_details_Adapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, final int i) {
        final View view = LayoutInflater.from(context).inflate(R.layout.item_column_news, viewGroup, false);
        final Column_news_details_Adapter.ViewHolder holder = new Column_news_details_Adapter.ViewHolder(view);
        dbHelper = new MyDatabaseHelper(context);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = holder.getAdapterPosition();
                news_id_17 = (String) list.get(position).get("id");
                news_id = "https://news-at.zhihu.com/api/4/news/"+news_id_17;
                Log.d("hhh",news_id);
                Intent intent = new Intent();
                intent.putExtra("flag","column");
                intent.setClass(context,News_Activity.class);
                context.startActivity(intent);
            }
        });

        holder.imageButton.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {
                ImageButton imageButton;
                imageButton = view.findViewById(R.id.column_news_like);
                flag = 0;
                if (Login_Activity.signer == null){
                    Toast.makeText(context,"请先登录",Toast.LENGTH_SHORT).show();
                }else {
                    int position = holder.getAdapterPosition();
                    Map map = list.get(position);               //获取所点击的item
                    String id = map.get("id").toString();
                    String title = map.get("title").toString();
                    String string = map.get("images").toString().replace("[","");
                    string = string.replace("]","");
                    string = string.replace("\"","");
                    string = string.replace("\\",""); //读取到的网址中出现非常鬼畜的符号，此处加以处理
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
                        values.put("News_image", string);
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
    public void onBindViewHolder(@NonNull Column_news_details_Adapter.ViewHolder holder, int position) {

        Map map = list.get(position);
        Log.d("image",map.get("images").toString());
        string = map.get("images").toString().replace("[","");
        string = string.replace("]","");
        string = string.replace("\"","");
        string = string.replace("\\",""); //读取到的网址中出现非常鬼畜的符号，此处加以处理
        Log.d("string", string);
        Glide.with(context).load(string).placeholder(R.drawable.logo).error(R.drawable.logo).into(holder.webView);

        news_id_17 = map.get("id").toString();
        title = map.get("title").toString();
        holder.textView1.setText(map.get("title").toString());
        Log.d("title",map.get("title").toString());
        holder.textView2.setText(map.get("date").toString());
    }


    @Override
    public int getItemCount() {
        return list.size();
    }
}


