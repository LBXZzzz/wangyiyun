package com.example.wangyiyun.modal;

import com.example.wangyiyun.contacts.ContactClass;
import com.example.wangyiyun.utils.HttpUtil;

import okhttp3.FormBody;
import okhttp3.RequestBody;

public class GetPhoneNumber implements ContactClass.ILoginModel {
    @Override
    public void login(String phoneNumber) {
        RequestBody  requestBody = new FormBody.Builder().add("phone",phoneNumber).build();
        HttpUtil.cachedThreadPool.execute(()->{
            HttpUtil httpUtil=new HttpUtil();
            httpUtil.post("https://netease-cloud-music-api-4eodv9lwk-tangan91314.vercel.app/captcha/sent",requestBody);
        });
    }
}
