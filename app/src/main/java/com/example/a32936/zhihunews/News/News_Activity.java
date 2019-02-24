package com.example.a32936.zhihunews.News;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.a32936.zhihunews.Adapter.Column_news_details_Adapter;
import com.example.a32936.zhihunews.Adapter.NewsAdapter;
import com.example.a32936.zhihunews.R;
import com.example.a32936.zhihunews.SQLiteDatabase.Login_Activity;
import com.example.a32936.zhihunews.SQLiteDatabase.MyDatabaseHelper;
import com.example.a32936.zhihunews.bean.HTML;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class News_Activity extends AppCompatActivity {
    public static final int GET_DATA_SUCCESS = 1,GET_DETAILS_SUCCESS = 2;
    private StringBuilder response,response1;
    private String body;
    private String image_source;
    private String title;
    private String image;
    private String long_comments, popularity, short_comments, comments;
    private String share_url;
    private String js;
    private String ga_prefix;
    private String thumbnail;
    private String section_id;
    private String name;
    private String images;
    private String type;
    private String id;
    private int flag;
    public static String long_comments_id,short_comments_id;
    private String css1;
    private MyDatabaseHelper dbHelper;
    private String theme_id;
    private String editor_name;
    private String theme_name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_);
        Button long_comments = findViewById(R.id.long_comments);
        Button short_comments = findViewById(R.id.short_comments);
        Button button_like = findViewById(R.id.like_news);
        dbHelper = new MyDatabaseHelper(this);
        CreateThread();
        CreateThread_details();
        long_comments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(News_Activity.this,short_comments_Activity.class);
                intent.putExtra("flag",1);
                long_comments_id = "https://news-at.zhihu.com/api/4/story/"+id+"/long-comments";
                startActivity(intent);
            }
        });

        short_comments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(News_Activity.this,short_comments_Activity.class);
                intent.putExtra("flag",2);
                short_comments_id = "https://news-at.zhihu.com/api/4/story/"+id+"/short-comments";
                startActivity(intent);
            }
        });

        button_like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                flag = 0;
                if (Login_Activity.signer == null){
                    Toast.makeText(News_Activity.this,"请先登录",Toast.LENGTH_SHORT).show();
                }else {
                    dbHelper = new MyDatabaseHelper(News_Activity.this);
                    SQLiteDatabase db = dbHelper.getWritableDatabase();

                    Cursor cursor = db.query("Like_news",new String[]{"Owner","News_id"},"Owner = ?",new String[]{Login_Activity.signer},null,null,"id");
                    if (cursor.moveToFirst()){
                        do {
                            String Cloumn_id = cursor.getString(cursor.getColumnIndex("News_id"));
                            if (Cloumn_id.equals(id)){
                                flag ++;
                                break;
                            }
                        }while (cursor.moveToNext());
                    }
                    if (flag != 0) {
                        db.delete("Like_news", "Owner = ? and News_id = ?", new String[]{ Login_Activity.signer,id});
                        Toast.makeText(News_Activity.this, "取消收藏成功", Toast.LENGTH_SHORT).show();
                    }else {
                        ContentValues values = new ContentValues();
                        values.put("Owner", Login_Activity.signer);
                        values.put("News_id", id);
                        values.put("News_image", image);
                        values.put("News_title", title);
                        db.insert("Like_news", null, values);
                        values.clear();
                        Toast.makeText(News_Activity.this, "收藏成功", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    private void CreateThread_details() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection connection = null;
                BufferedReader reader = null;
                try {
                    Intent intent = getIntent();
                    URL url_1 = null;
                    if (intent.getStringExtra("flag").equals("column")){
                        url_1 = new URL("https://news-at.zhihu.com/api/4/story-extra/"+Column_news_details_Adapter.news_id_17);
                    }else if (intent.getStringExtra("flag").equals("recently")) {
                        url_1 = new URL("https://news-at.zhihu.com/api/4/story-extra/"+NewsAdapter.getNews_id_17);
                    }
                    connection = (HttpURLConnection) url_1.openConnection();
                    connection.setRequestMethod("GET");
//                    connection.connect();
                    InputStream inputStream = connection.getInputStream();
                    //读取输入流
                    reader = new BufferedReader(new InputStreamReader(inputStream));
                    response1 = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response1.append(line);
                    }
                    Log.d("h", response1.toString());
                    //showResponse(response.toString());
                    Message message = Message.obtain();
                    Bundle bundle = new Bundle();
                    bundle.putString("response", response1.toString());
                    message.setData(bundle);
                    message.what = GET_DETAILS_SUCCESS;
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


    public void CreateThread() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection connection = null;
                BufferedReader reader = null;
                try {
                    Intent intent = getIntent();
                    URL url_1 = null;
                    if (intent.getStringExtra("flag").equals("column")){
                        url_1 = new URL(Column_news_details_Adapter.news_id);
                    }else if (intent.getStringExtra("flag").equals("recently")) {
                        url_1 = new URL(NewsAdapter.news_id);
                    }
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
                    Log.d("h", response.toString());
                    //showResponse(response.toString());
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

                case GET_DETAILS_SUCCESS:
                    praseJSONWithJSONObject_details(response1.toString());
                default:
                    break;

            }


        }
    };

    private void praseJSONWithJSONObject_details(String JsonData) {          //用于加载新闻评论数等信息
        try{
            JSONObject jsonObject = new JSONObject(JsonData);
            long_comments = jsonObject.getString("long_comments");
            popularity = jsonObject.getString("popularity");
            short_comments = jsonObject.getString("short_comments");
            comments = jsonObject.getString("comments");

            TextView textView_short = findViewById(R.id.short_comments_number);
            TextView textView_long = findViewById(R.id.long_comments_number);
            TextView textView_comments_number = findViewById(R.id.comments_number);
            TextView textView_like = findViewById(R.id.like_number);

            textView_short.setText("短评论数："+short_comments);
            textView_comments_number.setText("评论总数："+comments);
            textView_long.setText("长评论数："+long_comments);
            textView_like.setText("点赞数："+popularity);

        }catch (JSONException e) {
            e.printStackTrace();
        }

    }


    @SuppressLint("SetJavaScriptEnabled")
    private void praseJSONWithJSONObject(String JsonData) {
        try {
            JSONObject jsonObject = new JSONObject(JsonData);
            type = jsonObject.getString("type");
            if (type.equals("0")) {             //用于处理极少数的站外文章
                body = jsonObject.getString("body");
                Log.d("hh", body);
                image_source = jsonObject.getString("image_source");
                title = jsonObject.getString("title");
                image = jsonObject.getString("image");
                share_url = jsonObject.getString("share_url");
                js = jsonObject.getString("js");
                ga_prefix = jsonObject.getString("ga_prefix");
                if (jsonObject.has("section")) {
                    JSONObject jsonObject1_section = jsonObject.getJSONObject("section");
                    Log.d("lanmu", "" + jsonObject1_section.toString());
                    thumbnail = jsonObject1_section.getString("thumbnail");
                    section_id = jsonObject1_section.getString("id");
                    name = jsonObject1_section.getString("name");
                }
                images = jsonObject.getString("images");

                id = jsonObject.getString("id");
                JSONArray css = jsonObject.getJSONArray("css");
                css1 = css.getString(0);




            WebView webView = findViewById(R.id.News_details);
            webView.getSettings().setJavaScriptEnabled(true);
            webView.setWebViewClient(new WebViewClient());
            String htmlData = HTML.createHtmlData(body, css1, js);
            webView.loadData(htmlData, HTML.MIME_TYPE, HTML.ENCODING);
//            webView.loadData(body, "text/html", "UTF -8");
            //webView.loadUrl(css1);

            ImageView imageView = findViewById(R.id.News_details_image);
            Glide.with(News_Activity.this).load(image).placeholder(R.drawable.logo).error(R.drawable.logo).into(imageView);

            TextView textView_resource = findViewById(R.id.News_details_imageresource);
            textView_resource.setText(image_source);

            TextView textView_title = findViewById(R.id.title);
            textView_title.setText(title);

            }else{
                body = jsonObject.getString("body");
                title = jsonObject.getString("title");
                JSONArray recommenders = jsonObject.getJSONArray("recommenders");
                String avatar = recommenders.getString(0);
                share_url = jsonObject.getString("share_url");
                js = jsonObject.getString("js");
                JSONObject theme = jsonObject.getJSONObject("theme");
                thumbnail = theme.getString("thumbnail");
                String theme_id = theme.getString("id");
                name = theme.getString("name");
                ga_prefix = jsonObject.getString("ga_prefix");
                JSONArray images = jsonObject.getJSONArray("images");
                String images_image = images.getString(0);
                id = jsonObject.getString("id");
                JSONArray css = jsonObject.getJSONArray("css");
                css1 = css.getString(0);

                TextView textView_title = findViewById(R.id.title);
                textView_title.setText(title);
                WebView webView = findViewById(R.id.News_details);
                webView.getSettings().setJavaScriptEnabled(true);
                webView.setWebViewClient(new WebViewClient());
                webView.loadUrl(share_url);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


}
