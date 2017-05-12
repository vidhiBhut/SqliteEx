package com.example.vidhi.preferenceex;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.databinding.DataBindingUtil;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.LinearSmoothScroller;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.example.vidhi.preferenceex.databinding.ActivityHomePageBinding;
import com.example.vidhi.preferenceex.databinding.ListItemBinding;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.example.vidhi.preferenceex.MainActivity.MyPREFERENCES;


public class HomePageActivity extends AppCompatActivity {

    private static final String TAG = "HomePageActivity";
    private ActivityHomePageBinding activityHomePageBinding;
    SharedPreferences sharedpreferences;
    public static final int REQ_ADD_TASK = 99;
    RecyclerView mRecyclerView;
    LinearLayoutManager mLinearLayoutManager;
    TextView tvListCategory;
    ArrayList<TaskModel> unchecked = new ArrayList<>();
    ArrayList<TaskModel> checked = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityHomePageBinding = DataBindingUtil.setContentView(this, R.layout.activity_home_page);
        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        activityHomePageBinding.setHome(this);

        mRecyclerView = activityHomePageBinding.recyclerView;

        mLinearLayoutManager = new LinearLayoutManager(getApplicationContext());
        mLinearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);


        mRecyclerView.setLayoutManager(mLinearLayoutManager);
        SqlHelper helper = new SqlHelper(this);
        ArrayList<TaskModel> tasks1 = helper.updateView();
        ArrayList<TaskModel> myTask = ordering(tasks1);
        TestAdapter testAdapter = new TestAdapter(myTask);
        mRecyclerView.setAdapter(testAdapter);


        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(mRecyclerView.getContext(),
                mLinearLayoutManager.getOrientation());
        mRecyclerView.addItemDecoration(dividerItemDecoration);


    }

    public ArrayList<TaskModel> ordering(ArrayList<TaskModel> tasks1) {
//        ArrayList<TaskModel> unchecked = new ArrayList<>();
//        ArrayList<TaskModel> checked = new ArrayList<>();

        for (int i = 0; i < tasks1.size(); i++) {
            if (!tasks1.get(i).getStatus()) {
                unchecked.add(tasks1.get(i));
            } else {
                checked.add(tasks1.get(i));
            }
        }

        ArrayList<TaskModel> ordered = new ArrayList<>();
        ordered.addAll(unchecked);
        ordered.addAll(checked);

        return ordered;
    }

    public ArrayList<TaskModel> changeAdapter(ArrayList<TaskModel> tasks2) {
        ArrayList<TaskModel> newList = ordering(tasks2);
        return newList;

    }

    @Override
    protected void onResume() {
        super.onResume();
        SqlHelper helper = new SqlHelper(this);
        ArrayList<TaskModel> tasks = helper.updateView();
        TestAdapter testAdapter = new TestAdapter(tasks);
        mRecyclerView.setAdapter(testAdapter);
    }

    private class TestAdapter extends RecyclerView.Adapter<TestAdapter.TestViewHolder> {

        private ArrayList<TaskModel> myList;

        public TestAdapter(ArrayList<TaskModel> list) {
            this.myList = list;
        }

        @Override
        public TestViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            final View itemView = LayoutInflater.from(
                    parent.getContext()).inflate(R.layout.list_item, parent, false);
            return new TestViewHolder(itemView);
        }

//        @Override
//        public void onViewRecycled(TestViewHolder holder) {
//            super.onViewRecycled(holder);
//            holder.binding.checkbox1.setOnCheckedChangeListener(null);
//        }

        @Override
        public void onBindViewHolder(final TestViewHolder holder, int position) {
            TaskModel task = myList.get(position);
            holder.binding.setTaskModel(task);
            tvListCategory = holder.binding.tvListCategory;
            final CheckBox checkbox1 = holder.binding.checkbox1;
            holder.binding.executePendingBindings();
            checkbox1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    int position = holder.getAdapterPosition();
                    Log.d(TAG, "onCheckedChanged() called with: buttonView = [" + position + "], isChecked = [" + myList.size() + "]");

                    myList.get(position).setStatus(isChecked);
                    SqlHelper helper = new SqlHelper(getApplicationContext());
                    SQLiteDatabase database = helper.getWritableDatabase();
                    ContentValues content = new ContentValues();
                    content.put(SqlHelper.COLUMN_STATUS, checkbox1.isChecked() ? 1 : 0);
                    int update = database.update(SqlHelper.TABLE_TASK, content, SqlHelper.COLUMN_ID + " = " + myList.get(position).getId(), null);
                    Log.d(TAG, "onCheckedChanged: update count: " + update);


                    if (isChecked) {
                        myList.add(myList.get(position));
                        myList.remove(position);
                        notifyItemMoved(position, myList.size() - 1);
                    } else {
                        myList.add(0, myList.get(position));
                        myList.remove(position);
                        notifyItemMoved(position, 0);
                    }

                }
            });
        }

        @Override
        public int getItemCount() {
            return myList.size();
        }

        public class TestViewHolder extends RecyclerView.ViewHolder {
            ListItemBinding binding;

            public TestViewHolder(View itemView) {
                super(itemView);
                binding = DataBindingUtil.bind(itemView);
            }
        }
    }//testAdapter


    public void signOut() {
        Intent i = new Intent(HomePageActivity.this, MainActivity.class);
        startActivity(i);
        finish();
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.clear();
        editor.apply();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == REQ_ADD_TASK) {
            if (resultCode == Activity.RESULT_OK) {
                SqlHelper helper = new SqlHelper(this);
                ArrayList<TaskModel> tasks = helper.updateView();
                TestAdapter testAdapter = new TestAdapter(tasks);
                mRecyclerView.setAdapter(testAdapter);
            }
            if (resultCode == Activity.RESULT_CANCELED) {

            }
        }
    }

    public void addTask() {
        Intent intent = new Intent(HomePageActivity.this, AddTaskActivity.class);
        startActivityForResult(intent, REQ_ADD_TASK);
    }


}








