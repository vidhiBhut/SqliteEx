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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityHomePageBinding = DataBindingUtil.setContentView(this, R.layout.activity_home_page);
        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        activityHomePageBinding.setHome(this);

        mRecyclerView = activityHomePageBinding.recyclerView;

        mLinearLayoutManager = new LinearLayoutManager(getApplicationContext());
        mLinearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mLinearLayoutManager.scrollToPositionWithOffset(2, 20);

        mRecyclerView.setLayoutManager(mLinearLayoutManager);
        SqlHelper helper=new SqlHelper(this);
        ArrayList<TaskModel> tasks=helper.updateView();
        TestAdapter testAdapter = new TestAdapter(tasks);
        mRecyclerView.setAdapter(testAdapter);

//        ItemTouchHelper.Callback callback = new MovieTouchHelper(testAdapter);
//        ItemTouchHelper helper1 = new ItemTouchHelper(callback);
//        helper1.attachToRecyclerView(mRecyclerView);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(mRecyclerView.getContext(),
                mLinearLayoutManager.getOrientation());
        mRecyclerView.addItemDecoration(dividerItemDecoration);


    }

    @Override
    protected void onResume() {
        super.onResume();
        SqlHelper helper=new SqlHelper(this);
        ArrayList<TaskModel> tasks=helper.updateView();
        TestAdapter testAdapter = new TestAdapter(tasks);
        mRecyclerView.setAdapter(testAdapter);
    }

//    public class MovieTouchHelper extends ItemTouchHelper.SimpleCallback {
//        TestAdapter recycleAdapter;
//
//
//        public MovieTouchHelper(TestAdapter recycleAdapter) {
//            super(ItemTouchHelper.UP | ItemTouchHelper.DOWN, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT);
////            this.recycleAdapter = recycleAdapter;
//        }
//        @Override
//        public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
//            recycleAdapter.swap(viewHolder.getAdapterPosition(), target.getAdapterPosition());
//            return true;
//        }
//
//        @Override
//        public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
////            recycleAdapter.remove(viewHolder.getAdapterPosition());
//        }
//    }

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
        public void swap(int firstPosition, int secondPosition)
        {
            Collections.swap(myList, firstPosition, secondPosition);
            notifyItemMoved(firstPosition, secondPosition);
            notifyItemRangeChanged(firstPosition,secondPosition);
        }




        @Override
        public void onBindViewHolder(final TestViewHolder holder, final int position) {
            final TaskModel item=myList.get(position);
            final TaskModel task = myList.get(position);
            holder.binding.setTaskModel(task);
            tvListCategory=holder.binding.tvListCategory;
            final CheckBox checkbox1 = holder.binding.checkbox1;
            checkbox1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    task.setStatus(checkbox1.isChecked());
                    SqlHelper helper = new SqlHelper(getApplicationContext());
                    SQLiteDatabase database = helper.getWritableDatabase();
                    ContentValues content = new ContentValues();
                    content.put(SqlHelper.COLUMN_STATUS, checkbox1.isChecked() ? 1 : 0);
                    int update = database.update(SqlHelper.TABLE_TASK, content, SqlHelper.COLUMN_ID + " = " + task.getId(), null);
                    Log.d(TAG, "onCheckedChanged: update count: " + update);
                    remove(position);
                    add(item,position);
//                    swap(position,myList.size()-1);
                }
            });


        }
        public void remove(int position) {
            myList.remove(position);
            notifyItemRemoved(position);
        }

        public void add(TaskModel item1, int position) {
            myList.add(position, item1);
            notifyItemInserted(position);
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
                SqlHelper helper=new SqlHelper(this);
                ArrayList<TaskModel> tasks=helper.updateView();
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








