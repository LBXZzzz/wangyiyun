package com.example.wangyiyun.modal;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.wangyiyun.contacts.ContactClass;
import com.example.wangyiyun.entries.SongItem;
import com.example.wangyiyun.utils.HttpUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class GetSearchContent implements ContactClass.IGetSearchContent {

    /**
     * 搜索的接口：https://netease-cloud-music-api-4eodv9lwk-tangan91314.vercel.app/cloudsearch?keywords=%E6%B5%B7%E9%98%94%E5%A4%A9%E7%A9%BA
     * 必选参数 : keywords : 关键词
     * 可选参数 : limit : 返回数量 , 默认为 30 offset : 偏移数量，用于分页 , 如 : 如 :( 页数 -1)*30, 其中 30 为 limit 的值 , 默认为 0
     * type: 搜索类型；默认为 1 即单曲 , 取值意义 : 1: 单曲, 10: 专辑, 100: 歌手, 1000: 歌单, 1002: 用户, 1004: MV, 1006: 歌词, 1009: 电台, 1014: 视频, 1018:综合, 2000:声音(搜索声音返回字段格式会不一样)
     */
    private final String url = "https://netease-cloud-music-api-4eodv9lwk-tangan91314.vercel.app/cloudsearch?keywords=";
    private List<SongItem> list = new ArrayList<>();
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

    private void analyzeData(String data) throws JSONException {
        Log.d("zwy搜返回的数据：", data);
        JSONObject jsonObject = new JSONObject(data);
        JSONObject jsonObject1 = jsonObject.getJSONObject("result");
        JSONArray jsonArray = jsonObject1.getJSONArray("songs");
        list = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObject2 = jsonArray.getJSONObject(i);
            JSONArray jsonArray1 = jsonObject2.getJSONArray("ar");
            JSONObject jsonObject3 = jsonArray1.getJSONObject(0);
            JSONObject jsonObject4 = jsonObject2.getJSONObject("al");
            SongItem songItem = new SongItem(jsonObject3.getString("name"), jsonObject2.getString("name"),
                    jsonObject2.getString("id"), jsonObject4.getString("picUrl"));
            list.add(songItem);
        }
        Message message = new Message();
        message.obj = list;
        handler.sendMessage(message);
    }

    @Override
    public void getContent(String searchWord, int offset, ContactClass.IDataList iDataList) {
        String s = String.valueOf(offset);
        String searchUrl = url + searchWord + "&offset=" + s;
        Log.d("zwysr", url);
        returnData(searchUrl);
        handler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(@NonNull Message msg) {
                list = new ArrayList<>();
                list = (ArrayList<SongItem>) msg.obj;
                iDataList.dataReturn(list);
                super.handleMessage(msg);
            }
        };
    }
}
