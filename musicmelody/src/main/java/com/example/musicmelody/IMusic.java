package com.example.musicmelody;

import java.util.List;

public interface IMusic {
    //开始播放音乐
    void startMusic(SongItem songItem);

    //暂停音乐
    void stopMusic();

    //下一首播放
    void nextSong();

    //上一首播放
    void preSong();

    //播放模式，单曲循环...
    int playMode();

    //获取音乐的总时间
    int getMusicTotalTime();

    //获取音乐当前时间
    int getMusicCurrentTime();

    //
    void openMusic(SongItem songItem);

    //获取播放模式
    int playModeInt();

}
