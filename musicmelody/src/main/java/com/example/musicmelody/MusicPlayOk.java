package com.example.musicmelody;

import android.media.MediaPlayer;
import android.os.Binder;
import android.util.Log;
import android.view.View;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MusicPlayOk extends Binder implements IMusic{
    private MediaPlayer mediaPlayer;
    //判断有没有MediaPlayer准备过,true为没准备过
    private boolean isPlay=true;
    //记录歌曲播放到第几首
    int songNumber=0;
    List<String> songList=new ArrayList<>();
    public MusicPlayOk(List<String> songList){
        this.songList=songList;
    }

    @Override
    public void startMusic(String musicUrl) {
            if(isPlay){
                try {
                    mediaPlayer=new MediaPlayer();
                    mediaPlayer.setDataSource(musicUrl);//设置音源
                    mediaPlayer.prepareAsync();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                isPlay=false;
            }else {
                mediaPlayer.start();
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
        if (!isPlay) {
            mediaPlayer.stop();
            mediaPlayer.release();
            isPlay = true;
        }
        if (songNumber==songList.size()-1){
            songNumber=0;
        }else {
            songNumber+=1;
        }
        startMusic(songList.get(songNumber));
    }

    @Override
    public void preSong() {
        if (!isPlay) {
            mediaPlayer.stop();
            mediaPlayer.release();
            isPlay = true;
        }
        if (songNumber==0){
            songNumber=songList.size()-1;
        }else {
            songNumber-=1;
        }

        startMusic(songList.get(songNumber));
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

    @Override
    public void openMusic(String musicUrl) {
        if (!isPlay) {
            mediaPlayer.stop();
            mediaPlayer.release();
            isPlay = true;
        }
        startMusic(musicUrl);
    }
}
