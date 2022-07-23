package com.example.wangyiyun.view;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;

import com.example.musicmelody.IMusic;
import com.example.musicmelody.MusicPlayOk;
import com.example.musicmelody.MusicService;
import com.example.wangyiyun.R;
import com.example.wangyiyun.utils.HttpUtil;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    private ViewPager2 mViewPaper2;
    private ImageView mivUser,ivCurrent,mivMusic;
    public static ImageView mivMusicPlay;
    private LinearLayout llUser,llMusic;
    private Toolbar toolbar;
    private ImageButton ibNextSong,ibPreSong;
    SeekBar seekBar;
    boolean isTime=false;
    //判断歌曲是否有在播放
    static boolean play=false;
    static List<String> songList=new ArrayList<>();
    static MusicService.MusicPlay musicPlay;

    private final Runnable r = new Runnable() {
        @Override
        public void run() {
            while (isTime){
                try {
                    //seekBar.setMax(iMusic.getMusicTotalTime());
                    Thread.sleep(70);
                    //seekBar.setProgress(iMusic.getMusicCurrentTime());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        songList.add("https://music.163.com/song/media/outer/url?id=32192436.mp3");
        songList.add("https://music.163.com/song/media/outer/url?id=415792881.mp3");
        songList.add("https://music.163.com/song/media/outer/url?id=516657051.mp3");
        songList.add("https://music.163.com/song/media/outer/url?id=468517654.mp3");
        songList.add("https://music.163.com/song/media/outer/url?id=1498342485.mp3");
        MusicService.songList=songList;
        //绑定服务
        //判断当前版本是否支持前台服务，不支持则开启后台服务
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(new Intent(getApplicationContext(), MusicService.class));
        }else {
            startService(new Intent(getApplicationContext(), MusicService.class));
        }
    Intent bindIntent=new Intent(this,MusicService.class);
        bindService(bindIntent,connection,BIND_AUTO_CREATE);
        //设置状态栏的背景颜色和字体颜色
        getWindow().setStatusBarColor(Color.rgb(255,255,255));
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);//实现状态栏图标和文字颜色为暗色
        initPaper();
        initTabView();
        mivMusicPlay=findViewById(R.id.iv_music_play);
        toolbar=findViewById(R.id.main_too_bar);
        ibNextSong =findViewById(R.id.ib_next_song);
        ibPreSong=findViewById(R.id.ib_pre_song);
        ibNextSong.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopProgress();
                musicPlay.nextSong();
                mivMusicPlay.setSelected(true);
                play=true;
                startProgress();
            }
        });
        ibPreSong.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopProgress();
                musicPlay.preSong();
                mivMusicPlay.setSelected(true);
                play=true;
                startProgress();
            }
        });
        mivMusicPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!play){
                    //写音乐播放事件
                    mivMusicPlay.setSelected(true);
                    play=true;
                    HttpUtil.cachedThreadPool.execute(new Runnable() {
                        @Override
                        public void run() {
                            musicPlay.startMusic("https://music.163.com/song/media/outer/url?id=1964644539.mp3");
                            startProgress();
                        }
                    });
                }else {
                    //音乐暂停
                    mivMusicPlay.setSelected(false);
                    play=false;
                    musicPlay.stopMusic();
                    stopProgress();
                }

            }
        });

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(MainActivity.this,SearchActivity.class);//给后面开启的活动传值
                startActivity(intent);
            }
        });
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


    private void startProgress(){
        isTime = true;
        HttpUtil.cachedThreadPool.execute(r);
    }

    private void stopProgress(){
        isTime = false;
    }

    private void initPaper() {
        seekBar=findViewById(R.id.seekbar);
        mViewPaper2=findViewById(R.id.view_paper_main);
        ArrayList<Fragment> fragmentList=new ArrayList<>();
        fragmentList.add(MusicFragment.newInstance("1","2"));
        fragmentList.add(UserFragment.newInstance("1","2"));
        MainFragmentAdapter mainFragmentAdapter=new MainFragmentAdapter(getSupportFragmentManager(),getLifecycle(),fragmentList);
        mViewPaper2.setAdapter(mainFragmentAdapter);
        mViewPaper2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {//滚动的动画
                super.onPageScrolled(position, positionOffset, positionOffsetPixels);
            }

            @Override
            public void onPageSelected(int position) {//页面选择了之后，实现响应事件
                super.onPageSelected(position);
                changeTab(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                super.onPageScrollStateChanged(state);
            }
        });
    }

    private void initTabView() {
        llUser=findViewById(R.id.user);
        llMusic=findViewById(R.id.music);
        llUser.setOnClickListener(this);
        llMusic.setOnClickListener(this);
        mivMusic=findViewById(R.id.music_photo);
        mivUser=findViewById(R.id.user_photo);
        mivMusic.setSelected(true);
        ivCurrent=mivMusic;
    }

    private void changeTab(int position) {
        ivCurrent.setSelected(false);
        switch (position){
            case R.id.music:
                mViewPaper2.setCurrentItem(0);
            case 0:
                mivMusic.setSelected(true);
                ivCurrent=mivMusic;
                break;
            case R.id.user:
                mViewPaper2.setCurrentItem(1);
            case 1:
                ivCurrent=mivUser;
                mivUser.setSelected(true);
                break;
        }
    }

    @Override
    public void onClick(View view) {
        changeTab(view.getId());
    }
}