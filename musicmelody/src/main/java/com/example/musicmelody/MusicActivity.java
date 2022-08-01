package com.example.musicmelody;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class MusicActivity extends AppCompatActivity {
    private ImageView mSongImageView, mPlayImageView;
    private TextView mTextView1, mTextView2, mTextViewCurrent, mTextViewTotal;
    private Toolbar mToolbar;
    private SeekBar mSeekBar;
    private MusicService.MusicPlay musicPlay;
    private ImageView mivNextSong, mivPreSong, mivPlayMode;
    SongItem songItem;
    private boolean isTime = false;
    //判断歌曲是否有在播放
    private boolean play = false;
    //
    private List<SongItem> songItemList = new ArrayList<>();
    private int number;
    private MusicService musicService;
    private MusicBroadReceiver musicBroadReceiver;
    boolean isSetTotalTime = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music);
        songItemList = MusicService.songItemList;
        number = MusicService.songNumber;
        //设置状态栏的背景颜色和字体颜色
        getWindow().setStatusBarColor(Color.rgb(255, 255, 255));
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        Intent bindIntent = new Intent(this, MusicService.class);
        bindService(bindIntent, connection, BIND_AUTO_CREATE);
        //初始化控件
        initControl();
        //接收歌曲信息
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        songItem = (SongItem) bundle.getSerializable("SongList");
        int playMode = bundle.getInt("playMode");
        updateView();
        //给界面的返回键设置返回事件
        //播放播放模式,1为列表播放，2为单循环，3为随机播放
        switch (playMode) {
            case 1:
                mivPlayMode.setImageResource(R.drawable.ic_list_play);
                break;
            case 2:
                mivPlayMode.setImageResource(R.drawable.ic_loop_playback);
                break;

            case 3:
                mivPlayMode.setImageResource(R.drawable.ic_random_play);
                break;
        }
        initSongFunction();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("UPDATE");
        musicBroadReceiver = new MusicBroadReceiver();
        registerReceiver(musicBroadReceiver, intentFilter);
    }

    private void initSongFunction() {
        mPlayImageView.setSelected(true);
        mPlayImageView.setOnClickListener(v -> {
            MusicService.isStartActivity = false;
            if (!play) {
                //写音乐播放事件
                mPlayImageView.setSelected(true);
                play = true;
                Intent intent = new Intent();
                intent.setAction("PlayStart");
                sendBroadcast(intent);
                Util.cachedThreadPool.execute(() -> {
                    musicService.startMusic(songItemList.get(number));
                    startProgress();
                });
            } else {
                //音乐暂停
                mPlayImageView.setSelected(false);
                play = false;
                Intent intent = new Intent();
                intent.setAction("PlayPause");
                sendBroadcast(intent);
                musicService.stopMusic();
                stopProgress();
            }
        });
        mivNextSong.setOnClickListener(v -> {
            mivNextSong.setSelected(true);
            mivNextSong.setSelected(false);
            stopProgress();
            if (number == songItemList.size() - 1) {
                number = 0;
            } else {
                number += 1;
            }
            updateView();
            Util.cachedThreadPool.execute(() -> {
                MusicService.isStartActivity = false;
                musicService.nextSong();
            });
            mPlayImageView.setSelected(true);
            play = true;
            startProgress();
        });
        mivPreSong.setOnClickListener(v -> {
            mivPreSong.setSelected(true);
            mivPreSong.setSelected(false);
            stopProgress();
            if (number == 0) {
                number = songItemList.size() - 1;
            } else {
                number -= 1;
            }
            updateView();
            Util.cachedThreadPool.execute(() -> {
                MusicService.isStartActivity = false;
                musicService.preSong();
            });
            mPlayImageView.setSelected(true);
            play = true;
            startProgress();
        });
        mivPlayMode.setOnClickListener(v -> {
            //播放播放模式,1为列表播放，2为单循环，3为随机播放
            int playInt = musicService.playMode();
            switch (playInt) {
                case 1:
                    mivPlayMode.setImageResource(R.drawable.ic_list_play);
                    Toast.makeText(getApplicationContext(), "列表播放", Toast.LENGTH_SHORT).show();
                    break;
                case 2:
                    mivPlayMode.setImageResource(R.drawable.ic_loop_playback);
                    Toast.makeText(getApplicationContext(), "单曲循环", Toast.LENGTH_SHORT).show();
                    break;
                case 3:
                    mivPlayMode.setImageResource(R.drawable.ic_random_play);
                    Toast.makeText(getApplicationContext(), "随机播放", Toast.LENGTH_SHORT).show();
                    break;
            }
        });
    }

    private void initControl() {
        mSeekBar = findViewById(R.id.seekbar_service);
        mSongImageView = findViewById(R.id.iv_music_photo_service);
        mPlayImageView = findViewById(R.id.iv_music_play_service);
        mToolbar = findViewById(R.id.too_bar_service);
        mTextView1 = findViewById(R.id.tv1_toolbar);
        mTextView2 = findViewById(R.id.tv2_toolbar);
        mSeekBar = findViewById(R.id.seekbar_service);
        mivNextSong = findViewById(R.id.iv_next_song_service);
        mivPreSong = findViewById(R.id.iv_pre_song_service);
        mivPlayMode = findViewById(R.id.iv_play_mode_service);
        mTextViewCurrent = findViewById(R.id.tv_service_current_time);
        mTextViewTotal = findViewById(R.id.tv_service_total_time);
    }

    public class MusicBroadReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            //播放播放模式,1为列表播放，2为单曲循环，3为随机播放
            Bundle bundle = intent.getExtras();
            int playMode = bundle.getInt("playNumber");
            if (playMode == 1) {
                stopProgress();
                number = MusicService.songNumber;
                updateView();
                mPlayImageView.setSelected(true);
                play = true;
                isSetTotalTime = true;
                startProgress();
            } else if (playMode == 3) {
                number = bundle.getInt("songNumber");
                MusicService.songNumber = number;
                updateView();
                mPlayImageView.setSelected(true);
                play = true;
                isSetTotalTime = true;
                startProgress();
            }
            String s = bundle.getString("PLAY");
            if (s != null) {
                switch (s) {
                    case "START_MUSIC":
                        mPlayImageView.setSelected(true);
                        play = true;
                        startProgress();
                        break;
                    case "PAUSE_MUSIC":
                        mPlayImageView.setSelected(false);
                        play = false;
                        stopProgress();
                        break;
                    case "BROAD_RECEIVER_PRE":
                        if (number == 0) {
                            number = songItemList.size() - 1;
                        } else {
                            number -= 1;
                        }
                        updateView();
                        mPlayImageView.setSelected(true);
                        play = true;
                        startProgress();
                        isSetTotalTime = true;
                        break;
                    case "BROAD_RECEIVER_NEXT":
                        if (number == songItemList.size() - 1) {
                            number = 0;
                        } else {
                            number += 1;
                        }
                        updateView();
                        mPlayImageView.setSelected(true);
                        play = true;
                        startProgress();
                        isSetTotalTime = true;
                        break;
                }
            }
        }
    }

    private void updateView() {
        //加载歌曲的名字和歌手名字
        mTextView1.setText(songItemList.get(number).getSongName() + "-");
        mTextView2.setText(songItemList.get(number).getSingerName());
        //加载歌曲图片
        Picasso.with(this).load(songItemList.get(number).getPicUrl()).placeholder(R.drawable.ic_music_start).into(mSongImageView);
    }

    private Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            mTextViewTotal.setText(Util.format(musicService.getMusicTotalTime()));
            mTextViewCurrent.setText(Util.format(musicService.getMusicCurrentTime()));
        }
    };

    private final Runnable r = new Runnable() {
        @Override
        public void run() {
            while (isTime) {
                try {
                    mSeekBar.setMax(musicService.getMusicTotalTime());
                    Thread.sleep(70);
                    mSeekBar.setProgress(musicService.getMusicCurrentTime());
                    Message message = new Message();
                    handler.sendMessage(message);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    };

    private void startProgress() {
        isTime = true;
        Util.cachedThreadPool.execute(r);
    }

    private void stopProgress() {
        isTime = false;
    }

    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            musicPlay = (MusicService.MusicPlay) service;
            musicService = musicPlay.getService();
            mToolbar.setNavigationOnClickListener(view -> {
                stopProgress();
                finish();
            });
            startProgress();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(musicBroadReceiver);
        stopProgress();
    }
}