package com.example.wangyiyun.contacts;

import android.view.View;

import java.util.List;

public final class ContactClass {
    //    view层接口
    //登录功能的接口
    public interface ILoginView {
        //登录成功
        void loginSuccess();

        //登录失败
        void loginFailure();
    }

    //
    public interface IView {
        void getData(List<?> dataList);
    }

    public interface IView2 {
        void getData2(List<?> dataList);
    }

    public interface IView3 {
        void getData(String dataString, String dataString2);
    }

    //      presenter层接口
    public interface ILoginPresenter {
        void login(String phoneNumber);
    }

    public interface IPresenter {
        void bridge();
    }

    public interface ISearchPresenter {
        void searchWord(String searchWord, int limit);
    }

    public interface ISongUrl {
        void getSongUrl(String songId, String songUrl);
    }

    //      model层接口
    public interface ILoginModel {
        void login(String phoneNumber);
    }

    public interface ILoginCodeModel {
        void loginCode(String phoneNumber, String code);
    }

    public interface IGetHotWord {
        void getHot(IDataList iDataList);
    }

    public interface IGetSearchContent {
        void getContent(String searchWord, int limit, IDataList iDataList);
    }

    public interface IGetSongUrl {
        void getSongUrl(String songId, IDataString iDataString);
    }

    //modal层用来返回数据的接口
    public interface IDataList {
        void dataReturn(List<?> arrayList);
    }

    public interface IDataString {
        void dataReturn(String songUrl);
    }

    //解析数据的接口
   /* public interface IAnalyzeData{
        void getReturn();
        void analyzeData();
    }*/
    //recyclerview的点击事件的接口
    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }
}
