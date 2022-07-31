package com.example.musicmelody;


import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Util {
    public static ExecutorService cachedThreadPool = Executors.newCachedThreadPool();

    //网络获取的post方法
    public static String post(String Url, RequestBody requestBody) {
        try {
            //1.创建OkHttpClient对象
            OkHttpClient okHttpClient = new OkHttpClient();
            //2.通过new FormBody()调用build方法,创建一个RequestBody,可以用add添加键值对
            //3.创建Request对象，设置URL地址，将RequestBody作为post方法的参数传入
            Request request = new Request.Builder().url(Url).post(requestBody).build();
            Response response = okHttpClient.newCall(request).execute();
            return response.body().string();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String get(String url) {
        try {
            OkHttpClient okHttpClient = new OkHttpClient();
            Request request = new Request.Builder().get().url(url).build();
            Response response = okHttpClient.newCall(request).execute();
            return response.body().string();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    //把毫秒转换为时分秒
    public static String format(int t){
        if(t<60000){
            String s=String.valueOf((t % 60000 )/1000);
            if((t % 60000 )/1000>10){
                return "00:"+s;
            }else {
                return "00:"+"0"+s;
            }
        }else if((t>=60000)&&(t<3600000)){
            return getString((t % 3600000)/60000)+":"+getString((t % 60000 )/1000);
        }else {
            return getString(t / 3600000)+":"+getString((t % 3600000)/60000)+":"+getString((t % 60000 )/1000);
        }
    }

    private static String getString(int t){
        String m;
        if(t>0){
            if(t<10){
                m="0"+t;
            }else{
                m=t+"";
            }
        }else{
            m="00";
        }
        return m;
    }

}
