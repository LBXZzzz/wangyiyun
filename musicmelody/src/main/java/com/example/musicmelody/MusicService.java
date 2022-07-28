
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
import java.util.List;
import java.util.Random;

public class MusicService extends Service {

    //存歌曲的ID
    public static List<SongItem> songItemList = new ArrayList<>();
    private final MusicPlay musicPlay = new MusicPlay(songItemList);
    public static int songNumber = 0;
    public static boolean isStartActivity =true;
    //存随机播放的歌曲信息
    private static List<SongItem> songItemListRandom = new ArrayList<>();
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



    public class MusicPlay extends Binder implements IMusic, MediaPlayer.OnCompletionListener, MediaPlayer.OnErrorListener {
        private final MediaPlayer mediaPlayer = new MediaPlayer();
        //判断有没有MediaPlayer准备过,true为没准备过
        private volatile boolean isPlay = true;
        //判断是否需要释放
        volatile boolean isFirstPre = true;
        //记录歌曲播放到第几首
        //存储播放列表的集合
        private List<SongItem> songList;
        //播放播放模式,1为列表播放，2为单循环，3为随机播放
        private int playPattern = 1;
        //判断是否准备好了,0是准备好了，1是未准备好
        private boolean isPreSee = false;

        public MusicPlay(List<SongItem> songList) {
            this.songList = songList;
        }

        public class BroadReceiver extends BroadcastReceiver{

            @Override
            public void onReceive(Context context, Intent intent) {
               mediaPlayer.start();
            }
        }

        @Override
        public void startMusic(SongItem songItem) {
            if (isPlay) {
                isPreSee = false;
                mediaPlayer.setOnCompletionListener(this);
                mediaPlayer.setOnErrorListener(this);
                String musicId = songItem.getSongId();
                String url = "https://netease-cloud-music-api-4eodv9lwk-tangan91314.vercel.app/song/url?id=" + musicId;
                String songPlayId = "https://music.163.com/song/media/outer/url?id=" + musicId + ".mp3";
                //更新通知
                IntentFilter intentFilter = new IntentFilter();
                intentFilter.addAction("MUSIC");
                MusicPlay musicPlay=new MusicPlay(null);
                MusicPlay.BroadReceiver broadReceiver=musicPlay.new BroadReceiver();
                registerReceiver(broadReceiver, intentFilter);
                Intent intent1 = new Intent();
                intent1.setAction("MUSIC");
                PendingIntent pending = PendingIntent.getBroadcast(getApplicationContext(),0,intent1,PendingIntent.FLAG_IMMUTABLE);
                remoteViews.setOnClickPendingIntent(R.id.iv_notification_music_play_service,pending);
                remoteViews.setTextViewText(R.id.tv_notification_song_name, songItem.getSongName());
                remoteViews.setTextViewText(R.id.tv_notification_singer_name, songItem.getSingerName());
                handler = new Handler(Looper.getMainLooper()) {
                    @Override
                    public void handleMessage(@NonNull Message msg) {
                        String photoUrl = (String) msg.obj;
                        Picasso.with(getApplicationContext()).load(photoUrl).into(remoteViews, R.id.iv_notification_music_photo, 1, notification);
                    }
                };
                Message message = new Message();
                message.obj = songItem.getPicUrl() + "?param=200y200";
                handler.sendMessage(message);
                notificationManager.notify(1, notification);
                returnData(url);
                if (Looper.myLooper() == null) {
                    Looper.prepare();
                }
                Looper looper = Looper.myLooper();
                if(isStartActivity){
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
            Intent intent = new Intent();
            intent.setAction("UPDATE");
            Bundle bundle = new Bundle();
            bundle.putInt("playNumber",playPattern);
            intent.putExtras(bundle);
            if(playPattern==1||playPattern == 2){
                sendBroadcast(intent);
            }
            isStartActivity=false;
            if (playPattern == 1) {
                nextSong();
            } else if (playPattern == 2) {
                startMusic(songList.get(songNumber));
            } else if (playPattern == 3) {
                Random random = new Random();
                int x=random.nextInt(songList.size());
                bundle.putInt("songNumber",x);
                intent.putExtras(bundle);
                sendBroadcast(intent);
                SongItem songItemRandom = songList.get(x);
                isPlay = true;
                startMusic(songItemRandom);
            }
        }

        @Override
        public boolean onError(MediaPlayer mp, int what, int extra) {
            return true;
        }
    }

}