package com.matrimony.identity.data;

import java.io.Serializable;

public class LoginRequest implements Serializable {

    private String phoneNo;
    private String password;

    public LoginRequest(String phoneNo, String password) {
        this.phoneNo = phoneNo;
        this.password = password;
    }

    public String getPhoneNo() {
        return phoneNo;
    }

    public void setPhoneNo(String phoneNo) {
        this.phoneNo = phoneNo;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
