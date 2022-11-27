package com.rr.taskmanager;

import android.app.Application;

import androidx.annotation.NonNull;

import com.rr.taskmanager.database.DatabaseHalper;
import com.rr.taskmanager.fragment.TaskFragment;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

public class App extends Application {

    public static String TAG = "Task Manager";

    public static final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm a", Locale.getDefault());

    public static DatabaseHalper databaseHalper;
    public static ArrayList<TaskFragment> fragments;

    @Override
    public void onCreate() {
        super.onCreate();

        databaseHalper = DatabaseHalper.getDatabase(this);

        fragments = new ArrayList<>();
    }

    @NonNull
    public static String twoChar(int value) {
        String data = String.valueOf(value);
        if (data.length() == 1) {
            return "0"+data;
        }
        return data;
    }
}
