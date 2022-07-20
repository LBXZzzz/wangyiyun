package com.example.musicmelody;

import android.media.MediaPlayer;
import android.util.Log;

import java.io.IOException;

public class MusicPlay implements IMusic{
    private MediaPlayer mediaPlayer=new MediaPlayer();
    private boolean isPlay=true;
    private boolean isTime=false;
    @Override
    public void startMusic() {
        if(!mediaPlayer.isPlaying()){
            if(isPlay){
                try {
                    String threadName = Thread.currentThread().getName();
                    Log.v("zwy", "线程：" + threadName );
                    mediaPlayer.setDataSource("https://music.163.com/song/media/outer/url?id=1964644539.mp3");//设置音源
                    mediaPlayer.prepareAsync();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                isPlay=false;
            }else {
                mediaPlayer.start();
            }
        }
        mediaPlayer.setOnPreparedListener((mediaPlayer -> {
            mediaPlayer.start();
        }));
    }


    @Override
    public void stopMusic() {
        if(mediaPlayer.isPlaying()){
            mediaPlayer.pause();
        }
    }

    @Override
    public void nextSong() {

    }

    @Override
    public void preSong() {

    }

    @Override
    public void playMode() {

    }

    @Override
    public int getMusicTotalTime() {
        return mediaPlayer.getDuration();
    }

    @Override
    public int getMusicCurrentTime() {
        return mediaPlayer.getCurrentPosition();
    }
}
