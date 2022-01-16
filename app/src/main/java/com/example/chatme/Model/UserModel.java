package com.example.chatme.Model;

import android.os.Parcel;
import android.os.Parcelable;

public class UserModel implements Parcelable {

    private String userName;
    private String userId;
    private String userNumber;
    private String userProfife;
    private String status;


    public UserModel(Parcel in) {
        userName = in.readString();
        userId = in.readString();
        userNumber = in.readString();
        userProfife = in.readString();
        status = in.readString();
    }

    public static final Creator<UserModel> CREATOR = new Creator<UserModel>() {
        @Override
        public UserModel createFromParcel(Parcel in) {
            return new UserModel(in);
        }

        @Override
        public UserModel[] newArray(int size) {
            return new UserModel[size];
        }
    };

    public UserModel() {

    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserNumber() {
        return userNumber;
    }

    public void setUserNumber(String userNumber) {
        this.userNumber = userNumber;
    }

    public String getUserProfife() {
        return userProfife;
    }

    public void setUserProfife(String userProfife) {
        this.userProfife = userProfife;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(userName);
        dest.writeString(userId);
        dest.writeString(userNumber);
        dest.writeString(userProfife);
        dest.writeString(status);
    }
}