package com.example.wangyiyun.presenter;

import com.example.wangyiyun.modal.GetPhoneCode;

public class CodePresenter {
    GetPhoneCode getPhoneCode;
    public CodePresenter(){
        getPhoneCode=new GetPhoneCode();
    }
    public void getPhoneCode(String phoneNumber,String code){
        getPhoneCode.loginCode(phoneNumber,code);
    }
}
