package com.example.a32936.zhihunews.bean;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.a32936.zhihunews.Adapter.NewsAdapter;
import com.example.a32936.zhihunews.News.MainActivity;
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

public class Recent_Fragment extends Fragment {
    private View rootView;
    private RecyclerView recyclerView;
    public static final int GET_DATA_SUCCESS = 1;
    private SwipeRefreshLayout swipeRefreshLayout;
    List<Map<String,Object>> list=new ArrayList<>();
    private int flag;
    private StringBuilder response;
    private String id;
    private String url;
    private String thumbnail;
    private String title;

    @Override
    public View onCreateView(@Nullable LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        rootView = inflater.inflate(R.layout.recent_news,container,false);
        initUi();
        recyclerView = rootView.findViewById(R.id.recycleview);
        swipeRefreshLayout = (SwipeRefreshLayout)rootView.findViewById(R.id.SwipeRefresh);
        return rootView;
    }

    private void initUi(){

    }

    @Override
    public void onActivityCreated(Bundle savedInstanState){
        super.onActivityCreated(savedInstanState);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimaryDark);




        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            public void onRefresh() {
                list.clear();
                flag = 0;

                NewsAdapter newsAdapter=new NewsAdapter(getContext(),list);


                CreateThread();
                newsAdapter.notifyDataSetChanged();
                swipeRefreshLayout.setRefreshing(false);
                if (flag!= 0){
                    Toast.makeText(getContext(),"刷新成功",Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(getContext(),"刷新失败，请检查网络状态",Toast.LENGTH_SHORT).show();
                }
            }
        });
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());


        CreateThread();


        recyclerView.setLayoutManager(linearLayoutManager);
        NewsAdapter newsAdapter = new NewsAdapter(getContext(),list);
        recyclerView.setAdapter(newsAdapter);
    }

    public void CreateThread(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection connection = null;
                BufferedReader reader = null;
                try {
                    URL url_1 = new URL("https://news-at.zhihu.com/api/4/news/hot");
                    connection = (HttpURLConnection) url_1.openConnection();
                    connection.setRequestMethod("GET");
//                    connection.connect();
                    InputStream inputStream = connection.getInputStream();
                    //读取输入流
                    reader = new BufferedReader(new InputStreamReader(inputStream));
                    response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    Log.d("hhh",response.toString());
                    flag++;                 //用于判断刷新成功与否
                    Message message = Message.obtain();
                    Bundle bundle = new Bundle();
                    bundle.putString("response",response.toString());
                    message.setData(bundle);
                    message.what = GET_DATA_SUCCESS;
                    handler.sendMessage(message);

                }catch (Exception e){
                    e.printStackTrace();
                }finally {
                    if (reader != null){
                        try{
                            reader.close();
                        }catch (IOException e){
                            e.printStackTrace();
                        }
                    }
                    if (connection != null){
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

    private void praseJSONWithJSONObject(String JsonData){
        try{
            JSONObject jsonObject = new JSONObject(JsonData);
            JSONArray jsonArray = jsonObject.getJSONArray("recent");
            for (int i = 0;i<jsonArray.length();i++){
                JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                id = jsonObject1.getString("news_id");
                url = jsonObject1.getString("url");
                thumbnail = jsonObject1.getString("thumbnail");
                title = jsonObject1.getString("title");
                Map map=new HashMap();

                map.put("id",id);
                map.put("url",url);
                map.put("title",title);
                map.put("thumbnail",thumbnail);
                list.add(map);

                LinearLayoutManager manager=new LinearLayoutManager(getContext());
                recyclerView.setLayoutManager(manager);

                NewsAdapter newsAdapter=new NewsAdapter(getContext(),list);

                recyclerView.setAdapter(newsAdapter);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


}
