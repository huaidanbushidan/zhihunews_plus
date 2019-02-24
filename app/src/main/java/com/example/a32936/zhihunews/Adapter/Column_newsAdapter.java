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
import com.example.a32936.zhihunews.News.Column_news_detailsActivity;
import com.example.a32936.zhihunews.R;
import com.example.a32936.zhihunews.SQLiteDatabase.Login_Activity;
import com.example.a32936.zhihunews.SQLiteDatabase.MyDatabaseHelper;

import java.util.List;
import java.util.Map;

public class Column_newsAdapter extends RecyclerView.Adapter<Column_newsAdapter.ViewHolder> {

    private List<Map<String, Object>> list;
    private Context context;
    public static String news_id;
    private MyDatabaseHelper dbHelper;
    private String thumbnail;
    private String description;
    private String name;
    private String id;
    private int flag;

    public Column_newsAdapter(Context context, List<Map<String, Object>> list) {
        this.context = context;
        this.list = list;
    }

    static class ViewHolder extends RecyclerView.ViewHolder{
        ImageView webView;
        TextView textView1;
        TextView textView2;
        ImageView imageView;
        View Column_newsView;

        public ViewHolder(@NonNull View view) {
            super(view);
            Column_newsView = view;
            textView1 = view.findViewById(R.id.column_news_title);
            webView = view.findViewById(R.id.column_news_image);
            textView2 = view.findViewById(R.id.column_news_details);
            imageView = view.findViewById(R.id.column_news_like);

        }
    }
    @NonNull
    @Override
    public Column_newsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, final int i) {
        final View view = LayoutInflater.from(context).inflate(R.layout.item_column_news, viewGroup, false);
        final Column_newsAdapter.ViewHolder holder = new Column_newsAdapter.ViewHolder(view);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = holder.getAdapterPosition();
                news_id = (String) list.get(position).get("id");
                news_id = "https://news-at.zhihu.com/api/4/section/"+news_id;
                Log.d("hhh",news_id);
                Intent intent = new Intent();
                intent.setClass(context,Column_news_detailsActivity.class);
                context.startActivity(intent);
            }
        });
        holder.imageView.setOnClickListener(new View.OnClickListener() {
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
                    String name = map.get("name").toString();
                    String thumbnail = map.get("thumbnail").toString();
                    String description = map.get("description").toString();
                    dbHelper = new MyDatabaseHelper(context);
                    SQLiteDatabase db = dbHelper.getWritableDatabase();

                    Cursor cursor = db.query("Like_column",new String[]{"Owner","Column_id"},"Owner = ?",new String[]{Login_Activity.signer},null,null,"id");
                    if (cursor.moveToFirst()){
                        do {
                            String Cloumn_id = cursor.getString(cursor.getColumnIndex("Column_id"));
                            if (Cloumn_id.equals(id)){
                                flag ++;
                                break;
                            }
                        }while (cursor.moveToNext());
                    }
                    if (flag != 0) {
                        db.delete("Like_column", "Owner = ? and Column_id = ?", new String[]{ Login_Activity.signer,id});
                        Toast.makeText(context, "取消收藏成功", Toast.LENGTH_SHORT).show();
                        imageButton.setBackgroundResource(R.drawable.dislike);
                    }else {
                        ContentValues values = new ContentValues();
                        values.put("Owner", Login_Activity.signer);
                        values.put("Column_id", id);
                        values.put("Column_image", thumbnail);
                        values.put("Column_name", name);
                        values.put("Column_description", description);
                        db.insert("Like_column", null, values);
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
    public void onBindViewHolder(@NonNull Column_newsAdapter.ViewHolder holder, int position) {

        Map map = list.get(position);
        id = map.get("id").toString();
        name = map.get("name").toString();
        description = map.get("description").toString();
        thumbnail = map.get("thumbnail").toString();
        Glide.with(context).load(map.get("thumbnail").toString()).placeholder(R.drawable.logo).error(R.drawable.logo).into(holder.webView);
        holder.textView1.setText(map.get("name").toString());
        holder.textView2.setText(map.get("description").toString());

    }


    @Override
    public int getItemCount() {
        return list.size();
    }
}
