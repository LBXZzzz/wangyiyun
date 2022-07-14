package com.example.wangyiyun.Contacts;

public final class ContactClass {
    //    view层接口
    public interface ILoginView{
        //登录成功
        void loginSuccess();
        //登录失败
        void loginFailure();
    }
    //      presenter层接口
    public interface ILoginPresenter{
        void login(String phoneNumber);
    }
    //      model层接口
    public interface ILoginModel{
        void login(String phoneNumber);
    }
    public interface ILoginCodeModel{
        void loginCode(String phoneNumber,String code);
    }
}
