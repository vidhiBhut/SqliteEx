package com.example.vidhi.preferenceex;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Created by vidhi on 5/5/2017.
 */

public class SqlHelper extends SQLiteOpenHelper {

    public static final String DB_NAME = "MyData.db";
    public static final int DB_VERSION = 21;

    public static final String TABLE_TASK = "taskTable";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_TASK = "task";
    public static final String COLUMN_STATUS = "status";
    public static final String COLUMN_LIST = "listCategory";

    public static final String TABLE_LIST = "listTable";
    public static final String COLUMN_LIST_ID = "listId";
    public static final String COLUMN_LIST_NAME = "list";

    String listCat;


    public SqlHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        // TODO Auto-generated constructor stub
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CreateTable = "CREATE TABLE " + TABLE_TASK
                + " ( " + COLUMN_ID + " INTEGER PRIMARY KEY, "
                + COLUMN_TASK + " TEXT ,"
                + COLUMN_STATUS + " INTEGER " + "DEFAULT '0',"
                + COLUMN_LIST + " INTEGER " + " );";
        // TODO Auto-generated method stub
        String CreateListTable = "CREATE TABLE "
                + TABLE_LIST + " ( "
                + COLUMN_LIST_ID + " INTEGER PRIMARY KEY, "
                + COLUMN_LIST_NAME + " TEXT " + " );";
        db.execSQL(CreateTable);
        db.execSQL(CreateListTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO Auto-generated method stub
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TASK);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_LIST);
        onCreate(db);
    }

    public void insertData(TaskModel taskModel) {
        SQLiteDatabase db = getWritableDatabase();
        db.beginTransaction();
        try {
            ContentValues contentValues = new ContentValues();
            contentValues.put(SqlHelper.COLUMN_TASK, taskModel.getTask());
            contentValues.put(SqlHelper.COLUMN_LIST, taskModel.getListCategory());
            db.insert(SqlHelper.TABLE_TASK, null, contentValues);
            db.setTransactionSuccessful();
        } catch (Exception e) {
            Log.d("insertData: ", "");
        } finally {
            db.endTransaction();
        }

    }


    public void insertList(ListModel listModel) {
        SQLiteDatabase db = getWritableDatabase();
        db.beginTransaction();
        try {
            ContentValues contentValues = new ContentValues();
            contentValues.put(COLUMN_LIST_NAME, listModel.getList());
            db.insert(TABLE_LIST, null, contentValues);
            db.setTransactionSuccessful();
        } catch (Exception e) {

        } finally {
            db.endTransaction();
        }
    }

    public ArrayList<TaskModel> updateView() {
        ArrayList<TaskModel> taskList = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(TABLE_TASK,
                new String[]{COLUMN_ID, COLUMN_TASK, COLUMN_STATUS, COLUMN_LIST},
                null, null, null, null, null);
        int idIdx = cursor.getColumnIndex(COLUMN_ID);
        int nameIdx = cursor.getColumnIndex(COLUMN_TASK);
        int statusIdx = cursor.getColumnIndex(COLUMN_STATUS);
        int listCatIdx=cursor.getColumnIndex(COLUMN_LIST);//listId index from taskTable

        while (cursor.moveToNext()) {
            int id = cursor.getInt(idIdx);
            String task = cursor.getString(nameIdx);
            int status = cursor.getInt(statusIdx);
            int listCategory = cursor.getInt(listCatIdx);// listId value from taskTable
            Cursor c=db.rawQuery(" SELECT * FROM " + TABLE_LIST + " WHERE " + COLUMN_LIST_ID + " = " + listCategory,null);

            if (c.moveToFirst()) {
                listCat = c.getString(c.getColumnIndex(COLUMN_LIST_NAME));//listName from listTable
                Log.e("cat",listCat);
            }

            TaskModel taskModel = new TaskModel();
            taskModel.setId(id);
            taskModel.setTask(task);
            taskModel.setStatus(status == 1);
            taskModel.setListCat(listCat);
            taskList.add(taskModel);
        }
        return taskList;
    }



    public List<ListModel> getAllLabels(){
        List<ListModel> labels = new ArrayList<ListModel>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_LIST,
                new String[]{COLUMN_LIST_ID, COLUMN_LIST_NAME},
                null, null, null, null, null);
        int listIdIdx=cursor.getColumnIndex(COLUMN_LIST_ID);
        int listNameIdx=cursor.getColumnIndex(COLUMN_LIST_NAME);

        while (cursor.moveToNext()) {
            int listId = cursor.getInt(listIdIdx);
            String listName = cursor.getString(listNameIdx);

            ListModel listModel = new ListModel();
            listModel.setListId(listId);
            listModel.setList(listName);

            labels.add(listModel);
        }
        return labels;

    }

}
