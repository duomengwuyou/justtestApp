package com.zboss.suiyuan.bean;

import java.text.SimpleDateFormat;
import java.util.Date;
import android.graphics.Bitmap;

/**
 * 聊天内容
 * 
 * @author xinglong
 *
 */
public class ChatMessage {

    private String message;
    private int isComing; // 1 发出 2 收到 3系统 4 语音
    private Date date;
    private String userId;
    private int icon;
    private Bitmap bitmap; // 图片文件

    private float seconds; // 录音长度
    private String voicePath; // 录音路径

    private String imagePath; // 图片路径

    public void setDateStr(String dateStr) {
        this.dateStr = dateStr;
    }

    private String nickname;
    private boolean readed;
    private String dateStr;

    public ChatMessage() {
    }

    public ChatMessage(String message, int isComing, String userId, int icon, String nickname, boolean readed,
            String dateStr) {
        super();
        this.message = message;
        this.isComing = isComing;
        this.userId = userId;
        this.icon = icon;
        this.nickname = nickname;
        this.readed = readed;
        this.dateStr = dateStr;
    }

    public String getDateStr() {
        return dateStr;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getIsComing() {
        return isComing;
    }

    public void setIsComing(int isComing) {
        this.isComing = isComing;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        this.dateStr = df.format(date);
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public int getIcon() {
        return icon;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }

    public boolean isReaded() {
        return readed;
    }

    public void setReaded(boolean readed) {
        this.readed = readed;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public float getSeconds() {
        return seconds;
    }

    public void setSeconds(float seconds) {
        this.seconds = seconds;
    }

    public String getVoicePath() {
        return voicePath;
    }

    public void setVoicePath(String voicePath) {
        this.voicePath = voicePath;
    }

}
