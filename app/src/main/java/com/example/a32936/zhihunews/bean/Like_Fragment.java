package com.example.a32936.zhihunews.bean;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.a32936.zhihunews.Adapter.Column_newsAdapter;
import com.example.a32936.zhihunews.Adapter.NewsAdapter;
import com.example.a32936.zhihunews.News.MainActivity;
import com.example.a32936.zhihunews.R;
import com.example.a32936.zhihunews.SQLiteDatabase.Login_Activity;
import com.example.a32936.zhihunews.SQLiteDatabase.MyDatabaseHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Like_Fragment extends Fragment {
    private View rootView;
    List<Map<String,Object>> list=new ArrayList<>();
    List<Map<String,Object>> list_1=new ArrayList<>();
    private StringBuilder response;
    private String thumbnail;
    private String name;
    private String description;
    private String id;
    private MyDatabaseHelper dbHelper;
    private RecyclerView recyclerView_news,recyclerView_column;
    private String title;
    private String news_image;
    private SwipeRefreshLayout swipeRefreshLayout;
    private String column_id;
    private static int flag;

    @Override
    public View onCreateView(@Nullable LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        list.clear();
        list_1.clear();   //加载布局前首先清空list，避免出现左右滑动时布局重新加载出现内容重复
        if (Login_Activity.signer==null){
            rootView = inflater.inflate(R.layout.like_nosigner,container,false);
            flag = 0;
        }else{
        rootView = inflater.inflate(R.layout.activity_like_,container,false);
        flag = 1;
        initUi();
        }
        return rootView;

    }

    private void initUi(){

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);
            if(flag != 0) {
                CreateThread();
                swipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.SwipeRefresh);
                swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimaryDark);
                swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                    public void onRefresh() {
                        list_1.clear();
                        list.clear();

                        NewsAdapter newsAdapter = new NewsAdapter(getContext(), list_1);
                        Column_newsAdapter column_newsAdapter = new Column_newsAdapter(getContext(), list);

                        CreateThread();
                        column_newsAdapter.notifyDataSetChanged();
                        newsAdapter.notifyDataSetChanged();
                        swipeRefreshLayout.setRefreshing(false);
                        Toast.makeText(getContext(), "刷新成功", Toast.LENGTH_SHORT).show();
                    }
                });
            }
    }

    public void CreateThread(){

        recyclerView_column = rootView.findViewById(R.id.recycleview_column);
        recyclerView_news = rootView.findViewById(R.id.recycleview_news);
        dbHelper = new MyDatabaseHelper(getContext());

        //查数据库
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor cursor = db.query("Like_news",new String[]{"Owner","News_id","News_title","News_image"},"Owner = ?",new String[]{Login_Activity.signer},null,null,"id");
        if (cursor.moveToFirst()){
            do {
                id = cursor.getString(cursor.getColumnIndex("News_id"));
                title = cursor.getString(cursor.getColumnIndex("News_title"));
                news_image = cursor.getString(cursor.getColumnIndex("News_image"));
                Map map=new HashMap();
                map.put("id",id);
                map.put("title", title);
                map.put("thumbnail", news_image);
                list_1.add(map);

                LinearLayoutManager manager=new LinearLayoutManager(getContext());

                NewsAdapter newsAdapter = new NewsAdapter(getContext(),list_1);


                recyclerView_news.setLayoutManager(manager);
                recyclerView_news.setAdapter(newsAdapter);
            }while (cursor.moveToNext());
        }
        cursor.close();
        LinearLayoutManager manager=new LinearLayoutManager(getContext());

        NewsAdapter newsAdapter = new NewsAdapter(getContext(),list_1);

        recyclerView_news.setLayoutManager(manager);
        recyclerView_news.setAdapter(newsAdapter);



        //查数据库
        SQLiteDatabase db1 = dbHelper.getWritableDatabase();
        Cursor cursor1 = db1.query("Like_column",new String[]{"Owner","Column_id","Column_description","Column_image","Column_name"},"Owner = ?",new String[]{Login_Activity.signer},null,null,"id");
        if (cursor1.moveToFirst()){
            do {
                column_id = cursor1.getString(cursor1.getColumnIndex("Column_id"));
                thumbnail = cursor1.getString(cursor1.getColumnIndex("Column_image"));
                description = cursor1.getString(cursor1.getColumnIndex("Column_description"));
                name = cursor1.getString(cursor1.getColumnIndex("Column_name"));
                Map map1=new HashMap();
                map1.put("id", column_id);
                map1.put("thumbnail", thumbnail);
                map1.put("name", name);
                map1.put("description", description);
                list.add(map1);

                LinearLayoutManager manager1=new LinearLayoutManager(getContext());

                Column_newsAdapter column_newsAdapter =new Column_newsAdapter(getContext(),list);

                recyclerView_column.setLayoutManager(manager1);
                recyclerView_column.setAdapter(column_newsAdapter);

            }while (cursor1.moveToNext());
        }
        cursor1.close();
        LinearLayoutManager manager1=new LinearLayoutManager(getContext());

        Column_newsAdapter column_newsAdapter =new Column_newsAdapter(getContext(),list);

        recyclerView_column.setLayoutManager(manager1);
        recyclerView_column.setAdapter(column_newsAdapter);
    }

}
