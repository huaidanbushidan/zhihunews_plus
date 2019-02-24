package com.example.a32936.zhihunews.News;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.example.a32936.zhihunews.Adapter.Column_newsAdapter;
import com.example.a32936.zhihunews.Adapter.Column_news_details_Adapter;
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

public class Column_news_detailsActivity extends AppCompatActivity {
    public static final int GET_DATA_SUCCESS = 1;
    private StringBuilder response;
    private String stories;
    private RecyclerView recyclerView;

    List<Map<String,Object>> list=new ArrayList<>();
    private Column_news_details_Adapter column_news_details_Adapter = new Column_news_details_Adapter(this,list);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_column_news_details);
        recyclerView = findViewById(R.id.column_news_details_re);
        CreateThread();
    }

    public void CreateThread() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection connection = null;
                BufferedReader reader = null;
                try {
                    URL url_1 = new URL(Column_newsAdapter.news_id);
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
            JSONArray stories = jsonObject.getJSONArray("stories");
            Log.d("hh", stories.toString());
            for (int i = 0;i<stories.length();i++){
                JSONObject jsonObject1 = stories.getJSONObject(i);
                String images = jsonObject1.getString("images");
                Log.d("images",images);
                String date = jsonObject1.getString("date");
                String display_date = jsonObject1.getString("display_date");
                String id = jsonObject1.getString("id");
                String title = jsonObject1.getString("title");

                Map map=new HashMap();

                map.put("images",images);
                map.put("id",id);
                map.put("date",date);
                map.put("title",title);
                map.put("display_date",display_date);
                list.add(map);//检查到list了
                Log.d("list",list.toString());

                LinearLayoutManager manager=new LinearLayoutManager(this);
                recyclerView.setLayoutManager(manager);
                recyclerView.setAdapter(column_news_details_Adapter);
            }


        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
