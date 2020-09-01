package com.example.voicerecorder;

import android.media.MediaPlayer;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.google.android.material.bottomsheet.BottomSheetBehavior;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;

public class AudioListFragment extends Fragment implements RecordAdapter.OnItemClick, View.OnClickListener {

    private RecyclerView mRecyclerViewList;
    private ConstraintLayout mBottomSheet;

    private ArrayList<RecordModel> recordModels;

    private RecordAdapter recordAdapter;
    private BottomSheetBehavior bottomSheetBehavior;

    private MediaPlayer player;
    private boolean isPlaying = false;
    private boolean isFinished = false;

    private String filePath;
    private String fileToPlay;

    private TextView playing_stat,file_name;
    private ImageView playBtn,playPReBtn,playNextBtn;
    private SeekBar seekBar;
    private Handler seekBarHandler;
    private Runnable runnable;
    private int filePosition;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_audio_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mRecyclerViewList = view.findViewById(R.id.audio_recycler_view);
        mBottomSheet = view.findViewById(R.id.bottom_sheet);
        playing_stat = view.findViewById(R.id.player_not_playing_txt);
        file_name = view.findViewById(R.id.file_name_text);
        playBtn = view.findViewById(R.id.player_play_btn);
        playPReBtn = view.findViewById(R.id.player_pre_btn);
        playNextBtn = view.findViewById(R.id.player_next_btn);
        seekBar = view.findViewById(R.id.player_seekBar);

        //BottomSheet collapsed
        bottomSheetBehavior = new BottomSheetBehavior();
        bottomSheetBehavior = BottomSheetBehavior.from(mBottomSheet);
        bottomSheetBehavior.addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                if(newState == BottomSheetBehavior.STATE_HIDDEN){
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

            }
        });



        getFiles();
        recordAdapter = new RecordAdapter(getContext(),recordModels,this);
        mRecyclerViewList.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerViewList.setHasFixedSize(true);
        mRecyclerViewList.setAdapter(recordAdapter);

        filePath = Objects.requireNonNull(requireContext().getExternalCacheDir()).getAbsolutePath()+"/";

        playBtn.setOnClickListener(this);
        playPReBtn.setOnClickListener(this);
        playNextBtn.setOnClickListener(this);

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                if(fileToPlay != null) {
                    pauseAudio();
                }
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if(fileToPlay != null){
                    int progress = seekBar.getProgress();
                    player.seekTo(progress);
                    resumeAudio();
                }
            }
        });

    }

    private void getFiles(){
        File file = getContext().getExternalCacheDir();
        recordModels = new ArrayList<>();
        File fileList[] = file.listFiles();
        for(File list : fileList) {
            RecordModel model = new RecordModel();
            model.setFileName(list.getName());

            TimeAgo ago = new TimeAgo();
            String time = ago.getTimeAgo(list.lastModified(),new Date().getTime());
            model.setDate(time);
            recordModels.add(model);
        }

    }

    @Override
    public void onClickListener(String fileName, int position) {

        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        filePosition = position;

        fileToPlay = filePath + fileName;
        if(isPlaying){
            stopAudio();
            isPlaying = false;
            playAudio(fileToPlay);

        } else{
            playAudio(fileToPlay);
        }

        file_name.setText(fileName);

    }

    private void stopAudio() {
    isPlaying = false;
    player.stop();
    seekBarHandler.removeCallbacks(runnable);
    }

    private void playAudio(String fileToPlay) {
        isPlaying = true;
        isFinished = false;
        player = new MediaPlayer();
        try {
            player.setDataSource(fileToPlay);
            player.prepare();
            player.start();
        } catch (Exception e){
            e.printStackTrace();
        }

        playBtn.setImageDrawable(ResourcesCompat.getDrawable(getResources(),R.drawable.pause_btn,null));
        playing_stat.setText(R.string.now_playing);

        seekBar.setMax(player.getDuration());
        seekBarHandler = new Handler();

        runnable = new Runnable() {
            @Override
            public void run() {
                seekBar.setProgress(player.getCurrentPosition());
                seekBar.postDelayed(this,500);
            }
        };

        seekBarHandler.postDelayed(runnable,0);
        playerCompletion();

    }

    private void playerCompletion(){
        player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                stopAudio();
                playing_stat.setText(R.string.finished);
                playBtn.setImageDrawable(ResourcesCompat.getDrawable(getResources(),R.drawable.play,null));
                isPlaying = false;
                isFinished = true;
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.player_play_btn:
                if(fileToPlay != null) {
                    if (isPlaying) {
                        pauseAudio();
                    } else {
//                    playBtn.setImageDrawable(getResources().getDrawable(R.drawable.pause_btn,null));
//                    resumeAudio();
                        if (fileToPlay != null && isFinished) {
                            playAudio(fileToPlay);
                        } else {
                            resumeAudio();
                        }
                    }
                }
                break;
            case R.id.player_pre_btn:
                playerPreBtn();
                break;
            case R.id.player_next_btn:
                playerNextBtn();
                break;
        }
    }

    private void playerNextBtn() {
        if(fileToPlay != null) {
            if (filePosition < recordModels.size()-1) {
                filePosition = filePosition + 1;
                String file = recordModels.get(filePosition).getFileName();
                fileToPlay = filePath + file;

                if (isPlaying) {
                    stopAudio();
                }
                playAudio(fileToPlay);
            }
        }

    }

    private void playerPreBtn() {
        if(fileToPlay != null) {
            if (filePosition > 0) {
                filePosition = filePosition - 1;
                String file = recordModels.get(filePosition).getFileName();
                fileToPlay = filePath + file;

                if (isPlaying) {
                    stopAudio();
                }
                playAudio(fileToPlay);
            }
        }

    }

    private void pauseAudio(){
        playBtn.setImageDrawable(ResourcesCompat.getDrawable(getResources(),R.drawable.play,null));
        player.pause();
        isPlaying = false;
        seekBarHandler.removeCallbacks(runnable);
    }

    private void resumeAudio(){
        player.start();
        isPlaying = true;
        playBtn.setImageDrawable(ResourcesCompat.getDrawable(getResources(),R.drawable.pause_btn,null));

        runnable = new Runnable() {
            @Override
            public void run() {

                seekBar.setProgress(player.getCurrentPosition());
                seekBar.postDelayed(this,500);
            }
        };

        seekBarHandler.postDelayed(runnable,0);
    }

    @Override
    public void onStop() {
        super.onStop();
        if(player != null){
            player.pause();
            player.stop();
            player.release();
            seekBar.removeCallbacks(runnable);
        }
    }
}