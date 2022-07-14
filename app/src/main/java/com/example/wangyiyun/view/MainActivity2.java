package com.example.wangyiyun.view;

import androidx.appcompat.app.AppCompatActivity;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.wangyiyun.R;

import java.io.IOException;

/***
 * 该Demo是演示MediaPlayer播放网络音频
 */
public class MainActivity2 extends AppCompatActivity implements View.OnClickListener {

    private Button start;
    private Button pause;

    private MediaPlayer mediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        init();
        initMediaPlayer();
    }

    private void initMediaPlayer() {
        try {
            mediaPlayer.setDataSource("http://www.ytmp3.cn/down/57799.mp3");
            mediaPlayer.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void init() {
        start = findViewById(R.id.start);
        pause = findViewById(R.id.pause);
        start.setOnClickListener(this);
        pause.setOnClickListener(this);
        mediaPlayer = new MediaPlayer();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.start:
                if (!mediaPlayer.isPlaying()) {
                    System.out.println("我我 我我我我我 我"+mediaPlayer.getDuration());
                    mediaPlayer.start();
                }
                break;
            case R.id.pause:
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.pause();
                }
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
        }
    }
}
//音乐地址：http://www.ytmp3.cn/down/57799.mp3