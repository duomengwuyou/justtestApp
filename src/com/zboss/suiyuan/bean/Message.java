package com.zboss.suiyuan.bean;

import java.io.Serializable;

import com.google.gson.annotations.Expose;

public class Message implements Serializable {
    private static final long serialVersionUID = 1L;

    private String userId;

    private String channelId;

    private String nickname;

    private int headIcon;

    private long timeSamp;

    private String message;

    @Expose
    private String title;

    @Expose
    private String description;

    public Message(long time_samp, String message, String uid, String channelId, String nickName, int headIcon) {
        super();
        this.userId = uid;
        this.channelId = channelId;
        this.nickname = nickName;
        this.headIcon = headIcon;
        this.timeSamp = time_samp;
        this.message = message;
        this.title = "test";
        this.description = message;
    }
    
    public Message(String message, String channelId) {
        super();
        this.channelId = channelId;
        this.title = "Message";
        this.description = message;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getChannelId() {
        return channelId;
    }

    public void setChannelId(String channelId) {
        this.channelId = channelId;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public int getHeadIcon() {
        return headIcon;
    }

    public void setHeadIcon(int headIcon) {
        this.headIcon = headIcon;
    }

    public long getTimeSamp() {
        return timeSamp;
    }

    public void setTimeSamp(long timeSamp) {
        this.timeSamp = timeSamp;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

}
