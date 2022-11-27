package com.rr.taskmanager.fragment;

import static com.rr.taskmanager.App.TAG;
import static com.rr.taskmanager.App.databaseHalper;
import static com.rr.taskmanager.App.fragments;
import static com.rr.taskmanager.App.simpleDateFormat;
import static com.rr.taskmanager.App.twoChar;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TimePicker;
import android.widget.Toast;

import com.rr.taskmanager.activity.MainActivity;
import com.rr.taskmanager.adapter.PageAdapter;
import com.rr.taskmanager.adapter.TaskFragmentAdapter;
import com.rr.taskmanager.databinding.FragmentBinding;
import com.rr.taskmanager.databinding.NewTaskBinding;
import com.rr.taskmanager.databinding.TaskItemDetailsBinding;
import com.rr.taskmanager.info.TaskData;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;

public class TaskFragment extends Fragment {


    private ArrayList<TaskData> taskData;
    private HashMap<String, Object> mapData;
    private FragmentBinding binding;
    private TaskFragmentAdapter adapter;
    private String status = "";

    public TaskFragment(String status) {
        this.status = status;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentBinding.inflate(getLayoutInflater(), container, false);

        taskData = new ArrayList<>();
        mapData = new HashMap<>();

        List<TaskData> temp = databaseHalper.taskDataDao().getAll();

        if(status.equals("all")) {
            taskData = (ArrayList<TaskData>) temp;
        } else {
            long now = new Date().getTime();

            for (int i=0; i< temp.size(); i++) {
                long start = temp.get(i).getStart_time();
                long end = temp.get(i).getEnd_time();

                if(status.equals("panding") && now < start) {
                    taskData.add(temp.get(i));
                } else if (status.equals("runing") && now >= start && now < end) {
                    taskData.add(temp.get(i));
                } else if (status.equals("completed") && now >= start && now >= end) {
                    taskData.add(temp.get(i));
                }
            }
        }

        Collections.sort(taskData, new Comparator<TaskData>() {
            @Override
            public int compare(TaskData lhsTaskData, TaskData rhsTaskData) {
                return Integer.valueOf(rhsTaskData.getId()).compareTo(lhsTaskData.getId());
            }
        });

        for(int i=0; i<taskData.size(); i++) {
            mapData.put(String.valueOf(taskData.get(i).getId()), i);
        }

        adapter = new TaskFragmentAdapter(getActivity(), taskData);

        return binding.getRoot();
    }

    public void addTask(TaskData data) {
        long now = new Date().getTime();

        long start = data.getStart_time();
        long end = data.getEnd_time();

        if(status.equals("all")) {
            if(taskData != null) taskData.add(0, data);
        } else if(status.equals("panding") && now < start) {
            if(taskData != null) taskData.add(0, data);
        } else if (status.equals("runing") && now >= start && now < end) {
            if(taskData != null) taskData.add(0, data);
        } else if (status.equals("completed") && now >= start && now >= end) {
            if(taskData != null) taskData.add(0, data);
        }

        if(adapter != null) adapter.notifyDataSetChanged();
    }

    public void delete(TaskData data) {
        if(mapData != null) {
            Object object = mapData.get(String.valueOf(data.getId()));
            if(object != null) {
                if(taskData != null) taskData.remove(Integer.parseInt(String.valueOf(object)));
            }
        }

        if(adapter != null) adapter.notifyDataSetChanged();
    }

    public void update(TaskData data) {
        if(mapData != null) {
            Object object = mapData.get(String.valueOf(data.getId()));
            if(object != null) {
                if(taskData != null) {
                    int position = Integer.parseInt(String.valueOf(object));
                    taskData.get(position).setTitle(data.getTitle());
                    taskData.get(position).setDescription(data.getDescription());
                    taskData.get(position).setStart_time(data.getStart_time());
                    taskData.get(position).setEnd_time(data.getEnd_time());
                }
            }
        }

        if(adapter != null) adapter.notifyDataSetChanged();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        binding.recylerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        binding.recylerView.setAdapter(adapter);

        adapter.setOnItemClickListener(new TaskFragmentAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                TaskItemDetailsBinding taskItemDetailsBinding = TaskItemDetailsBinding.inflate(getLayoutInflater());

                AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());

                dialog.setView(taskItemDetailsBinding.getRoot());

                TaskData data = taskData.get(position);

                taskItemDetailsBinding.taskTitle.setText("Title: "+data.getTitle());
                taskItemDetailsBinding.taskDescription.setText("Decription: "+data.getDescription());
                taskItemDetailsBinding.taskStartTime.setText("Start Time: "+simpleDateFormat.format(new Timestamp(data.getStart_time())));
                taskItemDetailsBinding.taskEndTime.setText("End Time: "+simpleDateFormat.format(new Timestamp(data.getEnd_time())));


                dialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });

                dialog.create().show();
            }
        });

        adapter.setOnItemLongClickListener(new TaskFragmentAdapter.OnItemLongClickListener() {
            @Override
            public void onItemLongClick(View view, int position) {
                AlertDialog.Builder edDialog = new AlertDialog.Builder(getActivity());
                edDialog.setTitle("Edit or Delete Data");
                edDialog.setMessage("You can edit or delete this data");

                edDialog.setPositiveButton("Edit", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        update(position);
                    }
                });

                edDialog.setNegativeButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        AlertDialog.Builder dDialog = new AlertDialog.Builder(getActivity());
                        dDialog.setTitle("Confirm Delete Data");
                        dDialog.setMessage("You want delete this data");

                        dDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                                databaseHalper.taskDataDao().delete(taskData.get(position));

                                TaskData data = taskData.get(position);
                                for(int j=0; j<fragments.size(); j++) {
                                    TaskFragment fragment = fragments.get(j);
                                    if(fragments.get(j) != null) fragment.delete(data);
                                }

                                Toast.makeText(getActivity(), "Delete Success ", Toast.LENGTH_SHORT).show();
                            }
                        });

                        dDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        });

                        dDialog.create().show();

                        //end
                    }
                });

                edDialog.setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });

                edDialog.create().show();
            }
        });

        super.onViewCreated(view, savedInstanceState);
    }

    private void update(int position) {
        TaskData data = taskData.get(position);

        NewTaskBinding newTaskBinding = NewTaskBinding.inflate(getLayoutInflater());

        androidx.appcompat.app.AlertDialog dialog = new androidx.appcompat.app.AlertDialog.Builder(getActivity())
                .setTitle("Update Task")
                .setView(newTaskBinding.getRoot())
                .setPositiveButton("Update", null)
                .setNegativeButton("Cancel", null)
                .create();

        newTaskBinding.taskTitle.setText(data.getTitle());
        newTaskBinding.taskDescription.setText(data.getDescription());
        newTaskBinding.taskStartTimeView.setText(simpleDateFormat.format(new Timestamp(data.getStart_time())));
        newTaskBinding.taskEndTimeView.setText(simpleDateFormat.format(new Timestamp(data.getEnd_time())));

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
                                data.setTitle(title);
                                data.setDescription(description);
                                data.setStart_time(simpleDateFormat.parse(startTime).getTime());
                                data.setEnd_time(simpleDateFormat.parse(endTime).getTime());

                                databaseHalper.taskDataDao().update(data);

                                for(int i=0; i<fragments.size(); i++) {
                                    TaskFragment fragment = fragments.get(i);
                                    if(fragments.get(i) != null) fragment.update(data);
                                }

                                Toast.makeText(getActivity(), "Update Task", Toast.LENGTH_SHORT).show();
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