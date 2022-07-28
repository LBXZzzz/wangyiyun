package com.example.wangyiyun.modal;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import androidx.annotation.NonNull;

import com.example.wangyiyun.contacts.ContactClass;
import com.example.wangyiyun.entries.SongItem;
import com.example.wangyiyun.utils.HttpUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class GetSongUrl implements ContactClass.IGetSongUrl {
    private Handler handler;

    private void returnData(String url) {
        HttpUtil.cachedThreadPool.execute(() -> {
            try {
                analyzeData(HttpUtil.get(url));
            } catch (Throwable e) {
                e.printStackTrace();
            }
        });
    }

    private void analyzeData(String s) throws JSONException {
        JSONObject jsonObject = new JSONObject(s);
        JSONArray jsonArray = jsonObject.optJSONArray("data");
        JSONObject jsonObject1 = jsonArray.getJSONObject(0);
        String songUrl = jsonObject1.getString("url");
        Message message = new Message();
        message.obj = songUrl;
        handler.sendMessage(message);
    }

    @Override
    public void getSongUrl(String songId, ContactClass.IDataString iDataString) {
        String url = "https://netease-cloud-music-api-4eodv9lwk-tangan91314.vercel.app/song/url?id=" + songId;
        returnData(url);
        handler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(@NonNull Message msg) {
                String songUrl = (String) msg.obj;
                iDataString.dataReturn(songUrl);
                super.handleMessage(msg);
            }
        };
    }
}
