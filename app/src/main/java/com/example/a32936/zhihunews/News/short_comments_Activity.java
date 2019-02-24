package com.example.a32936.zhihunews.News;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Toast;

import com.example.a32936.zhihunews.Adapter.commentsAdapter;
import com.example.a32936.zhihunews.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class short_comments_Activity extends AppCompatActivity {
    public static final int GET_DATA_SUCCESS = 1;
    private StringBuilder response;
    private RecyclerView recyclerView;
    List<Map<String,Object>> list=new ArrayList<>();
    private com.example.a32936.zhihunews.Adapter.commentsAdapter commentsAdapter = new commentsAdapter(this,list);
    private URL url_1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_short_comments_);
        recyclerView = findViewById(R.id.recycleview);
        CreateThread();
    }

    public void CreateThread() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection connection = null;
                BufferedReader reader = null;
                try {
                    Intent intent = getIntent();
                    if (intent.getIntExtra("flag",0) == 1){
                        url_1 = new URL(News_Activity.long_comments_id);
                    }else if (intent.getIntExtra("flag",0) == 2){
                        url_1 = new URL(News_Activity.short_comments_id);
                    }
                    connection = (HttpURLConnection) url_1.openConnection();
                    connection.setRequestMethod("GET");
                    InputStream inputStream = connection.getInputStream();
                    //读取输入流
                    reader = new BufferedReader(new InputStreamReader(inputStream));
                    response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    Log.d("h", response.toString());
                    Message message = Message.obtain();
                    Bundle bundle = new Bundle();
                    bundle.putString("response", response.toString());
                    message.setData(bundle);
                    message.what = GET_DATA_SUCCESS;
                    handler.sendMessage(message);
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(short_comments_Activity.this,"暂时还没有长评论",Toast.LENGTH_SHORT).show();
                } finally {
                    if (reader != null) {
                        try {
                            reader.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    if (connection != null) {
                        connection.disconnect();
                    }
                }
            }
        }).start();
    }

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message message) {
            switch (message.what) {
                case GET_DATA_SUCCESS:
                    praseJSONWithJSONObject(response.toString());
                    break;
                default:
                    break;

            }


        }
    };

    @SuppressLint("SetJavaScriptEnabled")
    private void praseJSONWithJSONObject(String JsonData) {
        try {
            JSONObject jsonObject = new JSONObject(JsonData);
            JSONArray jsonArray = jsonObject.getJSONArray("comments");
            for (int i = 0;i<jsonArray.length();i++){
                JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                String author = jsonObject1.getString("author");
                String id = jsonObject1.getString("id");
                String content = jsonObject1.getString("content");
                String likes = jsonObject1.getString("likes");
                String time = jsonObject1.getString("time");
                String avatar = jsonObject1.getString("avatar");

                Map map=new HashMap();

                map.put("id",id);
                map.put("author",author);
                map.put("content",content);
                map.put("likes",likes);
                map.put("time",time);
                map.put("avatar",avatar);
                list.add(map);

                LinearLayoutManager manager=new LinearLayoutManager(this);
                recyclerView.setLayoutManager(manager);
                recyclerView.setAdapter(commentsAdapter);
            }



        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
