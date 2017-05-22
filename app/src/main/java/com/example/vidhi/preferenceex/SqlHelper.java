package com.example.vidhi.preferenceex;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.example.vidhi.preferenceex.LoginActivity.ID;
import static com.example.vidhi.preferenceex.LoginActivity.MyPREFERENCES1;
import static com.example.vidhi.preferenceex.LoginActivity.USER_NAME;

/**
 * Created by vidhi on 5/5/2017.
 */

public class SqlHelper extends SQLiteOpenHelper {

    public static final String DB_NAME = "MyData.db";
    public static final int DB_VERSION = 33;

    public static final String TABLE_TASK = "taskTable";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_TASK = "task";
    public static final String COLUMN_STATUS = "status";
    public static final String COLUMN_LIST = "listCategory";
    public static final String COLUMN_TASK_USER_ID = "user_id";

    public static final String TABLE_LIST = "listTable";
    public static final String COLUMN_LIST_ID = "listId";
    public static final String COLUMN_LIST_NAME = "list";
    public static final String COLUMN_LIST_USER_ID = "user_id";

    public static final String TABLE_USER = "user";
    public static final String COLUMN_USER_ID = "id";
    public static final String COLUMN_USER_NAME = "name";
    public static final String COLUMN_USER_EMAIL = "email";


    String listCat;
    SharedPreferences sharedpreferences;


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
                + COLUMN_TASK_USER_ID + " INTEGER ,"
                + COLUMN_LIST + " INTEGER " + " );";
        // TODO Auto-generated method stub

        String CreateListTable = "CREATE TABLE "
                + TABLE_LIST + " ( "
                + COLUMN_LIST_ID + " INTEGER PRIMARY KEY, "
                + COLUMN_LIST_USER_ID + " INTEGER ,"
                + COLUMN_LIST_NAME + " TEXT " + " );";

        String CreateUserTable = "CREATE TABLE " + TABLE_USER
                + " ( " + COLUMN_USER_ID + " INTEGER PRIMARY KEY, "
                + COLUMN_USER_NAME + " TEXT ,"
                + COLUMN_USER_EMAIL + " TEXT " + " );";
        db.execSQL(CreateTable);
        db.execSQL(CreateListTable);
        db.execSQL(CreateUserTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO Auto-generated method stub
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TASK);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_LIST);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER);
        onCreate(db);
    }

    public void insertData(TaskModel taskModel) {
        SQLiteDatabase db = getWritableDatabase();
        db.beginTransaction();
        try {
            ContentValues contentValues = new ContentValues();
            contentValues.put(SqlHelper.COLUMN_TASK, taskModel.getTask());
            contentValues.put(SqlHelper.COLUMN_LIST, taskModel.getListCategory());
            contentValues.put(SqlHelper.COLUMN_TASK_USER_ID,taskModel.getUserId());
            db.insert(SqlHelper.TABLE_TASK, null, contentValues);
            db.setTransactionSuccessful();
        } catch (Exception e) {
            Log.d("insertData: ", "");
        } finally {
            db.endTransaction();
        }

    }

    public void signUp(UserModel userModel) {
        SQLiteDatabase db = getWritableDatabase();
        db.beginTransaction();
        try {
            ContentValues contentValues = new ContentValues();
            contentValues.put(SqlHelper.COLUMN_USER_NAME, userModel.getName());
            contentValues.put(SqlHelper.COLUMN_USER_EMAIL, userModel.getEmail());
            db.insert(SqlHelper.TABLE_USER, null, contentValues);
            db.setTransactionSuccessful();
        } catch (Exception e) {
            Log.d("insertData: ", "");
        } finally {
            db.endTransaction();
        }

    }

    public UserModel logIn(String email) {
        UserModel userModel = new UserModel();
        SQLiteDatabase db = getReadableDatabase();
       Cursor c = db.rawQuery(" SELECT " + COLUMN_USER_ID + " , " + COLUMN_USER_NAME + " FROM " + TABLE_USER + " WHERE "
                + COLUMN_USER_EMAIL + " =?", new String[]{email});
//        Cursor c = db.rawQuery(" SELECT * FROM " + TABLE_USER + " WHERE " + COLUMN_USER_EMAIL + " = " + email, null);

        while (c.moveToNext()) {

            int userId = c.getInt(c.getColumnIndex(COLUMN_USER_ID));
            String userName = c.getString(c.getColumnIndex(COLUMN_USER_NAME));

            userModel.setId(userId);
            userModel.setName(userName);
        }
        return userModel;


    }


    public void insertList(ListModel listModel) {
        SQLiteDatabase db = getWritableDatabase();
        db.beginTransaction();
        try {
            ContentValues contentValues = new ContentValues();
            contentValues.put(COLUMN_LIST_NAME, listModel.getList());
            contentValues.put(COLUMN_LIST_USER_ID,listModel.getUserId());
            db.insert(TABLE_LIST, null, contentValues);
            db.setTransactionSuccessful();
        } catch (Exception e) {

        } finally {
            db.endTransaction();
        }
    }

    public ArrayList<TaskModel> updateView(int userId) {
        ArrayList<TaskModel> taskList = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
//        Cursor cursor = db.query(TABLE_TASK,
//                new String[]{COLUMN_ID, COLUMN_TASK, COLUMN_STATUS, COLUMN_LIST},
//                null, null, null, null, null);
        Cursor cursor = db.rawQuery(" SELECT * FROM " + TABLE_TASK + " WHERE " + COLUMN_TASK_USER_ID + " = " + userId, null);
        int idIdx = cursor.getColumnIndex(COLUMN_ID);
        int nameIdx = cursor.getColumnIndex(COLUMN_TASK);
        int statusIdx = cursor.getColumnIndex(COLUMN_STATUS);
        int listCatIdx = cursor.getColumnIndex(COLUMN_LIST);//listId index from taskTable

        while (cursor.moveToNext()) {
            int id = cursor.getInt(idIdx);
            String task = cursor.getString(nameIdx);
            int status = cursor.getInt(statusIdx);
            int listCategory = cursor.getInt(listCatIdx);// listId value from taskTable
            Cursor c = db.rawQuery(" SELECT * FROM " + TABLE_LIST + " WHERE " + COLUMN_LIST_ID + " = " + listCategory, null);

            if (c.moveToFirst()) {
                listCat = c.getString(c.getColumnIndex(COLUMN_LIST_NAME));//listName from listTable
                Log.e("cat", listCat);
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

    public List<ListModel> getAllLabels(int userId) {
        List<ListModel> labels = new ArrayList<ListModel>();
        SQLiteDatabase db = this.getReadableDatabase();
//        Cursor cursor = db.query(TABLE_LIST,
//                new String[]{COLUMN_LIST_ID, COLUMN_LIST_NAME},
//                null, null, null, null, null);
        Cursor cursor = db.rawQuery(" SELECT * FROM " + TABLE_LIST + " WHERE " + COLUMN_LIST_USER_ID + " = " + userId, null);
        int listIdIdx = cursor.getColumnIndex(COLUMN_LIST_ID);
        int listNameIdx = cursor.getColumnIndex(COLUMN_LIST_NAME);

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
