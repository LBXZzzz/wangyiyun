package com.example.wangyiyun.presenter;

import com.example.wangyiyun.contacts.ContactClass;
import com.example.wangyiyun.modal.GetHotWord;
import com.example.wangyiyun.modal.GetSearchContent;

import java.util.List;

public class SearchPresenter implements ContactClass.IPresenter ,ContactClass.ISearchPresenter{
    //热搜的网址"https://netease-cloud-music-api-4eodv9lwk-tangan91314.vercel.app/search/hot/detail"
    GetHotWord getHotWord;
    GetSearchContent getSearchContent;
    ContactClass.IView iView;
    ContactClass.IView2 iView2;
    public SearchPresenter(ContactClass.IView iView,ContactClass.IView2 iView2){
        this.iView=iView;
        this.iView2=iView2;
        getHotWord=new GetHotWord();
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
}
