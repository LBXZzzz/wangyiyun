
package com.example.musicmelody;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.widget.RemoteViews;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class MusicService extends Service {

    //存歌曲的ID
    public static List<SongItem> songItemList = new ArrayList<>();
    private MusicPlay musicPlay = new MusicPlay(songItemList);
    //
    RemoteViews remoteViews;
    NotificationManager notificationManager;
    private static final String id = "1";
    Notification notification;

    public MusicService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return musicPlay;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        remoteViews = new RemoteViews(getPackageName(), R.layout.notification);
        remoteViews.setImageViewResource(R.id.iv_notification_music_photo, R.drawable.ic_music_stop);
        sendDefaultNotification(remoteViews);
    }

    public void sendDefaultNotification(RemoteViews remoteViews) {
        //        判断是否为8.0版本以上
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
//            获取系统服务管理器
            notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            String id = "通知id";
            String name = "通知分类名称";
//            建立通知通道
            NotificationChannel notificationChannel = new NotificationChannel(id,
                    name,
                    NotificationManager.IMPORTANCE_DEFAULT);
            if (notificationManager.getNotificationChannel(id) == null)
                notificationManager.createNotificationChannel(notificationChannel);
            notification = new NotificationCompat.Builder(this, id)
                    .setContentTitle("前台服务")
                    .setContentText("这是一个前台服务")
                    .setContent(remoteViews)
                    .setWhen(System.currentTimeMillis())   //   当前时间
                    .setSmallIcon(R.drawable.ic_music_normal)    //  图标
                    .setProgress(100, 10, false)   //   进度
                    .build();
            startForeground(1, notification);
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


    public class MusicPlay extends Binder implements IMusic,MediaPlayer.OnCompletionListener,MediaPlayer.OnErrorListener {
        private MediaPlayer mediaPlayer = new MediaPlayer();
        //判断有没有MediaPlayer准备过,true为没准备过
        private volatile boolean isPlay = true;
        //判断是否需要释放
        volatile boolean isFirstPre = true;
        //记录歌曲播放到第几首
        private int songNumber = 0;
        //存储播放列表的集合
        List<SongItem> songList;

        //
        public MusicPlay(List<SongItem> songList) {
            this.songList = songList;
        }

        @Override
        public void startMusic(SongItem songItem) {
            if (isPlay) {
                mediaPlayer.setOnCompletionListener(this);
                mediaPlayer.setOnErrorListener(this);
                String musicId = songItem.getSongId();
                String url = "https://netease-cloud-music-api-4eodv9lwk-tangan91314.vercel.app/song/url?id=" + musicId;
                String songPlayId = "https://music.163.com/song/media/outer/url?id=" + musicId + ".mp3";
                //更新通知
                remoteViews.setTextViewText(R.id.tv_notification_song_name, songItem.getSongName());
                remoteViews.setTextViewText(R.id.tv_notification_singer_name, songItem.getSingerName());
                handler=new Handler(Looper.getMainLooper()){
                    @Override
                    public void handleMessage(@NonNull Message msg) {
                        String photoUrl = (String) msg.obj;
                        Picasso.with(getApplicationContext()).load(photoUrl).into(remoteViews, R.id.iv_notification_music_photo, 1, notification);
                    }
                };
                Message message = new Message();
                message.obj = songItem.getPicUrl()+ "?param=200y200";
                handler.sendMessage(message);
                notificationManager.notify(1, notification);
                returnData(url);
                if (Looper.myLooper() == null) {
                    Looper.prepare();
                }
                Looper looper = Looper.myLooper();
                Intent intent = new Intent(MusicService.this,MusicActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("SongList", (Serializable)songItem);
                intent.putExtras(bundle);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                handler = new Handler(looper) {
                    @Override
                    public void handleMessage(@NonNull Message msg) {
                        String songUrl = (String) msg.obj;
                        try {
                            if (songUrl.equals("null")) {
                                songUrl = songPlayId;
                            }
                            if (isFirstPre) {
                                mediaPlayer.setDataSource(songUrl);//设置音源
                                mediaPlayer.prepareAsync();
                                isFirstPre = false;
                            } else {
                                mediaPlayer.reset();
                                mediaPlayer.setDataSource(songUrl);//设置音源
                                mediaPlayer.prepareAsync();
                            }

                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        isPlay = false;
                        super.handleMessage(msg);
                        mediaPlayer.setOnPreparedListener((mediaPlayer -> {
                            mediaPlayer.start();
                        }));
                    }
                };
                Looper.loop();
            } else {
                mediaPlayer.start();
            }
        }

        //获取歌曲id后拿来获取歌曲的url
        private void returnData(String url) {
            HttpUtil.cachedThreadPool.execute(() -> {
                try {
                    analyzeData(HttpUtil.get(url));
                } catch (Throwable e) {
                    e.printStackTrace();
                }
            });
        }

        private Handler handler;

        private void analyzeData(String s) throws JSONException {
            JSONObject jsonObject = new JSONObject(s);
            JSONArray jsonArray = jsonObject.optJSONArray("data");
            JSONObject jsonObject1 = jsonArray.getJSONObject(0);
            String songUrl = jsonObject1.getString("url");
            Message message = new Message();
            message.obj = songUrl;
            handler.sendMessage(message);
        }


        @Override
        public void stopMusic() {
            if (mediaPlayer != null) {
                try {
                    if (mediaPlayer.isPlaying()) {
                        mediaPlayer.pause();
                    }
                } catch (Exception ignore) {

                }

            }
        }

        @Override
        public void nextSong() {
            if (!isPlay) {
                mediaPlayer.reset();
                isPlay = true;
            }
            if (songNumber == songList.size() - 1) {
                songNumber = 0;
            } else {
                songNumber += 1;
            }
            startMusic(songList.get(songNumber));
        }

        @Override
        public void preSong() {
            if (!isPlay) {
                mediaPlayer.reset();
                isPlay = true;
            }
            if (songNumber == 0) {
                songNumber = songList.size() - 1;
            } else {
                songNumber -= 1;
            }
            startMusic(songList.get(songNumber));
        }

        @Override
        public void playMode() {
        }

        @Override
        public int getMusicTotalTime() {
            int i = 0;
            try {
                i = mediaPlayer.getDuration();
            } catch (Exception ignore) {

            }
            return i;
        }

        @Override
        public int getMusicCurrentTime() {
            int i = 0;
            try {
                i = mediaPlayer.getCurrentPosition();
            } catch (Exception ignore) {

            }
            return i;
        }

        @Override
        public void openMusic(SongItem songItem) {
            if (!isPlay) {
                mediaPlayer.reset();
                isPlay = true;
            }
            startMusic(songItem);
        }

        @Override
        public void onCompletion(MediaPlayer mp) {
            nextSong();
        }

        @Override
        public boolean onError(MediaPlayer mp, int what, int extra) {
            return true;
        }
    }

}