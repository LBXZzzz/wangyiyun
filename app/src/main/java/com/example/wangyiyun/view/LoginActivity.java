package com.example.wangyiyun.view;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.wangyiyun.R;
import com.example.wangyiyun.presenter.LoginActivityPresenter;

import java.util.ArrayList;

public class LoginActivity extends AppCompatActivity {
    private EditText mEditText;
    private Button mButton;
    LoginActivityPresenter loginActivityPresenter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initView();
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String phoneNumber;
                phoneNumber=mEditText.getText().toString();
                if(phoneNumber.length()==11){
                    loginActivityPresenter.login(phoneNumber);
                    Intent intent=new Intent(LoginActivity.this, CodeActivity.class);//给后面开启的活动传值
                    intent.putExtra("PhoneNumber",phoneNumber);
                    startActivity(intent);
                }else {
                    Toast.makeText(LoginActivity.this,"请输入长度为11的手机号码",Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    private void initView() {
        mEditText=findViewById(R.id.et_login);
        mButton=findViewById(R.id.bt_login);
        loginActivityPresenter=new LoginActivityPresenter();
    }


}