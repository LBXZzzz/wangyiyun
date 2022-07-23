
package com.example.musicmelody;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;

import androidx.core.app.NotificationCompat;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MusicService extends Service {
    public static List<String> songList=new ArrayList<>();
    private MusicPlay musicPlay=new MusicPlay(songList);
    public MusicService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return musicPlay;
    }

    @Override
    public void onCreate() {
        super.onCreate();
//        判断是否为8.0版本以上
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
//            获取系统服务管理器
            NotificationManager manage = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            String id = "通知id";
            String name = "通知分类名称";
//            建立通知通道
            NotificationChannel notificationChannel = new NotificationChannel(id,
                    name,
                    NotificationManager.IMPORTANCE_DEFAULT);
            manage.createNotificationChannel(notificationChannel);
            Notification build = new NotificationCompat.Builder(this, id)
                    .setContentTitle("前台服务")
                    .setContentText("这是一个前台服务")
                    .setWhen(System.currentTimeMillis())   //   当前时间
                    .setSmallIcon(R.drawable.music_tab)    //  图标
                    .setProgress(100, 10, false)   //   进度
                    .build();

            startForeground(1, build);
//            manage.notify(1, build);
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }


    public static class MusicPlay extends Binder implements IMusic{
        private MediaPlayer mediaPlayer;
        //判断有没有MediaPlayer准备过,true为没准备过
        private boolean isPlay=true;
        //记录歌曲播放到第几首
        int songNumber=0;
        List<String> songList=new ArrayList<>();
        public  MusicPlay(List<String> songList){
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
            int i=0;
            try {
                i=mediaPlayer.getCurrentPosition();
            }catch (Exception ignore){

            }
           return i;
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

}