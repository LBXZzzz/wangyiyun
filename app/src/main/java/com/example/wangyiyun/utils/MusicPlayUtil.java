package com.example.wangyiyun.utils;

import android.media.MediaPlayer;

import com.example.wangyiyun.view.MainActivity;

import java.io.IOException;

public class MusicPlayUtil {
    private String musicUrl;
    static MediaPlayer sMediaPlayer = new MediaPlayer();
    public MusicPlayUtil(String musicUrl){
        musicUrl=this.musicUrl;
    }
    //"https://music.163.com/song/media/outer/url?id=1963064332.mp3"
    public void musicPlay(){
        try {
            sMediaPlayer.setDataSource(musicUrl);//设置音源
            sMediaPlayer.prepare();
            sMediaPlayer.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
