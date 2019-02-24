package com.example.a32936.zhihunews.News;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.a32936.zhihunews.Adapter.NewsAdapter;
import com.example.a32936.zhihunews.R;
import com.example.a32936.zhihunews.SQLiteDatabase.Edit_informationActivity;
import com.example.a32936.zhihunews.SQLiteDatabase.Login_Activity;
import com.example.a32936.zhihunews.SQLiteDatabase.MyDatabaseHelper;
import com.example.a32936.zhihunews.bean.About;
import com.example.a32936.zhihunews.bean.All_Fragment;
import com.example.a32936.zhihunews.bean.Like_Fragment;
import com.example.a32936.zhihunews.bean.Recent_Fragment;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
        private String Head_name;
        private ImageView imageView;
        private MyDatabaseHelper dbHelper;
        private int flag;
        private SwipeRefreshLayout swipeRefreshLayout;
        List<Map<String,Object>> list=new ArrayList<>();
        private StringBuilder response;
        public static final int GET_DATA_SUCCESS = 1;
        private RecyclerView recyclerView;
        private NewsAdapter newsAdapter=new NewsAdapter(this,list);   //改成全局变量方便刷新数据时调用


        private TabLayout tabLayout;
        private ViewPager viewPager;
        FragmentPagerAdapter fragmentPagerAdapter;
        private ArrayList<String> TitleList = new ArrayList<>();  //页卡标题集合
        private Fragment all_Fragment,recent_Fragment,like_Fragment;  //页卡视图
        private ArrayList<Fragment> ViewList = new ArrayList<>();   //页卡视图集合
        private TextView textView;

    //以下是oncreate方法
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        dbHelper = new MyDatabaseHelper(this);               //显示头像时调用数据库

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        //首先找到tablayout控件和view pager控件
        tabLayout = findViewById(R.id.Tablayout);
        viewPager = findViewById(R.id.Viewpager);
//        FragmentManager manager = AboutFragment.this.getChildFragmentManager();

        all_Fragment = new All_Fragment();
        recent_Fragment = new Recent_Fragment();
        like_Fragment = new Like_Fragment();

//        all_Fragment = (All_Fragment)getSupportFragmentManager().findFragmentById()


        //添加页卡视图
        ViewList.add(all_Fragment);
        ViewList.add(recent_Fragment);
        ViewList.add(like_Fragment);

        //添加页卡标题
        TitleList.add("栏目总览");
        TitleList.add("热门消息");
        TitleList.add("我的收藏");

        //设置tab模式
        tabLayout.setTabMode(TabLayout.MODE_FIXED);
        //添加tab选项卡
        tabLayout.addTab(tabLayout.newTab().setText(TitleList.get(0)));
        tabLayout.addTab(tabLayout.newTab().setText(TitleList.get(1)));
        tabLayout.addTab(tabLayout.newTab().setText(TitleList.get(2)));

        //设置adapter
        viewPager.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager()){

            //获取每个页卡
            @Override
            public android.support.v4.app.Fragment getItem(int position){
                return ViewList.get(position);
            }

            //获取页卡数
            @Override
            public int getCount(){
                return  TitleList.size();
            }

            //获取页卡标题
            @Override
            public CharSequence getPageTitle(int position){
                return TitleList.get(position);
            }
                             });

        //tab与viewpager绑定
        tabLayout.setupWithViewPager(viewPager);


        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.inflateHeaderView(R.layout.nav_header_main);
        navigationView.inflateMenu(R.menu.activity_main_drawer);
        View navHeaderView = navigationView.getHeaderView(0);
        ImageView imageView = navHeaderView.findViewById(R.id.imageView);
        textView = navHeaderView.findViewById(R.id.Head_name);
        if (Login_Activity.signer != null){
              Head_name = Login_Activity.yhm; //侧滑菜单栏显示用户名
            Log.d("yhm",Head_name);
//        if (Head_name != null){
//            Log.d("tvvvv", textView.toString());
        textView.setText(Head_name);

//            }
        }
        recyclerView = findViewById(R.id.recycleview);




        String Uers_name;
        byte[] head_image=null;
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        if (Login_Activity.signer != null){
        Cursor cursor = db.query("User",new String[]{"UserCount","Head_image","UserName"},"UserCount = ?",new String[]{Login_Activity.signer},null,null,"id");
        if (cursor.moveToFirst()) {
            do {
                 Uers_name = cursor.getString(cursor.getColumnIndex("UserCount"));
                head_image = cursor.getBlob(cursor.getColumnIndex("Head_image"));

//                if (Login_Activity.signer == null){
//                    imageView.setImageResource(R.drawable.logo);
//                }else {
//                    imageView.setImageBitmap(Edit_informationActivity.bitmap);
//                }
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
            if (new String(head_image).equals("1")) {
                imageView.setImageResource(R.drawable.logo);
            } else {
                Bitmap bitmap = Bytes2Bimap(head_image);
                imageView.setImageBitmap(bitmap);
            }
        }else imageView.setImageResource(R.drawable.logo);


        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Login_Activity.signer==null){
                    Intent intent = new Intent(MainActivity.this,Login_Activity.class);
                    startActivity(intent);
                }else {
                    Intent intent = new Intent(MainActivity.this, Edit_informationActivity.class);
                    startActivity(intent);
                }
            }
        });

    }




    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }



    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

          if (id == R.id.nav_about){
                Intent intent = new Intent(MainActivity.this,About.class);
                startActivity(intent);
        }

          if (id == R.id.nav_home){
              //关闭抽屉
          }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }



    private Bitmap Bytes2Bimap(byte[] b) {
        if (b.length != 0) {
            return BitmapFactory.decodeByteArray(b, 0, b.length);
        } else {
        return null;
        }
        }
    }
