package com.example.wangyiyun.modal;

import com.example.wangyiyun.contacts.ContactClass;
import com.example.wangyiyun.utils.HttpUtil;

import okhttp3.FormBody;
import okhttp3.RequestBody;

public class GetPhoneCode implements ContactClass.ILoginCodeModel {

    @Override
    public void loginCode(String phoneNumber, String code) {
        HttpUtil.cachedThreadPool.execute(()->{
            RequestBody requestBody = new FormBody.Builder().add("phone",phoneNumber).add("captcha",code).build();
            HttpUtil httpUtil=new HttpUtil();
            httpUtil.post("https://netease-cloud-music-api-4eodv9lwk-tangan91314.vercel.app/captcha/verify",requestBody);
        });
    }
}
