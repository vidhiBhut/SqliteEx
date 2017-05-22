package com.example.vidhi.preferenceex;

/**
 * Created by vidhi on 5/8/2017.
 */

public class TaskModel {
    int id;
    String task;
    boolean status;
    int listCategory;
    String listCat;
    int userId;

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getListCat() {
        return listCat;
    }

    public void setListCat(String listCat) {
        this.listCat = listCat;
    }

    public int getListCategory() {
        return listCategory;
    }

    public void setListCategory(int listCategory) {
        this.listCategory = listCategory;
    }


    public boolean getStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTask() {
        return task;
    }

    public void setTask(String task) {
        this.task = task;
    }


    @Override
    public String toString() {
        return "TaskModel{" +
                "id=" + id +
                ", task='" + task + '\'' +
                ", status=" + status +
                '}';
    }
}
