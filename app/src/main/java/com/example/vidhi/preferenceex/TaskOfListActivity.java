package com.example.vidhi.preferenceex;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.databinding.DataBindingUtil;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.example.vidhi.preferenceex.databinding.ListViewItemBinding;
import com.example.vidhi.preferenceex.databinding.TaskItemBinding;

import java.util.ArrayList;

public class TaskOfListActivity extends AppCompatActivity {

    private static final String TAG = "TaskOfListActivity";
    RecyclerView mRecyclerView;
    LinearLayoutManager mLinearLayoutManager;
    ArrayList<TaskModel> myList = new ArrayList<>();
    String name;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_of_list);

        mRecyclerView=(RecyclerView)findViewById(R.id.rec_task);

        mLinearLayoutManager = new LinearLayoutManager(getApplicationContext());
        mLinearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        mRecyclerView.setLayoutManager(mLinearLayoutManager);

        Intent intent=getIntent();
        int id= intent.getIntExtra("id",9);
        SqlHelper sqlHelper=new SqlHelper(getApplicationContext());
        SQLiteDatabase db=sqlHelper.getReadableDatabase();
        Cursor c=db.rawQuery(" SELECT * FROM " + SqlHelper.TABLE_TASK + " WHERE " + SqlHelper.COLUMN_LIST + " = " + id,null);

        while (c.moveToNext()){
            TaskModel taskName=new TaskModel();
            String name=c.getString(c.getColumnIndex(SqlHelper.COLUMN_TASK));
            int status=c.getInt(c.getColumnIndex(SqlHelper.COLUMN_STATUS));
            int taskId=c.getInt(c.getColumnIndex(SqlHelper.COLUMN_ID));
            taskName.setId(taskId);
            taskName.setTask(name);
            taskName.setStatus(status == 1 ? true : false);
            myList.add(taskName);
//                Log.e("task",taskName);
        }
        TaskAdapter taskAdapter=new TaskAdapter(myList);
        mRecyclerView.setAdapter(taskAdapter);


        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(mRecyclerView.getContext(),
                mLinearLayoutManager.getOrientation());
        mRecyclerView.addItemDecoration(dividerItemDecoration);

        Cursor c1=db.rawQuery(" SELECT * FROM " + SqlHelper.TABLE_LIST + " WHERE " + SqlHelper.COLUMN_LIST_ID + " = " + id,null);
        Log.e(TAG, "onCreate: "+id );
       if (c1.moveToFirst()) {
           name = c1.getString(c1.getColumnIndex(SqlHelper.COLUMN_LIST_NAME));
           Log.e("name",name);
       }
        ActionBar ab = getSupportActionBar();
        ab.setHomeAsUpIndicator(R.drawable.ic_keyboard_arrow_left_white_36dp);
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setTitle("Task of "+name);


    }

    public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskHolder> {
        public ArrayList<TaskModel> taskList;

        public TaskAdapter(ArrayList<TaskModel> list) {
            this.taskList = list;
        }

        @Override
        public TaskAdapter.TaskHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            final View itemView = LayoutInflater.from(
                    parent.getContext()).inflate(R.layout.task_item, parent, false);
            return new TaskHolder(itemView);
        }

        @Override
        public void onBindViewHolder(final TaskHolder holder, final int position) {

            final TaskModel task = taskList.get(position);
            holder.binding.setTaskModel(task);
            final CheckBox cbTask=holder.binding.cbTaskItem;;
            cbTask.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    task.setStatus(cbTask.isChecked());
                    SqlHelper helper = new SqlHelper(getApplicationContext());
                    SQLiteDatabase database = helper.getWritableDatabase();
                    ContentValues content = new ContentValues();
                    content.put(SqlHelper.COLUMN_STATUS, cbTask.isChecked() ? 1 : 0);
                    int update = database.update(SqlHelper.TABLE_TASK, content, SqlHelper.COLUMN_ID + " = " + task.getId(), null);
                    Log.d(TAG, "onCheckedChanged: update count: " + update);
                }
            });
        }
        @Override
        public int getItemCount() {
            return taskList.size();
        }


        public class TaskHolder extends RecyclerView.ViewHolder {
            TaskItemBinding binding;

            public TaskHolder(View itemView) {
                super(itemView);
                binding = DataBindingUtil.bind(itemView);



            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}

