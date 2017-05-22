package com.example.vidhi.preferenceex;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.vidhi.preferenceex.databinding.ActivityAddTaskBinding;

import java.util.ArrayList;
import java.util.List;

import static com.example.vidhi.preferenceex.LoginActivity.ID;
import static com.example.vidhi.preferenceex.LoginActivity.MyPREFERENCES1;
import static com.example.vidhi.preferenceex.LoginActivity.USER_NAME;

public class AddTaskActivity extends AppCompatActivity {

    Button btn_done, btn_add_list,btn_view_list;
    EditText et_add_task;
    String task;
    Spinner spinner;
    List<ListModel> labels;
    TaskModel taskModel = new TaskModel();
    int myId,userId;
    List<String > labels1=new ArrayList<String>();
    ActivityAddTaskBinding activityAddTaskBinding;
    public static final int REQ_CODE = 100;
    private static final String TAG = "AddTaskActivity";
    SharedPreferences sharedpreferences;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task);

        sharedpreferences = getSharedPreferences(MyPREFERENCES1, Context.MODE_PRIVATE);
        String name=sharedpreferences.getString(USER_NAME,"vidhi");
        Log.e("name",name);
        if (!name.equals("")){
            userId=sharedpreferences.getInt(ID,2);
            Log.e(TAG,"uid"+userId);
        }

        et_add_task = (EditText) findViewById(R.id.et_add_task);
        btn_done = (Button) findViewById(R.id.btn_done);
        btn_add_list = (Button) findViewById(R.id.btn_add_list);
        btn_view_list=(Button)findViewById(R.id.btn_view_list);
        spinner = (Spinner) findViewById(R.id.spinner_list);

        ActionBar ab = getSupportActionBar();
        ab.setHomeAsUpIndicator(R.drawable.ic_keyboard_arrow_left_white_36dp);
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setTitle("Add Task");

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                ListModel model=labels.get(position);
                myId=model.getListId();
                TaskModel taskModel=new TaskModel();
                taskModel.setListCategory(myId);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        loadSpinnerData();


        btn_done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (et_add_task.getText().toString().equals("") || et_add_task.getText().toString().equals(null)) {
                    Toast.makeText(getApplicationContext(), "Please enter task", Toast.LENGTH_SHORT).show();
                } else {
                    task = et_add_task.getText().toString();
                    taskModel.setTask(task);
                    taskModel.setListCategory(myId);
                    taskModel.setUserId(userId);
                    SqlHelper sqlHelper = new SqlHelper(AddTaskActivity.this);
                    sqlHelper.insertData(taskModel);
                    setResult(RESULT_OK);
                    finish();
                }
            }
        });

        btn_add_list.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AddTaskActivity.this, AddListActivity.class);
                startActivityForResult(intent, REQ_CODE);

            }
        });

        btn_view_list.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(AddTaskActivity.this,ViewListActivity.class);
                startActivity(intent);
            }
        });
    }



    @Override
    protected void onResume() {
        super.onResume();
        loadSpinnerData();
    }

    public void loadSpinnerData(){
       SqlHelper db = new SqlHelper(getApplicationContext());
        labels = db.getAllLabels(userId);
        labels1.clear();
        for (int i=0; i<labels.size();i++){
            String myList=labels.get(i).getList();
            labels1.add(myList);
        }
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
               android.R.layout.simple_spinner_item, labels1);
       dataAdapter
               .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

       spinner.setAdapter(dataAdapter);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
