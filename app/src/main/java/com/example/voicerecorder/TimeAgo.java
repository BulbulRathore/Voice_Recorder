package com.example.voicerecorder;

import android.util.Log;
import android.util.TimeUtils;

import java.util.Date;
import java.util.concurrent.TimeUnit;

public class TimeAgo {
    public String getTimeAgo(long duration,long currentTime){

        Date now = new Date();

        long seconds = TimeUnit.MILLISECONDS.toSeconds(currentTime - duration);
        long minutes = TimeUnit.MILLISECONDS.toMinutes(currentTime - duration);
        long hours = TimeUnit.MILLISECONDS.toHours(currentTime- duration);
        long days = TimeUnit.MILLISECONDS.toDays(currentTime- duration);

        if(seconds < 60){
            return "just now";
        } else if(minutes == 1){
            return  "a minute ago";
        } else if(minutes > 1 && minutes < 60){
            return minutes + " minutes ago";
        } else if(hours == 1){
            return "a hour ago";
        } else if(hours > 1 && hours < 24){
            return hours + " hours ago";
        } else if(days == 1) {
            return "a day ago";
        } else {
            return days + " days ago";
        }
    }
}
