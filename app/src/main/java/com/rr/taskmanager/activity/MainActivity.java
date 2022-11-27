package com.rr.taskmanager.activity;

import static com.rr.taskmanager.App.databaseHalper;
import static com.rr.taskmanager.App.fragments;
import static com.rr.taskmanager.App.simpleDateFormat;
import static com.rr.taskmanager.App.twoChar;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.view.View;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.viewpager.widget.ViewPager;

import com.rr.taskmanager.R;
import com.rr.taskmanager.adapter.PageAdapter;
import com.rr.taskmanager.databinding.ActivityMainBinding;
import com.rr.taskmanager.databinding.NewTaskBinding;
import com.rr.taskmanager.fragment.TaskFragment;
import com.rr.taskmanager.info.TaskData;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TimePicker;
import android.widget.Toast;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration appBarConfiguration;
    private ActivityMainBinding binding;
    private PageAdapter adapter;

    private boolean fabVisiable = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setTheme(R.style.Theme_TaskManager_NoActionBar);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);

        binding.tabLayout.setupWithViewPager(binding.viewPager);

        adapter = new PageAdapter(getSupportFragmentManager(), FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        adapter.addItem(new TaskFragment("all"), "All Task");
        adapter.addItem(new TaskFragment("panding"), "Panding");
        adapter.addItem(new TaskFragment("runing"), "Runing");
        adapter.addItem(new TaskFragment("completed"), "Completed");

        binding.viewPager.setOffscreenPageLimit(4);
        binding.viewPager.setAdapter(adapter);

        binding.viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                if(position == 0) {
                    if(positionOffset*100 > 50) {
                        if(fabVisiable) {
                            fabVisiable = false;
                            binding.fab.animate()
                                    .alpha(0f)
                                    .scaleX(0.5f)
                                    .scaleY(0.5f)
                                    .setDuration(200);
                        }
                    } else if(!fabVisiable) {
                        fabVisiable = true;

                        binding.fab.animate()
                                .alpha(1f)
                                .scaleX(1f)
                                .scaleY(1f)
                                .setDuration(200);
                    }
                }
            }

            @Override
            public void onPageSelected(int position) {
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

        binding.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(fabVisiable) {
                    NewTaskBinding newTaskBinding = NewTaskBinding.inflate(getLayoutInflater());

                    androidx.appcompat.app.AlertDialog dialog = new androidx.appcompat.app.AlertDialog.Builder(getActivity())
                            .setTitle("Add New Task")
                            .setView(newTaskBinding.getRoot())
                            .setPositiveButton("Add", null)
                            .setNegativeButton("Cancel", null)
                            .create();

                    newTaskBinding.taskStartTimeView.setSelected(true);
                    newTaskBinding.taskEndTimeView.setSelected(true);

                    newTaskBinding.taskStartTime.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Calendar calendar = Calendar.getInstance();
                            int day = calendar.get(Calendar.DAY_OF_MONTH);
                            int month = calendar.get(Calendar.MONTH);
                            int year = calendar.get(Calendar.YEAR);

                            DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
                                @Override
                                public void onDateSet(DatePicker datePicker, final int year, final int month, final int day) {
                                    try {
                                        Date startDate = simpleDateFormat.parse(year+"-"+twoChar(month+1)+"-"+twoChar(day)+" 11:59 PM");

                                        if(new Date().getTime() < startDate.getTime()) {
                                            Calendar calendar = Calendar.getInstance();
                                            int hour = calendar.get(Calendar.HOUR_OF_DAY);
                                            int minute = calendar.get(Calendar.MINUTE);

                                            TimePickerDialog timePickerDialog = new TimePickerDialog(getActivity(), new TimePickerDialog.OnTimeSetListener() {
                                                @Override
                                                public void onTimeSet(TimePicker timePicker, int hour, int minute) {
                                                    String time = "";

                                                    if (hour > 12) {
                                                        time = twoChar(hour-12)+":"+twoChar(minute)+" PM";
                                                    } else {
                                                        time = twoChar(hour)+":"+twoChar(minute)+" AM";
                                                    }

                                                    String dateTime = year+"-"+twoChar(month+1)+"-"+twoChar(day)+" "+time;

                                                    try {
                                                        Date startTime = simpleDateFormat.parse(dateTime);

                                                        if(new Date().getTime() < startTime.getTime()) {
                                                            String endDateTime = newTaskBinding.taskEndTimeView.getText().toString();

                                                            if(!endDateTime.equals("")) {
                                                                Date endTime = simpleDateFormat.parse(endDateTime);

                                                                if(startTime.getTime() >= endTime.getTime()) {
                                                                    newTaskBinding.taskEndTimeView.setText("");
                                                                }
                                                            }

                                                            newTaskBinding.taskStartTimeView.setText(dateTime);
                                                        } else {
                                                            Toast.makeText(getActivity(), "Start Time Error", Toast.LENGTH_SHORT).show();
                                                        }
                                                    } catch (Exception e) {
                                                        e.printStackTrace();
                                                    }
                                                }
                                            }, hour, minute, false);

                                            timePickerDialog.show();
                                        } else {
                                            Toast.makeText(getActivity(), "Start Date Error", Toast.LENGTH_SHORT).show();
                                        }
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            }, year, month, day);

                            datePickerDialog.show();
                        }
                    });

                    newTaskBinding.taskEndTime.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            String start = newTaskBinding.taskStartTimeView.getText().toString();

                            if(start.equals("")) {
                                Toast.makeText(getActivity(), "Please set Start Time", Toast.LENGTH_SHORT).show();
                            } else {
                                try {
                                    final Date startDate = simpleDateFormat.parse(start);

                                    final Calendar calendar = new GregorianCalendar();
                                    calendar.setTime(startDate);

                                    int day = calendar.get(Calendar.DAY_OF_MONTH);
                                    int month = calendar.get(Calendar.MONTH);
                                    int year = calendar.get(Calendar.YEAR);

                                    DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
                                        @Override
                                        public void onDateSet(DatePicker datePicker, final int year, final int month, final int day) {

                                            try {
                                                Date endDate = simpleDateFormat.parse(year+"-"+twoChar(month+1)+"-"+twoChar(day)+" 11:59 PM");

                                                if(startDate.getTime() <= endDate.getTime()) {

                                                    int hour = calendar.get(Calendar.HOUR_OF_DAY);
                                                    int minute = calendar.get(Calendar.MINUTE);

                                                    TimePickerDialog timePickerDialog = new TimePickerDialog(getActivity(), new TimePickerDialog.OnTimeSetListener() {
                                                        @Override
                                                        public void onTimeSet(TimePicker timePicker, int hour, int minute) {
                                                            String time = "";

                                                            if (hour > 12) {
                                                                time = twoChar(hour-12)+":"+twoChar(minute)+" PM";
                                                            } else {
                                                                time = twoChar(hour)+":"+twoChar(minute)+" AM";
                                                            }

                                                            try {
                                                                String dateTime = year+"-"+twoChar(month+1)+"-"+twoChar(day)+" "+time;
                                                                Date endTime = simpleDateFormat.parse(dateTime);

                                                                if(startDate.getTime() < endTime.getTime()) {
                                                                    newTaskBinding.taskEndTimeView.setText(dateTime);
                                                                } else {
                                                                    Toast.makeText(getActivity(), "End Time Error", Toast.LENGTH_SHORT).show();
                                                                }
                                                            } catch (Exception e) {
                                                                e.printStackTrace();
                                                            }
                                                        }
                                                    }, hour, minute, false);

                                                    timePickerDialog.show();
                                                } else {
                                                    Toast.makeText(getActivity(), "End Date Error", Toast.LENGTH_SHORT).show();
                                                }
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    }, year, month, day);

                                    datePickerDialog.show();

                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }

                        }
                    });


                    dialog.setOnShowListener(new DialogInterface.OnShowListener() {
                        @Override
                        public void onShow(DialogInterface dialogInterface) {

                            Button button = ((androidx.appcompat.app.AlertDialog) dialog).getButton(androidx.appcompat.app.AlertDialog.BUTTON_POSITIVE);
                            button.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    String title = newTaskBinding.taskTitle.getText().toString();
                                    String description = newTaskBinding.taskDescription.getText().toString();
                                    String startTime = newTaskBinding.taskStartTimeView.getText().toString();
                                    String endTime = newTaskBinding.taskEndTimeView.getText().toString();

                                    if(title.equals("") || description.equals("") || startTime.equals("") || endTime.equals("")) {
                                        Toast.makeText(getActivity(), "Invalid Task", Toast.LENGTH_SHORT).show();
                                    } else {
                                        dialog.dismiss();

                                        try {
                                            TaskData taskData =new TaskData(title, description,
                                                    simpleDateFormat.parse(startTime).getTime(),
                                                    simpleDateFormat.parse(endTime).getTime());

                                            databaseHalper.taskDataDao().add(taskData);

                                            for(int i=0; i<fragments.size(); i++) {
                                                TaskFragment fragment = fragments.get(i);
                                                if(fragments.get(i) != null) fragment.addTask(taskData);
                                            }

                                            Toast.makeText(getActivity(), "New Task Add", Toast.LENGTH_SHORT).show();
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }
                            });
                        }
                    });

                    dialog.show();
                }
            }
        });
    }

    private Activity getActivity() {
        return MainActivity.this;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}