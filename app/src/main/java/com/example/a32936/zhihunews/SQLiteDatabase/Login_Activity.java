package com.example.a32936.zhihunews.SQLiteDatabase;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.a32936.zhihunews.News.MainActivity;
import com.example.a32936.zhihunews.R;

public class Login_Activity extends AppCompatActivity {
    private MyDatabaseHelper dbHelper;
    public static int flag = 0;
    public static String signer = null;
    private String s = "1";
//    public static byte[] Head_image ;
    public static String yhm = "知乎月报欢迎您";
    public static String account = "Welcome";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_);
        Button button_login = findViewById(R.id.Login);
        Button button_register = findViewById(R.id.Register);
        final EditText editTextcount = findViewById(R.id.UserCount);
        final EditText editTextpassword = findViewById(R.id.UserPassword);
        dbHelper = new MyDatabaseHelper(this);


        button_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signer = null;
                flag = 0;
                String memailview = editTextcount.getText().toString();
                String password1 = editTextpassword.getText().toString();
                SQLiteDatabase db = dbHelper.getWritableDatabase();
                if(memailview.equals("")){Toast.makeText(Login_Activity.this,"账号不能为空",Toast.LENGTH_SHORT).show();}
                else {
                    if(password1.equals("")){Toast.makeText(Login_Activity.this,"密码不能为空",Toast.LENGTH_SHORT).show();}
                    else {
                        String mima ;
                        Cursor cursor = db.query("User",new String[]{"UserName","UserCount","Password","id"},null,null,null,null,"id");
                        if (cursor.moveToFirst()){
                            do {
                                yhm = cursor.getString(cursor.getColumnIndex("UserName"));
                                account = cursor.getString(cursor.getColumnIndex("UserCount"));
                                mima = cursor.getString(cursor.getColumnIndex("Password"));
                                if(account.equals(memailview)&&mima.equals(password1)){
                                    signer = cursor.getString(cursor.getColumnIndex("UserCount"));  //判断是否登录以及登录用户的重要依据！！！
//                                    Head_image = cursor.getBlob(cursor.getColumnIndex("Head_image"));
                                    flag++;
                                    break;
                                }
                            }while (cursor.moveToNext());

                        }
                        cursor.close();
                        if(flag == 1){
                            Toast.makeText(Login_Activity.this,"登录成功",Toast.LENGTH_SHORT).show();
                            //tv1.setText(yhm);
                            //tv2.setText(account);

                            Intent intent = new Intent(Login_Activity.this,MainActivity.class);
                            finish();
                            startActivityForResult(intent,1);
                        }
                        if (flag == 0)Toast.makeText(Login_Activity.this,"账号或密码错误",Toast.LENGTH_SHORT).show();
                    }
                }
            }

        });

        button_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Login_Activity.this,Register_Activity.class);
                startActivity(intent);
            }
        });
    }
}
