package com.example.wangyiyun.presenter;

import com.example.wangyiyun.contacts.ContactClass;
import com.example.wangyiyun.modal.GetHotWord;
import com.example.wangyiyun.modal.GetSearchContent;
import com.example.wangyiyun.modal.GetSongUrl;

import java.util.List;

public class SearchPresenter implements ContactClass.IPresenter ,ContactClass.ISearchPresenter,ContactClass.ISongUrl{
    //热搜的网址"https://netease-cloud-music-api-4eodv9lwk-tangan91314.vercel.app/search/hot/detail"
    GetHotWord getHotWord;
    GetSearchContent getSearchContent;
    GetSongUrl getSongUrl;
    ContactClass.IView iView;
    ContactClass.IView2 iView2;
    ContactClass.IView3 iView3;
    public SearchPresenter(ContactClass.IView iView,ContactClass.IView2 iView2,ContactClass.IView3 iView3){
        this.iView=iView;
        this.iView2=iView2;
        this.iView3=iView3;
        getHotWord=new GetHotWord();
        getSongUrl=new GetSongUrl();
        getSearchContent=new GetSearchContent();
    }
    @Override
    public void bridge() {
        this.getHotWord.getHot(new ContactClass.IDataList() {
            @Override
            public void dataReturn(List<?> arrayList) {
                iView.getData(arrayList);
            }
        });
    }

    @Override
    public void searchWord(String searchWord,int offset) {
        this.getSearchContent.getContent(searchWord, offset, new ContactClass.IDataList() {
            @Override
            public void dataReturn(List<?> arrayList) {
                iView2.getData2(arrayList);
            }
        });
    }

    @Override
    public void getSongUrl(String songId,String songUrl) {
        this.getSongUrl.getSongUrl(songId, new ContactClass.IDataString() {
            @Override
            public void dataReturn(String songUrl1) {
                iView3.getData(songUrl1,songUrl);
            }
        });
    }
}
