package com.example.voicerecorder;

import android.Manifest;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.os.FileUtils;
import android.os.SystemClock;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Chronometer;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;


public class RecordFragment extends Fragment implements View.OnClickListener {

    private static final int AUDIO_REQUEST_CODE = 1000;

    private NavController navController;

    //Member variables
    private ImageView mRecordListBtn;
    private ImageView mRecordStartBtn;
    private TextView mRecordFileName;
    private Chronometer mRecordChronometer;

    private String fileName = null;
    private String filePath = null;

    private boolean isRecording = false;
    private MediaRecorder recorder;


    public RecordFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_record, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        navController = Navigation.findNavController(view);

        mRecordListBtn = view.findViewById(R.id.record_list_btn);
        mRecordStartBtn = view.findViewById(R.id.record_start_btn);
        mRecordFileName = view.findViewById(R.id.record_file_name_txt);
        mRecordChronometer = view.findViewById(R.id.record_chronometer);

        filePath = Objects.requireNonNull(requireContext().getExternalCacheDir()).getAbsolutePath()+"/";

        //onClickListener
        mRecordListBtn.setOnClickListener(this);
        mRecordStartBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){

            //move to list fragment
            case R.id.record_list_btn:
                navController.navigate(R.id.action_recordFragment_to_audioListFragment);
                break;

            //start Recording
            case R.id.record_start_btn:
                if (isRecording){
                    //stop recording
                    stopRecording();
                } else{
                    //start Recording
                    if(checkPermissions()) {
                        startRecording();
                    }
                }
                break;
        }
    }

    private void stopRecording() {

        isRecording = false;
        mRecordStartBtn.setImageDrawable(ResourcesCompat.getDrawable(getResources(),R.drawable.mic_off,null));

        mRecordChronometer.stop();

        String fileRecordSetText = "Recording stopped: \n" + fileName;
        mRecordFileName.setText(fileRecordSetText);

       // getFileNameFromUser();
        recorder.stop();
        recorder.release();
        recorder = null;

    }

    private void startRecording() {

        isRecording = true;
        mRecordStartBtn.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.mic, null));

        mRecordChronometer.setBase(SystemClock.elapsedRealtime());
        mRecordChronometer.start();

        recorder = new MediaRecorder();
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);

        SimpleDateFormat formatter = new SimpleDateFormat("YYYY_MM_dd_hh_mm_ss", Locale.CANADA);
        Date date = new Date();
        fileName = "recording " + formatter.format(date) + ".3gp";

        //set external Storage location
        recorder.setOutputFile(filePath + fileName);
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

        try {
            recorder.prepare();
            recorder.start();
        } catch (IOException e){
            Log.i("prepareAudio","prepare() failed!!");
        }

        String fileNameSetText = getString(R.string.recording_file_name) +fileName;
        mRecordFileName.setText(fileNameSetText);

    }

    private boolean checkPermissions() {
        if(ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED){
            return true;
        } else{
            ActivityCompat.requestPermissions(requireActivity(),new String[]{Manifest.permission.RECORD_AUDIO},AUDIO_REQUEST_CODE);
            return false;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode == AUDIO_REQUEST_CODE){
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                //start Recording
                startRecording();
            }
        }
    }

//    private void getFileNameFromUser(){
//        final EditText editText = new EditText(getContext());
//        MaterialAlertDialogBuilder dialogBuilder = new MaterialAlertDialogBuilder(requireContext());
//        dialogBuilder
//                .setTitle("Save Recording")
//                .setMessage("please provide your file name here.")
//                .setView(editText)
//                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialogInterface, int i) {
//                        //don't save the file
//
//                    }
//                })
//
//                .setPositiveButton("save", new DialogInterface.OnClickListener() {
//                    @RequiresApi(api = Build.VERSION_CODES.Q)
//                    @Override
//                    public void onClick(DialogInterface dialogInterface, int i) {
//                        //save the recorded file
//
//                    }
//                })
//                .setCancelable(false)
//                .show();
//    }

}