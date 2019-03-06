package com.example.a32936.zhihunews.SQLiteDatabase;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.a32936.zhihunews.News.MainActivity;
import com.example.a32936.zhihunews.R;
import com.example.a32936.zhihunews.bean.About;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

public class Edit_informationActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    public static final int TAKE_PHOTO = 1;
    public static final int CHOOSE_PHOTO = 2;
    private ImageView picture;
    private Uri imageUri;
    public String Username;
    private MyDatabaseHelper dbHelper;
    private TextView textViewUserCount;
    private Button buttonmakesure;
    private Button buttonpassword;
    private Button buttongallery;
    private Button buttoncamera;
    private EditText editTextname;
    public int flag;
    private byte[] pic;
    private Button button_exit;
    public static Bitmap bitmap;
    public static String name;
    private TextView textView;
    private String Head_name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_information);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();


        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener((NavigationView.OnNavigationItemSelectedListener) this);
        navigationView.inflateHeaderView(R.layout.nav_header_main);
        navigationView.inflateMenu(R.menu.activity_main_drawer);
        View navHeaderView = navigationView.getHeaderView(0);
        ImageView imageView = navHeaderView.findViewById(R.id.imageView);
        textView = navHeaderView.findViewById(R.id.Head_name);


        textViewUserCount = findViewById(R.id.UserCount);
        textViewUserCount.setText(Login_Activity.account);
        picture = findViewById(R.id.HeadImage);
        buttonmakesure = findViewById(R.id.Makesure);
        buttonpassword = findViewById(R.id.Edit_password);
        buttongallery = findViewById(R.id.Gallery);
        buttoncamera = findViewById(R.id.Camera);
        button_exit = findViewById(R.id.Exit);
        editTextname = findViewById(R.id.Edit_name);
        dbHelper = new MyDatabaseHelper(this);
        //Username = editTextname.getText().toString();
        //Username = Login_Activity.signer;
        editTextname.setText(Login_Activity.yhm);
        flag = 0;


        if (Login_Activity.signer != null){
            Head_name = Login_Activity.yhm; //侧滑菜单栏显示用户名
            Log.d("yhm", Head_name);
//        if (Head_name != null){
//            Log.d("tvvvv", textView.toString());
            textView.setText(Head_name);

//            }
        }

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
//            db.close();
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
                    Intent intent = new Intent(Edit_informationActivity.this,Login_Activity.class);
                    startActivity(intent);
                }else {
                    Intent intent = new Intent(Edit_informationActivity.this, Edit_informationActivity.class);
                    startActivity(intent);
                }
            }
        });


        //头像框从数据库获取用户头像数据
        Cursor cursor = db.query("User",new String[]{"UserCount","Head_image"},"UserCount = ?",new String[]{Login_Activity.signer},null,null,"id asc");
        if (cursor.moveToFirst()){
            do{
//                byte[]
                head_image = cursor.getBlob(cursor.getColumnIndex("Head_image"));
                if (new String(head_image).equals("1")) {
                    picture.setImageResource(R.drawable.logo);
                }else{
                    bitmap = Bytes2Bimap(head_image);
                    picture.setImageBitmap(bitmap);
                }
            }while (cursor.moveToNext());
        }
        cursor.close();
        buttonmakesure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                name = editTextname.getText().toString();
                if (name.equals(Login_Activity.yhm)&&flag==0) {
                    Toast.makeText(Edit_informationActivity.this, "您还未进行修改哦", Toast.LENGTH_SHORT).show();
                } else {
                    SQLiteDatabase db = dbHelper.getWritableDatabase();
                    ContentValues values = new ContentValues();
                    if(pic!=null){
                    values.put("Head_image",pic);
//                    Login_Activity.Head_image = pic;
                    }
                    values.put("UserName", name);
                    db.update("User", values, "UserCount = ?", new String[]{Login_Activity.signer});  //signer与account内容一样
                    Toast.makeText(Edit_informationActivity.this, "修改成功", Toast.LENGTH_SHORT).show();
                    Login_Activity.yhm = name;
                    Intent intent = new Intent(Edit_informationActivity.this, MainActivity.class);
                    finish();
                    startActivity(intent);
                }
            }
        });

        buttonpassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Edit_informationActivity.this, Edit_PasswordActivity.class);
                startActivity(intent);
            }
        });


        buttoncamera=findViewById(R.id.Camera);
        buttoncamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                File outputImage=new File(getExternalCacheDir(),"output_image.jpg");
                try{
                    if(outputImage.exists()){
                        outputImage.delete();
                    }
                    outputImage.createNewFile();
                }catch (IOException e){
                    e.printStackTrace();
                }
                if(Build.VERSION.SDK_INT>=24){
                    imageUri=FileProvider.getUriForFile(Edit_informationActivity.this,"com.example.a32936.fileprovider",outputImage);
                }else{
                    imageUri=Uri.fromFile(outputImage);
                }
                //启动相机程序
                Intent intent=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                intent.putExtra(MediaStore.EXTRA_OUTPUT,imageUri);
                startActivityForResult(intent,TAKE_PHOTO);
            }
        });
        findViewById(R.id.Gallery).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(ContextCompat.checkSelfPermission(Edit_informationActivity.this,Manifest.permission.WRITE_EXTERNAL_STORAGE)!=PackageManager.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions(Edit_informationActivity.this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
                }else
                    openAlbum();
            }
        });

        button_exit.setOnClickListener(new View.OnClickListener() {       //退出登录
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Edit_informationActivity.this,Login_Activity.class);
                startActivity(intent);
                bitmap = null;
                finish();
            }
        });



    }

    private void openAlbum() {
        Intent intent = new Intent("android.intent.action.GET_CONTENT");
        intent.setType("image/*");
        startActivityForResult(intent, CHOOSE_PHOTO);
    }
    private void handleBeforeImage(Intent data) {
        Uri uri=data.getData();
        String imagePath=getImagePath(uri,null);
        displayImage(imagePath);
        flag = 1;
    }
    @TargetApi(19)
    private void handleImage(Intent data) {
        String imagePath = null;
        Uri uri = data.getData();

        if (DocumentsContract.isDocumentUri(this, uri)) {
            // 通过document id来处理
            String docId = DocumentsContract.getDocumentId(uri);
            if ("com.android.providers.media.documents".equals(uri.getAuthority())) {
                // 解析出数字id
                String id = docId.split(":")[1];
                String selection = MediaStore.Images.Media._ID + "=" + id;
                imagePath = getImagePath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, selection);
            } else if ("com.android.providers.downloads.documents".equals(uri.getAuthority())) {
                Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"),
                        Long.valueOf(docId));
                imagePath = getImagePath(contentUri, null);
            }
        } else if ("content".equals(uri.getScheme())) {
            // 如果不是document类型的Uri，则使用普通方式处理
            imagePath = getImagePath(uri, null);
        }

        // 根据图片路径显示图片
        displayImage(imagePath);
        flag = 1;
    }

    private String getImagePath(Uri uri, String selection) {
        String path = null;
        // 通过Uri和selection来获取真实图片路径
        Cursor cursor = getContentResolver().query(uri, null, selection, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            }
            cursor.close();
        }
        return path;
    }

    private void displayImage(String imagePath) {
        if (imagePath != null) {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true; // 只获取图片的大小信息，而不是将整张图片载入在内存中，避免内存溢出
            BitmapFactory.decodeFile(imagePath, options);
            int height = options.outHeight;
            int width= options.outWidth;
            int inSampleSize = 2; // 默认像素压缩比例，压缩为原图的1/2
            int minLen = Math.min(height, width); // 原图的最小边长
            if(minLen > 100) { // 如果原始图像的最小边长大于100dp（此处单位我认为是dp，而非px）
                float ratio = (float)minLen / 100.0f; // 计算像素压缩比例
                inSampleSize = (int)ratio;
            }
            options.inJustDecodeBounds = false; // 计算好压缩比例后，这次可以去加载原图了
            options.inSampleSize = inSampleSize; // 设置为刚才计算的压缩比例
            Bitmap bm = BitmapFactory.decodeFile(imagePath, options); // 解码文件
            pic = getBitmapByte(bm);
            picture.setImageBitmap(bm);
            flag = 1;
        } else {
            Toast.makeText(this, "获取失败", Toast.LENGTH_SHORT).show();
        }
    }
    @Override
    protected void onActivityResult(int requestCode,int resultCode,Intent data){
        switch (requestCode){
            case TAKE_PHOTO:
                if(resultCode==RESULT_OK){
                    try {
                        bitmap=BitmapFactory.decodeStream(getContentResolver().openInputStream(imageUri));
                        Bitmap Bit=compressImage(bitmap);
                        pic = getBitmapByte(Bit);

                        picture.setImageBitmap(Bit);
                        flag = 1;
                    }catch (FileNotFoundException e){
                        e.printStackTrace();
                    }
                }
                break;
            case CHOOSE_PHOTO:
                if (resultCode == RESULT_OK){
                    if(Build.VERSION.SDK_INT>=19)
                        handleImage(data);
                    else
                        handleBeforeImage(data);
                }
                break;
            default:break;
        }
    }

    private Bitmap compressImage(Bitmap image) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);//质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
        int options = 100;
        while (baos.toByteArray().length / 1024 > 100) { //循环判断如果压缩后图片是否大于100kb,大于继续压缩
            baos.reset();//重置baos即清空baos
            image.compress(Bitmap.CompressFormat.JPEG, options, baos);//这里压缩options%，把压缩后的数据存放到baos中
            options -= 10;//每次都减少10
        }
        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());//把压缩后的数据baos存放到ByteArrayInputStream中
        Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);//把ByteArrayInputStream数据生成图片
        return bitmap;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    openAlbum();
                } else {
                    Toast.makeText(this, "您拒绝了权限", Toast.LENGTH_LONG).show();
                }
                break;
            default:
        }
    }


    public byte[] getBitmapByte(Bitmap bitmap) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
        try {
            out.flush();
            out.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return out.toByteArray();
    }

    private Bitmap Bytes2Bimap(byte[] b) {
        if (b.length != 0) {
            return BitmapFactory.decodeByteArray(b, 0, b.length);
        } else {
            return null;
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
//    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_about){
            Intent intent = new Intent(Edit_informationActivity.this,About.class);
            startActivity(intent);
        }

        if (id == R.id.nav_home){
            Intent intent = new Intent(Edit_informationActivity.this,MainActivity.class);
            this.finish();
            startActivity(intent);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}



