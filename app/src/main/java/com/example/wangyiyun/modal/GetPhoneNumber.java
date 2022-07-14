package com.example.wangyiyun.modal;

import com.example.wangyiyun.Contacts.ContactClass;
import com.example.wangyiyun.utils.HttpUtil;

import okhttp3.FormBody;
import okhttp3.RequestBody;

public class GetPhoneNumber implements ContactClass.ILoginModel {
    @Override
    public void login(String phoneNumber) {
        HttpUtil httpUtil=new HttpUtil();
        RequestBody  requestBody = new FormBody.Builder().add("phone",phoneNumber).build();
        httpUtil.Post("https://netease-cloud-music-api-4eodv9lwk-tangan91314.vercel.app/captcha/sent",requestBody);
        RequestBody requestBody1=new FormBody.Builder().add("keywords","薛之谦").build();
        httpUtil.Post("https://netease-cloud-music-api-4eodv9lwk-tangan91314.vercel.app/cloudsearch",requestBody1);
    }
}
