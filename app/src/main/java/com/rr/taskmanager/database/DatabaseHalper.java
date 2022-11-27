package com.rr.taskmanager.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.rr.taskmanager.info.TaskData;
import com.rr.taskmanager.interfaces.TaskDataDao;

@Database(entities = TaskData.class, exportSchema = false, version = 1)
public abstract class DatabaseHalper extends RoomDatabase {
    private static final String DB_NAME = "task_db";
    private static DatabaseHalper databaseHalper;

    public static synchronized DatabaseHalper getDatabase(Context context) {
        if(databaseHalper == null) {
            databaseHalper = Room.databaseBuilder(context, DatabaseHalper.class, DB_NAME)
                    .fallbackToDestructiveMigration()
                    .allowMainThreadQueries()
                    .build();
        }
        return databaseHalper;
    };

    public abstract TaskDataDao taskDataDao();
}
