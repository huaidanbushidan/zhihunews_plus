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

public class Edit_PasswordActivity extends AppCompatActivity {
    private MyDatabaseHelper dbHelper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit__password);
        EditText old = findViewById(R.id.old);
        EditText new1 = findViewById(R.id.new1);
        EditText new2 = findViewById(R.id.new2);
        Button sure = findViewById(R.id.sure);
        dbHelper = new MyDatabaseHelper(this);

        sure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText old = findViewById(R.id.old);
                EditText new1 = findViewById(R.id.new1);
                EditText new2 = findViewById(R.id.new2);
                String oldpassword = old.getText().toString();
                String newpassword1 = new1.getText().toString();
                String newpassword2 = new2.getText().toString();
                if (newpassword1.equals("") | newpassword2.equals("")) {
                    Toast.makeText(Edit_PasswordActivity.this, "密码不能为空", Toast.LENGTH_LONG).show();
                } else {
                    SQLiteDatabase db = dbHelper.getWritableDatabase();
                    Cursor cursor = db.query("User", new String[]{"Password"}, "UserCount = ?", new String[]{Login_Activity.signer}, null, null, null);
                    if (cursor.moveToFirst()) {
                        do {
                            String password = cursor.getString(cursor.getColumnIndex("Password"));
                            if (password.equals(oldpassword)) {
                                if (newpassword1.equals(newpassword2)) {
                                    ContentValues values = new ContentValues();
                                    values.put("Password", newpassword1);
                                    db.update("User", values, "UserCount = ?", new String[]{Login_Activity.signer});
                                    values.clear();
                                    Toast.makeText(Edit_PasswordActivity.this, "修改成功", Toast.LENGTH_LONG).show();
                                    Intent intent = new Intent(Edit_PasswordActivity.this, Edit_informationActivity.class);
                                    startActivity(intent);
                                    break;
                                } else {
                                    Toast.makeText(Edit_PasswordActivity.this, "两次输入密码不一致", Toast.LENGTH_LONG).show();
                                    break;
                                }
                            } else {
                                Toast.makeText(Edit_PasswordActivity.this, "原密码错误", Toast.LENGTH_LONG).show();
                                break;
                            }
                        } while (cursor.moveToNext());
                    }

                }
            }
        });

    }
}
