package com.example.wangyiyun.view;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;

import com.example.wangyiyun.R;
import com.example.wangyiyun.utils.HttpUtil;

import java.io.IOException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    private ViewPager2 mViewPaper2;
    private ImageView mivUser,mivMusic,ivCurrent;
    private LinearLayout llUser,llMusic;
    private Toolbar toolbar;
    static MediaPlayer  sMediaPlayer = new MediaPlayer();
    private Button btpPlay, btPause;
    SeekBar seekBar;
    boolean isPlay=true;
    boolean isTime=false;

    private final Runnable r = new Runnable() {
        @Override
        public void run() {
            while (isTime && sMediaPlayer.isPlaying()){
                try {
                    Thread.sleep(100);
                    seekBar.setProgress(sMediaPlayer.getCurrentPosition());
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
        initPaper();
        initTabView();
        toolbar=findViewById(R.id.main_too_bar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(MainActivity.this,SearchActivity.class);//给后面开启的活动传值
                startActivity(intent);
            }
        });

        sMediaPlayer.setOnPreparedListener((mediaPlayer -> {
            mediaPlayer.start();
            seekBar.setMax(sMediaPlayer.getDuration());
            startProgress();
        }));

        btpPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HttpUtil.cachedThreadPool.execute(new Runnable() {
                    @Override
                    public void run() {
                        if(!sMediaPlayer.isPlaying()){
                            if(isPlay){
                                try {
                                    String threadName = Thread.currentThread().getName();
                                    Log.v("zwy", "线程：" + threadName );
                                    sMediaPlayer.setDataSource("https://music.163.com/song/media/outer/url?id=1963064332.mp3");//设置音源
                                    sMediaPlayer.prepareAsync();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                isPlay=false;
                            }else {
                                sMediaPlayer.start();
                            }
                            startProgress();
                        }
                    }
                });
            }
        });
        btPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(sMediaPlayer.isPlaying()){
                    sMediaPlayer.pause();
                    stopProgress();
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        startProgress();
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopProgress();
    }

    private void startProgress(){
        isTime = true;
        HttpUtil.cachedThreadPool.execute(r);
    }

    private void stopProgress(){
        isTime = false;
    }

    private void initPaper() {
        btpPlay =findViewById(R.id.play);
        btPause =findViewById(R.id.pause);
        seekBar=findViewById(R.id.seekbar);
        mViewPaper2=findViewById(R.id.view_paper_main);
        ArrayList<Fragment> fragmentList=new ArrayList<>();
        fragmentList.add(MusicFragment.newInstance("1","2"));
        fragmentList.add(UserFragment.newInstance("1","2"));
        MainFragmentAdapter mainFragmentAdapter=new MainFragmentAdapter(getSupportFragmentManager(),getLifecycle(),fragmentList);
        mViewPaper2.setAdapter(mainFragmentAdapter);
        //禁止ViewPaper2的滑动
        //viewPager.setUserInputEnabled(false);
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

    public void musicPlay(){

    }

}