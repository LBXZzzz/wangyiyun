package com.example.wangyiyun.view;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;

import android.os.IBinder;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;


import com.example.musicmelody.MusicService;
import com.example.wangyiyun.contacts.ContactClass;
import com.example.wangyiyun.R;
import com.example.wangyiyun.ViewByMyself.WaterFlowLayout;
import com.example.wangyiyun.adapter.SearchRecyclerViewAdapter;
import com.example.wangyiyun.entries.HotSearchItem;
import com.example.wangyiyun.entries.SongItem;
import com.example.wangyiyun.presenter.SearchPresenter;

import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends AppCompatActivity implements ContactClass.IView, ContactClass.IView2, ContactClass.IView3 {
    //布局控件
    private EditText mEditText;
    private Button mButton;
    private WaterFlowLayout mWaterFlowLayout;
    private LinearLayout mLinearLayout, mLinearLayout1, mLinearLayout2;
    private ProgressBar mProgressBar;
    private RecyclerView mRecyclerView;
    //数据相关
    //储存传递过来的热搜词集合
    List<HotSearchItem> hotSearchItems;
    //搜索后传过来的数据
    ArrayList<SongItem> songItems = new ArrayList<>();
    ArrayList<SongItem> totalSongItems = new ArrayList<>();
    SearchPresenter searchPresenter;
    //标记低第几页
    int page = 1;
    private String searchWord;
    private String songUrl;
    //recyclerview的适配器
    private SearchRecyclerViewAdapter searchRecyclerViewAdapter;
    MusicService.MusicPlay musicPlay;
    MusicService musicService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        getWindow().setStatusBarColor(Color.rgb(255, 255, 255));
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);//实现状态栏图标和文字颜色为暗色
        //绑定服务
        //判断当前版本是否支持前台服务，不支持则开启后台服务
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(new Intent(getApplicationContext(), MusicService.class));
        } else {
            startService(new Intent(getApplicationContext(), MusicService.class));
        }
        Intent bindIntent = new Intent(this, MusicService.class);
        bindService(bindIntent, connection, BIND_AUTO_CREATE);
        searchPresenter = new SearchPresenter(this, this, this);
        initControl();
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(layoutManager);
        mLinearLayout.setVisibility(View.GONE);
        mLinearLayout2.setVisibility(View.GONE);
        searchPresenter.bridge();
        controlFunction();
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                LinearLayoutManager manager = (LinearLayoutManager) recyclerView.getLayoutManager();
                // 当不滑动时
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    //获取最后一个完全显示的itemPosition
                    int lastItemPosition = manager.findLastCompletelyVisibleItemPosition();
                    int itemCount = manager.getItemCount();
                    // 判断是否滑动到了最后一个item，并且是向上滑动
                    if (lastItemPosition == (itemCount - 1)) {
                        //加载更多
                        page += 30;
                        searchPresenter.searchWord(searchWord, page);
                    }
                }
            }
        });
    }

    //初始化控件
    private void initControl() {
        mButton = findViewById(R.id.bt_search);
        mEditText = findViewById(R.id.et_search);
        mWaterFlowLayout = findViewById(R.id.fl_search);
        mLinearLayout = findViewById(R.id.lly1);
        mProgressBar = findViewById(R.id.pb_serrch);
        mRecyclerView = findViewById(R.id.search_activity_recyclerview);
        mLinearLayout1 = findViewById(R.id.ly_history);
        mLinearLayout2 = findViewById(R.id.lly_search_recyclerview);
    }

    //控件的功能
    private void controlFunction() {
        mButton.setOnClickListener(v -> finish());
        mEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                //这里写事件，返回为true，即为搜索键的事件
                searchWord = mEditText.getText().toString();
                searchPresenter.searchWord(searchWord, 0);
                mProgressBar.setVisibility(View.VISIBLE);
                mLinearLayout.setVisibility(View.GONE);
                songItems = new ArrayList<>();
                totalSongItems = new ArrayList<>();
                page = 1;
                //点击回车后自动收起键盘
                InputMethodManager manager = (InputMethodManager) getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                if (manager != null)
                    manager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                return true;
            }
        });
    }

    @SuppressWarnings("unchecked")
    @Override
    public void getData(List<?> dataList) {
        hotSearchItems = (ArrayList<HotSearchItem>) dataList;
        for (int i = 0; i < hotSearchItems.size(); i++) {
            Button bt = new Button(this);
            bt.setText(hotSearchItems.get(i).getSearchWord());
            if (i < 3) {
                bt.setTextColor(getResources().getColor(R.color.big_red));
            }
            bt.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_START);
            bt.setPadding(50, 0, 0, 0);
            bt.setBackgroundColor(getResources().getColor(R.color.gray_s));
            mLinearLayout.addView(bt);
            bt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mLinearLayout.setVisibility(View.GONE);
                    mProgressBar.setVisibility(View.VISIBLE);
                    page = 1;
                    songItems = new ArrayList<>();
                    totalSongItems = new ArrayList<>();
                    searchWord = bt.getText().toString();
                    searchPresenter.searchWord(searchWord, 0);
                }
            });
        }
        mProgressBar.setVisibility(View.GONE);
        mLinearLayout.setVisibility(View.VISIBLE);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void getData2(List<?> dataList) {
        songItems = new ArrayList<>();
        songItems = (ArrayList<SongItem>) dataList;
        totalSongItems.addAll(songItems);
        mProgressBar.setVisibility(View.GONE);
        mLinearLayout1.setVisibility(View.GONE);
        mLinearLayout2.setVisibility(View.VISIBLE);
        if (page == 1) {
            mLinearLayout.setVisibility(View.GONE);
            searchRecyclerViewAdapter = new SearchRecyclerViewAdapter(getApplicationContext(), songItems);
            mRecyclerView.setAdapter(searchRecyclerViewAdapter);
        } else {
            searchRecyclerViewAdapter.updateData(songItems);
        }
        searchRecyclerViewAdapter.setOnItemClickListener(new ContactClass.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                String singerName = totalSongItems.get(position).getSingerName();
                String songName = totalSongItems.get(position).getSongName();
                String songId = totalSongItems.get(position).getSongId();
                String picUrl = totalSongItems.get(position).getPicUrl();
                com.example.musicmelody.SongItem songItem = new com.example.musicmelody.SongItem(singerName, songName, songId, picUrl);
                MusicService.songItemList.add(songItem);
                MusicService.isStartActivity=true;
                MusicService.songNumber = MusicService.songItemList.size()-1;
                musicService.openMusic(MusicService.songItemList.get(MusicService.songNumber));
            }
        });
    }

    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            musicPlay = (MusicService.MusicPlay) service;
            musicService=musicPlay.getService();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    @Override
    public void getData(String dataString, String dataString2) {
        String songPlayId;
        if (dataString.equals("null")) {
            songPlayId = dataString2;
        } else {
            songPlayId = dataString;
            Toast.makeText(SearchActivity.this, "VIP歌曲，试听30秒", Toast.LENGTH_SHORT).show();
        }
    }
}