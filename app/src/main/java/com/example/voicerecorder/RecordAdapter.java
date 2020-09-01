package com.example.voicerecorder;

import android.content.Context;
import android.media.MediaPlayer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetBehavior;

import java.io.IOException;
import java.util.ArrayList;

public class RecordAdapter extends RecyclerView.Adapter<RecordAdapter.RecordViewHolder>{

    private ArrayList<RecordModel> recordList;
    private Context context;
    private OnItemClick onItemClick;

    public RecordAdapter(Context context,ArrayList<RecordModel> recordList, OnItemClick onItemClick) {
        this.context = context;
        this.recordList = recordList;
        this.onItemClick = onItemClick;
    }

    @NonNull
    @Override
    public RecordViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.record_single_list,parent,false);
        return new RecordViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecordViewHolder holder, final int position) {
        holder.setFileName(recordList.get(position).getFileName());
        holder.setDate(recordList.get(position).getDate());
    }

    @Override
    public int getItemCount() {
        return recordList.size();
    }

    public class RecordViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        View view;
        TextView fileName;
        TextView date;

        public RecordViewHolder(@NonNull View itemView) {
            super(itemView);
            view = itemView;

            view.setOnClickListener(this);
        }

        public void setFileName(String fileName) {
            this.fileName = view.findViewById(R.id.file_name);
            this.fileName.setText(fileName);
        }


        public void setDate(String date) {
            this.date = view.findViewById(R.id.record_time);
            this.date.setText(date);
        }

        @Override
        public void onClick(View view) {
            onItemClick.onClickListener(recordList.get(getAdapterPosition()).getFileName(),getAdapterPosition());
        }
    }

    public interface OnItemClick{
        public void onClickListener(String fileName,int position);
    }

}
