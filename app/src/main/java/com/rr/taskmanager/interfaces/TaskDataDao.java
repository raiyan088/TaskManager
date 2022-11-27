package com.rr.taskmanager.interfaces;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.rr.taskmanager.info.TaskData;

import java.util.List;

@Dao
public interface TaskDataDao {

    @Query("SELECT * FROM task_data")
    List<TaskData> getAll();

    @Insert
    void add(TaskData users);

    @Update
    void update(TaskData users);

    @Delete
    void delete(TaskData user);
}
