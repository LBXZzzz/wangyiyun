package com.example.wangyiyun.view;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import com.example.wangyiyun.R;
import com.example.wangyiyun.ViewByMyself.WaterFlowLayout;

public class SearchActivity extends AppCompatActivity {
    private EditText mEditText;
    private Button mButton;
    private WaterFlowLayout mWaterFlowLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        initControl();
    }

    private void initControl() {
        mButton=findViewById(R.id.bt_search);
        mEditText=findViewById(R.id.et_search);
        mWaterFlowLayout=findViewById(R.id.fl_search);
    }

}