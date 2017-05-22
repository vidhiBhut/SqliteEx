package com.example.vidhi.preferenceex;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import static com.example.vidhi.preferenceex.LoginActivity.ID;
import static com.example.vidhi.preferenceex.LoginActivity.MyPREFERENCES1;
import static com.example.vidhi.preferenceex.LoginActivity.USER_NAME;

public class AddListActivity extends AppCompatActivity {

    EditText et_add_list;
    Button btn_list_done;
    String list;
    SharedPreferences sharedpreferences;
    int userId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_list);

        sharedpreferences = getSharedPreferences(MyPREFERENCES1, Context.MODE_PRIVATE);
        String name=sharedpreferences.getString(USER_NAME,"vidhi");
        Log.e("name",name);
        if (!name.equals("")){
            userId=sharedpreferences.getInt(ID,2);

        }

        ActionBar ab = getSupportActionBar();
        ab.setHomeAsUpIndicator(R.drawable.ic_keyboard_arrow_left_white_36dp);
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setTitle("Add List");

        btn_list_done=(Button)findViewById(R.id.btn_list_done);
        et_add_list=(EditText)findViewById(R.id.et_add_list);

        btn_list_done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                list = et_add_list.getText().toString();
                if (et_add_list.getText().toString().equals("") || et_add_list.getText().toString().equals(null)) {
                    Toast.makeText(getApplicationContext(), "Please enter list", Toast.LENGTH_SHORT).show();
                } else {
                    ListModel listModel = new ListModel();
                    listModel.setList(list);
                    listModel.setUserId(userId);
                    SqlHelper sqlHelper = new SqlHelper(AddListActivity.this);
                    sqlHelper.insertList(listModel);
                    setResult(RESULT_OK);
                    finish();
                }
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
