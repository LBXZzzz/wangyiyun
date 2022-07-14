package com.example.wangyiyun.view;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.wangyiyun.R;
import com.example.wangyiyun.presenter.CodePresenter;

public class CodeActivity extends AppCompatActivity {
    private EditText mEditText;
    private Button mButton;
    CodePresenter codePresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_code);
        Intent intent=getIntent();
        String phoneNumber=intent.getStringExtra("PhoneNumber");
        mEditText=findViewById(R.id.et_code);
        mButton=findViewById(R.id.bt_login_code);
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String code=mEditText.getText().toString();
                if(code.length()==4){
                    codePresenter=new CodePresenter();
                    codePresenter.getPhoneCode(phoneNumber,code);
                }else {
                    Toast.makeText(CodeActivity.this,"请输入四位验证码",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}