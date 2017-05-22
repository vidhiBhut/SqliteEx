package com.example.vidhi.preferenceex;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.databinding.DataBindingUtil;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.vidhi.preferenceex.databinding.ActivityMainBinding;

import java.util.ArrayList;
import java.util.StringTokenizer;

import static com.example.vidhi.preferenceex.LoginActivity.ID;
import static com.example.vidhi.preferenceex.LoginActivity.MyPREFERENCES1;
import static com.example.vidhi.preferenceex.LoginActivity.USER_NAME;
import static com.example.vidhi.preferenceex.SqlHelper.COLUMN_USER_EMAIL;
import static com.example.vidhi.preferenceex.SqlHelper.TABLE_TASK;
import static com.example.vidhi.preferenceex.SqlHelper.TABLE_USER;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding activityMainBinding;
    public static final String MyPREFERENCES = "MyPrefs";
    public static final String NAME = "nameKey";
    public static final String PHONE = "phoneKey";
    public static final String EMAIL = "emailKey";

    SharedPreferences sharedpreferences;
    EditText et_name, et_email, et_phone;
    Button btn_continue;
    String MobilePattern = "[0-9]{10}";
    String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
    UserModel userModel=new UserModel();
    int userId;
    String userName,email;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityMainBinding= DataBindingUtil.setContentView(this,R.layout.activity_main);
        et_email=activityMainBinding.etEmail;
        et_name = (EditText) findViewById(R.id.et_name);
        et_email = (EditText) findViewById(R.id.et_email);
        et_phone = (EditText) findViewById(R.id.et_phone);
        btn_continue = (Button) findViewById(R.id.btn_continue);
        sharedpreferences = getSharedPreferences(MyPREFERENCES1, Context.MODE_PRIVATE);
        String name = sharedpreferences.getString(USER_NAME, "");
        if (!name.equals("")) {
            Intent intent = new Intent(MainActivity.this, HomePageActivity.class);
            startActivity(intent);
            finish();

        } else {

            btn_continue.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String n = et_name.getText().toString();
                    String e = et_email.getText().toString();
                    String p = et_phone.getText().toString();
//
//                    SharedPreferences.Editor editor = sharedpreferences.edit();
//
//                    editor.putString(NAME, n);
//                    editor.putString(EMAIL, e);
//                    editor.putString(PHONE, p);
//                    editor.commit();




                if (et_name.getText().toString().equals("")||et_name.getText().toString().equals(null)){
                    Toast.makeText(getApplicationContext(), "Please enter name", Toast.LENGTH_SHORT).show();
                }
               else if (et_email.getText().toString().equals("")||et_email.getText().toString().equals(null)){
                    Toast.makeText(getApplicationContext(), "Please enter Email Id", Toast.LENGTH_SHORT).show();
                }
                else if(!et_email.getText().toString().matches(emailPattern))
                {
                    Toast.makeText(getApplicationContext(), "Please enter valid email id", Toast.LENGTH_SHORT).show();
                }
                else if (et_phone.getText().toString().equals("")||et_phone.getText().toString().equals(null)){
                    Toast.makeText(getApplicationContext(), "Please enter Phone No.", Toast.LENGTH_SHORT).show();
                }
                else if(!et_phone.getText().toString().matches(MobilePattern))
                {
                    Toast.makeText(getApplicationContext(), "Please enter valid 10 digit phone number", Toast.LENGTH_SHORT).show();
                }
                else {
//                    Intent intent = new Intent(MainActivity.this, HomePageActivity.class);
////                    intent.putExtra("name", n);
//                    startActivity(intent);
//                    finish();

                    userModel.setName(n);
                    userModel.setEmail(e);
                    SqlHelper sqlHelper = new SqlHelper(MainActivity.this);
                    SQLiteDatabase db = sqlHelper.getReadableDatabase();
                    Cursor c = db.rawQuery(" SELECT " + COLUMN_USER_EMAIL + " FROM " + TABLE_USER + " WHERE "
                            + COLUMN_USER_EMAIL + " =?", new String[]{e});
                    if (c.getCount() > 0) {
                        Toast.makeText(getApplicationContext(), "User already registered", Toast.LENGTH_SHORT).show();
                    } else {
                        sqlHelper.signUp(userModel);

                        UserModel user = sqlHelper.logIn(e);
                        userId = user.getId();
                        userName = user.getName();
                        SharedPreferences.Editor editor = sharedpreferences.edit();
                        editor.putInt(ID, userId);
                        editor.putString(USER_NAME, userName);
                        editor.commit();
                        Intent intent = new Intent(MainActivity.this, HomePageActivity.class);
                        startActivity(intent);
                        finish();
                    }
                }

                }
            });
        }
    }
}
