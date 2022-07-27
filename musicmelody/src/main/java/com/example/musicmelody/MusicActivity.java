package com.example.musicmelody;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class MusicActivity extends AppCompatActivity {
    private ImageView mSongImageView,mPlayImageView;
    private TextView mTextView1,mTextView2;
    private Toolbar mToolbar;
    private SeekBar mSeekBar;
    private MusicService.MusicPlay musicPlay;
    private ImageView mivNextSong,mivPreSong,mivPlayMode;
    private SongItem songItem;
    boolean isTime=false;
    //判断歌曲是否有在播放
    static boolean play=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music);
        //设置状态栏的背景颜色和字体颜色
        getWindow().setStatusBarColor(Color.rgb(255,255,255));
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        Intent bindIntent=new Intent(this,MusicService.class);
        bindService(bindIntent,connection,BIND_AUTO_CREATE);
        //初始化控件
        initControl();
        //接收歌曲信息
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        songItem= (SongItem) bundle.getSerializable("SongList");
        int playMode=bundle.getInt("playMode");
        Log.d("zwui",String.valueOf(playMode));
        switch(playMode){
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
        //加载歌曲的名字和歌手名字
        mTextView1.setText(songItem.getSongName()+"-");
        mTextView2.setText(songItem.getSingerName());
        //加载歌曲图片
        Log.d("现在的线程为：", Thread.currentThread().getName());
        Picasso.with(this).load(songItem.getPicUrl()).placeholder(R.drawable.ic_music_start).into(mSongImageView);
        //给界面的返回键设置返回事件
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        initSongFunction();
    }

    private void initSongFunction() {
        mPlayImageView.setSelected(true);
        mPlayImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!play){
                    //写音乐播放事件
                    mPlayImageView.setSelected(true);
                    play=true;
                    HttpUtil.cachedThreadPool.execute(new Runnable() {
                        @Override
                        public void run() {
                            musicPlay.startMusic(songItem);
                            startProgress();
                        }
                    });
                }else {
                    //音乐暂停
                    mPlayImageView.setSelected(false);
                    play=false;
                    Log.d("现在的线程为：", Thread.currentThread().getName());
                    musicPlay.stopMusic();
                    stopProgress();
                }
            }
        });
        mivNextSong.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mivNextSong.setSelected(true);
                mivNextSong.setSelected(false);
                stopProgress();
                finish();
                HttpUtil.cachedThreadPool.execute(new Runnable() {
                    @Override
                    public void run() {
                        musicPlay.nextSong();
                    }
                });
                mPlayImageView.setSelected(true);
                play=true;
                startProgress();
            }
        });
        mivPreSong.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mivPreSong.setSelected(true);
                mivPreSong.setSelected(false);
                stopProgress();
                finish();
                HttpUtil.cachedThreadPool.execute(new Runnable() {
                    @Override
                    public void run() {
                        musicPlay.preSong();
                    }
                });
                mPlayImageView.setSelected(true);
                play=true;
                startProgress();
            }
        });
        mivPlayMode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //播放播放模式,1为列表播放，2为单循环，3为随机播放
                int playInt=musicPlay.playMode();
                switch(playInt){
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

            }
        });
    }

    private void initControl() {
        mSeekBar=findViewById(R.id.seekbar_service);
        mSongImageView=findViewById(R.id.iv_music_photo_service);
        mPlayImageView=findViewById(R.id.iv_music_play_service);
        mToolbar=findViewById(R.id.too_bar_service);
        mTextView1=findViewById(R.id.tv1_toolbar);
        mTextView2=findViewById(R.id.tv2_toolbar);
        mSeekBar=findViewById(R.id.seekbar_service);
        mivNextSong=findViewById(R.id.iv_next_song_service);
        mivPreSong=findViewById(R.id.iv_pre_song_service);
        mivPlayMode=findViewById(R.id.iv_play_mode_service);
    }

    private final Runnable r = new Runnable() {
        @Override
        public void run() {
            while (isTime){
                try {
                    mSeekBar.setMax(musicPlay.getMusicTotalTime());
                    Thread.sleep(70);
                    mSeekBar.setProgress(musicPlay.getMusicCurrentTime());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    };

    private void startProgress(){
        isTime = true;
        HttpUtil.cachedThreadPool.execute(r);
    }

    private void stopProgress(){
        isTime = false;
    }

    private ServiceConnection connection=new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            musicPlay=(MusicService.MusicPlay)service;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

}