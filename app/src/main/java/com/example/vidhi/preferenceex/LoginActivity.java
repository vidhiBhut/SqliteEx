package com.example.vidhi.preferenceex;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.lang.reflect.Array;


public class LoginActivity extends AppCompatActivity {

    EditText etEmail;
    Button btnLogin, btnSignUp;
    private static final String TAG = "LoginActivity";
    SharedPreferences sharedpreferences;
    public static final String MyPREFERENCES1 = "MyPrefs1";
    public static final String ID = "idKey";
    public static final String USER_NAME = "namekey";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etEmail = (EditText) findViewById(R.id.et_email);
        btnLogin = (Button) findViewById(R.id.btn_login);
        btnSignUp = (Button) findViewById(R.id.btn_sign_up);
        sharedpreferences = getSharedPreferences(MyPREFERENCES1, Context.MODE_PRIVATE);
        String name = sharedpreferences.getString(USER_NAME, "");
        if (!name.equals("")) {
            Intent intent = new Intent(LoginActivity.this, HomePageActivity.class);
            startActivity(intent);
            finish();
        } else {
            btnLogin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String email = etEmail.getText().toString();
                    if (etEmail.getText().toString().equals("") || etEmail.getText().toString().equals(null)) {
                        Toast.makeText(getApplicationContext(), "Please enter email", Toast.LENGTH_SHORT).show();
                    } else {
                        SqlHelper sqlHelper = new SqlHelper(LoginActivity.this);
                        UserModel user;
                        user = sqlHelper.logIn(email);

                        if (user.getId() == 0) {

                            Toast.makeText(getApplicationContext(), "Invalid email", Toast.LENGTH_SHORT).show();
                        } else {
                            Intent intent = new Intent(LoginActivity.this, HomePageActivity.class);
                            SharedPreferences.Editor editor = sharedpreferences.edit();
                            int id = user.getId();
                            String name = user.getName();
                            editor.putInt(ID, id);
                            editor.putString(USER_NAME, name);
                            editor.commit();
                            startActivity(intent);
                            finish();
                        }


                    }
                }
            });

            btnSignUp.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }
            });
        }
    }
}
