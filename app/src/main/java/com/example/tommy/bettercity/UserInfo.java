package com.example.tommy.bettercity;

/**
 * Created by tommy on 2015/11/23.
 */
public class UserInfo {
    private String mId;
    private String mPassword;
    private String mUserName;
    private String mSex;
    private String mBirthday;
    private String mHomeTown;
    private String mEmail;

    public UserInfo(String mId,String mPassword,String mUserName) {
        mId = this.mId;
        mPassword = this.mPassword;
        mUserName = this.mUserName;
    }

    public String getmId() {
        return mId;
    }

    public void setmId(String mId) {
        this.mId = mId;
    }

    public String getmPassword() {
        return mPassword;
    }

    public void setmPassword(String mPassword) {
        this.mPassword = mPassword;
    }

    public String getmUserName() {
        return mUserName;
    }

    public void setmUserName(String mUserName) {
        this.mUserName = mUserName;
    }

    public String getmSex() {
        return mSex;
    }

    public void setmSex(String mSex) {
        this.mSex = mSex;
    }

    public String getmBirthday() {
        return mBirthday;
    }

    public void setmBirthday(String mBirthday) {
        this.mBirthday = mBirthday;
    }

    public String getmHomeTown() {
        return mHomeTown;
    }

    public void setmHomeTown(String mHomeTown) {
        this.mHomeTown = mHomeTown;
    }

    public String getmEmail() {
        return mEmail;
    }

    public void setmEmail(String mEmail) {
        this.mEmail = mEmail;
    }
}
