package com.example.a32936.zhihunews.SQLiteDatabase;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.a32936.zhihunews.R;

import java.sql.Blob;

public class Register_Activity extends AppCompatActivity {
    private MyDatabaseHelper dbHelper;
    int flag;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_);
        dbHelper = new MyDatabaseHelper(this);

        Button buttonregister = findViewById(R.id.Register);



        buttonregister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText editTextUsername = findViewById(R.id.UserName);
                EditText editTextUsercount = findViewById(R.id.UserCount);
                EditText editTextUserpassword = findViewById(R.id.UserPassword);
                SQLiteDatabase db = dbHelper.getWritableDatabase();
                String yonghuming = editTextUsername.getText().toString();
                String zhanghao = editTextUsercount.getText().toString();
                String mima = editTextUserpassword.getText().toString();
                flag = 0;
                if(yonghuming.equals("")){Toast.makeText(Register_Activity.this,"用户名不能为空",Toast.LENGTH_SHORT).show();}
                else {
                    if(zhanghao.equals("")){Toast.makeText(Register_Activity.this,"账号不能为空",Toast.LENGTH_SHORT).show();}
                    else {
                        if (mima.equals("")){Toast.makeText(Register_Activity.this,"密码不能为空",Toast.LENGTH_SHORT).show();}
                        else {
                            String account = null;
                            Cursor cursor = db.query("User",new String[]{"UserCount","Password"},null,null,null,null,"id");
                            if (cursor.moveToFirst()){
                                do {
                                    account = cursor.getString(cursor.getColumnIndex("UserCount"));
                                    if(account.equals(zhanghao)){
                                        Toast.makeText(Register_Activity.this,"非常抱歉，账号已存在",Toast.LENGTH_SHORT).show();
                                        flag++;
                                        break;
                                    }
                                }while (cursor.moveToNext());

                            }
                            cursor.close();
                            if (flag == 0){
                                ContentValues values = new ContentValues();
                                values.put("UserName", yonghuming);
                                values.put("UserCount",zhanghao);
                                values.put("Password",mima);
                                String s = "1";
                                byte[] b = s.getBytes();
                                values.put("Head_image",b);
                                db.insert("User", null, values);
                                values.clear();		//清掉现有的内容
                                db.close();

                                Toast.makeText(Register_Activity.this,"注册成功",Toast.LENGTH_LONG).show();
                                Intent intent = new Intent(Register_Activity.this,Login_Activity.class);
                                finish();
                                startActivity(intent);}
                        }}}}

        });
    }
}
