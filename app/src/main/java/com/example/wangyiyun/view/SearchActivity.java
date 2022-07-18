package com.example.wangyiyun.view;

import androidx.appcompat.app.AppCompatActivity;


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
import android.widget.TextView;


import com.example.wangyiyun.Contacts.ContactClass;
import com.example.wangyiyun.R;
import com.example.wangyiyun.ViewByMyself.WaterFlowLayout;
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
    private LinearLayout mLinearLayout;
    //数据相关
    //储存传递过来的热搜词集合
    List<HotSearchItem> hotSearchItems;
    //搜索后传过来的数据
    List<SongItem> songItems;
    SearchPresenter searchPresenter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        searchPresenter=new SearchPresenter(this,this);
        initControl();
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
    }

    //控件的功能
    private void controlFunction() {
        mButton.setOnClickListener(v -> finish());
        mEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                //这里写事件，返回为true，即为搜索键的事件
                String searchWord=mEditText.getText().toString();
                Log.d("zwyz",searchWord);
                searchPresenter.searchWord(searchWord,0);
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
            String searchWord=hotSearchItems.get(i).getSearchWord();
            bt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d("zwyse",searchWord);
                    searchPresenter.searchWord(searchWord,0);
                }
            });
        }
        mLinearLayout.setVisibility(View.VISIBLE);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void getData2(List<?> dataList) {
        songItems=(ArrayList<SongItem>)dataList;
    }
}