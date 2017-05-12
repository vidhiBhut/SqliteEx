package com.example.vidhi.preferenceex;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabaseLockedException;
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

import com.example.vidhi.preferenceex.databinding.ListItemBinding;
import com.example.vidhi.preferenceex.databinding.ListViewItemBinding;

import java.util.ArrayList;

public class ViewListActivity extends AppCompatActivity {

    RecyclerView rec_view_list;
    LinearLayoutManager mLinearLayoutManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_list);

        ActionBar ab = getSupportActionBar();
        ab.setHomeAsUpIndicator(R.drawable.ic_keyboard_arrow_left_white_36dp);
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setTitle("View List");

        rec_view_list=(RecyclerView)findViewById(R.id.rec_view_list);

        mLinearLayoutManager = new LinearLayoutManager(getApplicationContext());
        mLinearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        rec_view_list.setLayoutManager(mLinearLayoutManager);

        SqlHelper helper=new SqlHelper(this);
        ArrayList<ListModel> listOfView= (ArrayList<ListModel>) helper.getAllLabels();
        ListAdapter listAdapter=new ListAdapter(listOfView);
        rec_view_list.setAdapter(listAdapter);


        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(rec_view_list.getContext(),
                mLinearLayoutManager.getOrientation());
        rec_view_list.addItemDecoration(dividerItemDecoration);

    }

    private class ListAdapter extends RecyclerView.Adapter<ViewListActivity.ListAdapter.ListViewHolder> {

        private ArrayList<ListModel> myList;

        public ListAdapter(ArrayList<ListModel> list) {
            this.myList = list;
        }

        @Override
        public ListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            final View itemView = LayoutInflater.from(
                    parent.getContext()).inflate(R.layout.list_view_item, parent, false);
            return new ListViewHolder(itemView);
        }



        @Override
        public void onBindViewHolder(final ViewListActivity.ListAdapter.ListViewHolder holder, final int position) {
            final ListModel list = myList.get(position);
            holder.binding.setListModel(list);
            final int id=list.getListId();
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent=new Intent(ViewListActivity.this,TaskOfListActivity.class);
                    intent.putExtra("id",id);
                    startActivity(intent);
                }
            });
        }
        @Override
        public int getItemCount() {
            return myList.size();
        }

        public class ListViewHolder extends RecyclerView.ViewHolder {
            ListViewItemBinding binding;
            public ListViewHolder(View itemView) {
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
