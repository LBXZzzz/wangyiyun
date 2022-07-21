package com.example.wangyiyun.modal;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.wangyiyun.contacts.ContactClass;
import com.example.wangyiyun.entries.HotSearchItem;
import com.example.wangyiyun.utils.HttpUtil;
import com.example.wangyiyun.utils.ListChangeUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class GetHotWord implements ContactClass.IGetHotWord {
    List<HotSearchItem> list=new ArrayList<>();
    private Handler handler;
    //热搜的网址
    private String Url ="https://netease-cloud-music-api-4eodv9lwk-tangan91314.vercel.app/search/hot/detail";
    //获取到网络返回的数据
    private void getReturn(){
        HttpUtil.cachedThreadPool.execute(() -> {
            HttpUtil httpUtil=new HttpUtil();
            String data=httpUtil.get(Url);
            Log.d("热搜返回的数据+",data);
            try {
                analyzeData(data);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        });
    }
    //对网络返回的数据解析
    private void analyzeData(String data) throws JSONException {
        JSONObject jsonObject=new JSONObject(data);
        JSONArray jsonArray=jsonObject.getJSONArray("data");
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObject1=jsonArray.getJSONObject(i);
            HotSearchItem hotSearchItem=new HotSearchItem(jsonObject1.getString("searchWord"));
            list.add(hotSearchItem);
        }
        Message message=new Message();
        message.obj=list;
        handler.sendMessage(message);
    }

    @Override
    public void getHot(ContactClass.IDataList iDataList) {
         getReturn();
         handler=new Handler(Looper.getMainLooper()){
             @Override
             public void handleMessage(@NonNull Message msg) {
                 super.handleMessage(msg);
                 list= ListChangeUtil.castList(msg.obj,HotSearchItem.class);
                 iDataList.dataReturn(list);
             }
         };
    }
}
