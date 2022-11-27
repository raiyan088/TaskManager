package com.rr.taskmanager.info;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "task_data")
public class TaskData {

    @PrimaryKey(autoGenerate = true)
    public int id;

    @ColumnInfo(name = "title")
    public String title;

    @ColumnInfo(name = "description")
    public String description;

    @ColumnInfo(name = "start_time")
    public long start_time;

    @ColumnInfo(name = "end_time")
    public long end_time;

    public TaskData(int id, String title, String description, long start_time, long end_time) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.start_time = start_time;
        this.end_time = end_time;
    }

    @Ignore
    public TaskData(String title, String description, long start_time, long end_time) {
        this.title = title;
        this.description = description;
        this.start_time = start_time;
        this.end_time = end_time;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public long getStart_time() {
        return start_time;
    }

    public long getEnd_time() {
        return end_time;
    }


    public void setId(int id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setStart_time(long start_time) {
        this.start_time = start_time;
    }

    public void setEnd_time(long end_time) {
        this.end_time = end_time;
    }
}
