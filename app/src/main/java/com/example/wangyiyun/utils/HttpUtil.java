package com.example.wangyiyun.utils;

import android.util.Log;




import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class HttpUtil {
    public  static  ExecutorService cachedThreadPool = Executors.newCachedThreadPool();
    //网络获取的post方法
    public static void Post(String Url, RequestBody requestBody){
        cachedThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    //1.创建OkHttpClient对象
                    OkHttpClient  okHttpClient = new OkHttpClient();
                    //2.通过new FormBody()调用build方法,创建一个RequestBody,可以用add添加键值对
                    //3.创建Request对象，设置URL地址，将RequestBody作为post方法的参数传入
                    Request request = new Request.Builder().url(Url).post(requestBody).build();
                    Response response=okHttpClient.newCall(request).execute();
                    Log.d("zwy",response.body().toString());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

}
