package com.example.wangyiyun.view;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;

import android.os.Looper;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;


import com.example.wangyiyun.Contacts.ContactClass;
import com.example.wangyiyun.R;
import com.example.wangyiyun.ViewByMyself.WaterFlowLayout;
import com.example.wangyiyun.adapter.SearchRecyclerViewAdapter;
import com.example.wangyiyun.entries.HotSearchItem;
import com.example.wangyiyun.entries.SongItem;
import com.example.wangyiyun.presenter.SearchPresenter;
import com.example.wangyiyun.utils.ListChangeUtil;

import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends AppCompatActivity implements ContactClass.IView ,ContactClass.IView2{
    //布局控件
    private EditText mEditText;
    private Button mButton;
    private WaterFlowLayout mWaterFlowLayout;
    private LinearLayout mLinearLayout,mLinearLayout1;
    private ProgressBar mProgressBar;
    private RecyclerView mRecyclerView;
    //数据相关
    //储存传递过来的热搜词集合
    List<HotSearchItem> hotSearchItems;
    //搜索后传过来的数据
    List<SongItem> songItems;
    List<SongItem> totalSongItems=new ArrayList<>();
    SearchPresenter searchPresenter;
    //标记低第几页
    int page=0;
    private String searchWord;
    //recyclerview的适配器
    private SearchRecyclerViewAdapter searchRecyclerViewAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        searchPresenter=new SearchPresenter(this,this);
        initControl();
        LinearLayoutManager layoutManager=new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(this,DividerItemDecoration.VERTICAL));
        mLinearLayout.setVisibility(View.GONE);
        searchPresenter.bridge();
        controlFunction();
    }

    //初始化控件
    private void initControl() {
        mButton=findViewById(R.id.bt_search);
        mEditText=findViewById(R.id.et_search);
        mWaterFlowLayout=findViewById(R.id.fl_search);
        mLinearLayout=findViewById(R.id.lly1);
        mProgressBar=findViewById(R.id.pb_serrch);
        mRecyclerView=findViewById(R.id.search_activity_recyclerview);
        mLinearLayout1=findViewById(R.id.ly_history);
    }

    //控件的功能
    private void controlFunction() {
        mButton.setOnClickListener(v -> finish());
        mEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                //这里写事件，返回为true，即为搜索键的事件
                searchWord=mEditText.getText().toString();
                Log.d("zwyz",searchWord);
                searchPresenter.searchWord(searchWord,0);
                mProgressBar.setVisibility(View.VISIBLE);
                mLinearLayout.setVisibility(View.GONE);
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
        hotSearchItems=(ArrayList<HotSearchItem>)dataList;
        for (int i = 0; i < hotSearchItems.size(); i++) {
            Button bt =new Button(this);
            String s = String.valueOf(i+1);
            bt.setText(s+"."+hotSearchItems.get(i).getSearchWord());
            if(i<3){
                bt.setTextColor(getResources().getColor(R.color.big_red));
            }
            bt.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_START);
            bt.setPadding(50,0,0,0);
            bt.setBackgroundColor(getResources().getColor(R.color.gray_s));
            mLinearLayout.addView(bt);
            bt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mLinearLayout.setVisibility(View.GONE);
                    mProgressBar.setVisibility(View.VISIBLE);
                    page=0;
                    searchWord=bt.getText().toString();
                    searchPresenter.searchWord(searchWord,0);
                }
            });
        }
        mProgressBar.setVisibility(View.GONE);
        mLinearLayout.setVisibility(View.VISIBLE);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void getData2(List<?> dataList) {
        songItems=(ArrayList<SongItem>)dataList;
        Log.d("zwyss",songItems.get(0).getSongName());
        totalSongItems.addAll(songItems);
        mProgressBar.setVisibility(View.GONE);
        mLinearLayout1.setVisibility(View.GONE);
        if(page==0){
            mLinearLayout.setVisibility(View.GONE);
            searchRecyclerViewAdapter=new SearchRecyclerViewAdapter(getApplicationContext(),songItems);
            mRecyclerView.setAdapter(searchRecyclerViewAdapter);
        }else {
            searchRecyclerViewAdapter.updateData(songItems);
        }
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                LinearLayoutManager manager=(LinearLayoutManager) recyclerView.getLayoutManager();
                // 当不滑动时
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    //获取最后一个完全显示的itemPosition
                    int lastItemPosition = manager.findLastCompletelyVisibleItemPosition();
                    int itemCount = manager.getItemCount();
                    // 判断是否滑动到了最后一个item，并且是向上滑动
                    if (lastItemPosition == (itemCount - 1) ) {
                        //加载更多
                        page+=1;
                        searchPresenter.searchWord(searchWord,page);
                    }
                }
            }
        });
    }
}