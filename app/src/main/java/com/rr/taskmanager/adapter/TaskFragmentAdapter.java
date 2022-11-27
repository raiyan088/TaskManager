package com.rr.taskmanager.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.rr.taskmanager.databinding.FragmentTaskItemBinding;
import com.rr.taskmanager.info.TaskData;

import java.util.ArrayList;
import java.util.Date;

public class TaskFragmentAdapter extends RecyclerView.Adapter<TaskFragmentAdapter.MyViewHolder> {

    private Context context;
    private ArrayList<TaskData> taskData;
    private OnItemClickListener itemClickListener;
    private OnItemLongClickListener itemLongClickListener;


    public TaskFragmentAdapter(Context context, ArrayList<TaskData> taskData) {
        this.context = context;
        this.taskData = taskData;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(FragmentTaskItemBinding.inflate(LayoutInflater.from(context), parent, false));
    }

    @SuppressLint("Range")
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.binding.taskTitle.setText("Title: "+taskData.get(position).getTitle());
        holder.binding.taskDescription.setText("Des. : "+taskData.get(position).getDescription());

        String status = "";
        String statusColor = "";

        long start = taskData.get(position).getStart_time();
        long end = taskData.get(position).getEnd_time();
        long now = new Date().getTime();

        if(now < start) {
            status = "Panding";
            statusColor = "#1A2196F3";
        } else if(now < end) {
            status = "Runing";
            statusColor = "#1A673AB7";
        } else {
            status = "Completed";
            statusColor = "#1A00FF0A";
        }

        holder.binding.taskStatus.setText("Status: "+status);
        holder.binding.container.setBackgroundColor(Color.parseColor(statusColor));
    }

    @Override
    public int getItemCount() {
        return taskData.size();
    }

    protected class MyViewHolder extends RecyclerView.ViewHolder {

        private FragmentTaskItemBinding binding;

        public MyViewHolder(@NonNull FragmentTaskItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;

            this.binding.getRoot().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (itemClickListener != null) itemClickListener.onItemClick(view, getAdapterPosition());
                }
            });

            this.binding.getRoot().setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    if (itemLongClickListener != null) itemLongClickListener.onItemLongClick(view, getAdapterPosition());
                    return false;
                }
            });
        }

    }

    public void setOnItemClickListener(OnItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public void setOnItemLongClickListener(OnItemLongClickListener itemLongClickListener) {
        this.itemLongClickListener = itemLongClickListener;
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    public interface OnItemLongClickListener {
        void onItemLongClick(View view, int position);
    }
}
