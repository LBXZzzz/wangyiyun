package com.example.wangyiyun.presenter;

import com.example.wangyiyun.contacts.ContactClass;
import com.example.wangyiyun.modal.GetPhoneNumber;

public class LoginActivityPresenter implements ContactClass.ILoginPresenter {
    GetPhoneNumber getPhoneNumber;

    public LoginActivityPresenter() {
        getPhoneNumber = new GetPhoneNumber();
    }

    @Override
    public void login(String phoneNumber) {
        this.getPhoneNumber.login(phoneNumber);
    }
}
