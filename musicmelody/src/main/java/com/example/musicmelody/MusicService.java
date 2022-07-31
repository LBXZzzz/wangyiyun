
package com.example.musicmelody;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
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
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class MusicService extends Service implements IMusic, MediaPlayer.OnCompletionListener, MediaPlayer.OnErrorListener {

    //存歌曲的ID
    public static List<SongItem> songItemList = new ArrayList<>();
    private final MusicPlay musicPlay = new MusicPlay();
    public static int songNumber = 0;
    public static boolean isStartActivity = true;
    //存随机播放的歌曲信息
    private RemoteViews remoteViews;
    private NotificationManager notificationManager;
    private Notification notification;

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
        //更新通知
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BROAD_RECEIVER_ACTION_PLAY);
        intentFilter.addAction(BROAD_RECEIVER_ACTION_PAUSE);
        intentFilter.addAction(BROAD_RECEIVER_NEXT);
        intentFilter.addAction(BROAD_RECEIVER_PRE);
        BroadReceiver broadReceiver = new BroadReceiver();
        registerReceiver(broadReceiver, intentFilter);
        Intent intent1 = new Intent();
        intent1.setAction(BROAD_RECEIVER_ACTION_PLAY);
        PendingIntent pending = PendingIntent.getBroadcast(getApplicationContext(), 0, intent1, PendingIntent.FLAG_MUTABLE);
        remoteViews.setOnClickPendingIntent(R.id.iv_notification_music_play_service, pending);
        intent1.setAction(BROAD_RECEIVER_NEXT);
        pending = PendingIntent.getBroadcast(getApplicationContext(), 0, intent1, PendingIntent.FLAG_MUTABLE);
        remoteViews.setOnClickPendingIntent(R.id.iv_notification_next_song_service, pending);
        intent1.setAction(BROAD_RECEIVER_PRE);
        pending = PendingIntent.getBroadcast(getApplicationContext(), 0, intent1, PendingIntent.FLAG_MUTABLE);
        remoteViews.setOnClickPendingIntent(R.id.iv_notification_pre_song_service, pending);
        notificationManager.notify(1, notification);
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


    public class MusicPlay extends Binder {
        public MusicService getService() {
            return MusicService.this;
        }
    }


    //音乐功能
    //常量
    private static final String BROAD_RECEIVER_ACTION_PAUSE = "BROAD_RECEIVER_ACTION_PAUSE";
    private static final String BROAD_RECEIVER_ACTION_PLAY = "BROAD_RECEIVER_ACTION_PLAY";
    private static final String BROAD_RECEIVER_NEXT = "BROAD_RECEIVER_NEXT";
    private static final String BROAD_RECEIVER_PRE = "BROAD_RECEIVER_PRE";

    private final MediaPlayer mediaPlayer = new MediaPlayer();
    //判断有没有MediaPlayer准备过,true为没准备过
    private volatile boolean isPlay = true;
    //判断是否需要释放
    volatile boolean isFirstPre = true;
    //记录歌曲播放到第几首
    //存储播放列表的集合
    //播放播放模式,1为列表播放，2为单循环，3为随机播放
    private int playPattern = 1;
    //记录随机播放的列的几首哥
    private int randomPlay = 0;
    //判断是否准备好了,0是准备好了，1是未准备好
    private boolean isPreSee = false;
    //判断是否需要重新排列随机播放的列表
    boolean isRandom = true;
    //随机播放的列表
    private ArrayList<Integer> randomPlayList = new ArrayList<>();

    public class BroadReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String s = intent.getAction();
            switch (s) {
                case BROAD_RECEIVER_ACTION_PLAY:
                    remoteViews.setImageViewResource(R.id.iv_notification_music_play_service, R.drawable.ic_music_start);
                    Intent intent1 = new Intent();
                    intent1.setAction(BROAD_RECEIVER_ACTION_PAUSE);
                    PendingIntent pending = PendingIntent.getBroadcast(getApplicationContext(), 0, intent1, PendingIntent.FLAG_IMMUTABLE);
                    remoteViews.setOnClickPendingIntent(R.id.iv_notification_music_play_service, pending);
                    notificationManager.notify(1, notification);
                    Intent intent2 = new Intent();
                    intent2.setAction("UPDATE");
                    Bundle bundle = new Bundle();
                    bundle.putString("PLAY", "START_MUSIC");
                    intent2.putExtras(bundle);
                    sendBroadcast(intent2);
                    startMusic(null);
                    break;
                case BROAD_RECEIVER_NEXT:
                    isStartActivity = false;
                    intent2 = new Intent();
                    intent2.setAction("UPDATE");
                    bundle = new Bundle();
                    bundle.putString("PLAY", "BROAD_RECEIVER_NEXT");
                    intent2.putExtras(bundle);
                    sendBroadcast(intent2);
                    nextSong();
                    break;
                case BROAD_RECEIVER_PRE:
                    isStartActivity = false;
                    intent2 = new Intent();
                    intent2.setAction("UPDATE");
                    bundle = new Bundle();
                    bundle.putString("PLAY", "BROAD_RECEIVER_PRE");
                    intent2.putExtras(bundle);
                    sendBroadcast(intent2);
                    preSong();
                    break;
                case BROAD_RECEIVER_ACTION_PAUSE:
                    remoteViews.setImageViewResource(R.id.iv_notification_music_play_service, R.drawable.ic_music_stop);
                    intent1 = new Intent();
                    intent1.setAction(BROAD_RECEIVER_ACTION_PLAY);
                    pending = PendingIntent.getBroadcast(getApplicationContext(), 0, intent1, PendingIntent.FLAG_IMMUTABLE);
                    remoteViews.setOnClickPendingIntent(R.id.iv_notification_music_play_service, pending);
                    notificationManager.notify(1, notification);
                    Intent intent3 = new Intent();
                    intent3.setAction("UPDATE");
                    Bundle bundle1 = new Bundle();
                    bundle1.putString("PLAY", "PAUSE_MUSIC");
                    intent3.putExtras(bundle1);
                    sendBroadcast(intent3);
                    stopMusic();
                    break;
            }
        }
    }


    @Override
    public void startMusic(SongItem songItem) {
        if (isPlay) {
            isPreSee = false;
            mediaPlayer.setOnCompletionListener(this);
            mediaPlayer.setOnErrorListener(this);
            String musicId = songItem.getSongId();
            String url = "https://netease-cloud-music-api-gan.vercel.app/song/url?id=" + musicId;
            String songPlayId = "https://music.163.com/song/media/outer/url?id=" + musicId + ".mp3";
            remoteViews.setTextViewText(R.id.tv_notification_song_name, songItem.getSongName());
            remoteViews.setTextViewText(R.id.tv_notification_singer_name, songItem.getSingerName());
            handler = new Handler(Looper.getMainLooper()) {
                @Override
                public void handleMessage(@NonNull Message msg) {
                    String photoUrl = (String) msg.obj;
                    Picasso.with(getApplicationContext()).load(photoUrl).into(remoteViews, R.id.iv_notification_music_photo, 1, notification);
                }
            };
            notificationManager.notify(1, notification);
            if (Looper.myLooper() == null) {
                Looper.prepare();
            }
            Looper looper = Looper.myLooper();
            Message message = new Message();
            message.obj = songItem.getPicUrl() + "?param=200y200";
            handler.sendMessage(message);
            returnData(url);
            if (isStartActivity) {
                Intent intent = new Intent(MusicService.this, MusicActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("SongList", songItem);
                bundle.putInt("playMode", playPattern);
                intent.putExtras(bundle);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
            handler = new Handler(looper) {
                @Override
                public void handleMessage(@NonNull Message msg) {
                    super.handleMessage(msg);
                    String songUrl = (String) msg.obj;
                    removeMessages(1);
                    try {
                        if (songUrl.equals("null")) {
                            songUrl = songPlayId;
                        }
                        if (isFirstPre) {
                            isFirstPre = false;
                        } else {
                            mediaPlayer.reset();
                        }
                        mediaPlayer.setDataSource(songUrl);//设置音源
                        try {
                            mediaPlayer.prepareAsync();
                        } catch (IllegalStateException exception) {

                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    isPlay = false;
                    mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                        @Override
                        public void onPrepared(MediaPlayer mp) {
                            isPreSee = true;
                            mediaPlayer.start();
                        }
                    });
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
        message.what = 1;
        message.obj = songUrl;
        handler.sendMessage(message);
    }


    @Override
    public void stopMusic() {
        try {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.pause();
            }
        } catch (Exception ignore) {
        }
    }

    @Override
    public void nextSong() {
        if (!isPlay) {
            mediaPlayer.reset();
            isPlay = true;
        }
        if (songNumber == songItemList.size() - 1) {
            songNumber = 0;
        } else {
            songNumber += 1;
        }
        startMusic(songItemList.get(songNumber));
    }

    @Override
    public void preSong() {
        if (!isPlay) {
            mediaPlayer.reset();
            isPlay = true;
        }
        if (songNumber == 0) {
            songNumber = songItemList.size() - 1;
        } else {
            songNumber -= 1;
        }
        startMusic(songItemList.get(songNumber));
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
    public int playModeInt() {
        return playPattern;
    }

    @Override
    public int playMode() {
        //播放播放模式,1为列表播放，2为单循环，3为随机播放
        if (playPattern == 1) {
            playPattern = 2;
            return playPattern;
        }
        if (playPattern == 2) {
            playPattern = 3;
            return playPattern;
        }
        if (playPattern == 3) {
            playPattern = 1;
            return playPattern;
        }
        return playPattern;
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        Log.d("playPattern",String.valueOf(playPattern));
        Intent intent = new Intent();
        intent.setAction("UPDATE");
        Bundle bundle = new Bundle();
        bundle.putInt("playNumber", playPattern);
        intent.putExtras(bundle);
        isStartActivity = false;
        if (playPattern == 1 || playPattern == 2) {
            sendBroadcast(intent);
        }
        if (playPattern == 1) {
            nextSong();
        } else if (playPattern == 2) {
            startMusic(songItemList.get(songNumber));
        } else if (playPattern == 3) {
            if ((randomPlay == songItemList.size()) || isRandom) {
                randomPlayList = new ArrayList<>();
                for (int i = 0; i < songItemList.size(); i++) {
                    randomPlayList.add(i);
                }
                int x;
                int y;
                Random random = new Random();
                for (int i = 0; i < 10; i++) {
                    x = random.nextInt(songItemList.size());
                    y = random.nextInt(songItemList.size());
                    Collections.swap(randomPlayList, x, y);
                }
                randomPlay = 0;
                isRandom = false;
            }
            bundle.putInt("songNumber", randomPlayList.get(randomPlay));
            intent.putExtras(bundle);
            sendBroadcast(intent);
            SongItem songItemRandom = songItemList.get(randomPlayList.get(randomPlay));
            randomPlay++;
            isPlay = true;
            startMusic(songItemRandom);
        }
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        return true;
    }
}