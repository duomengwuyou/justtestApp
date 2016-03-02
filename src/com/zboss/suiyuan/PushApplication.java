package com.zboss.suiyuan;

import android.app.Application;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.widget.RemoteViews;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.zboss.suiyuan.chat.BaiduPush;
import com.zboss.suiyuan.dao.MessageDB;
import com.zboss.suiyuan.dao.UserDB;
import com.zboss.suiyuan.utils.SharePreferenceUtil;

public class PushApplication extends Application {
    /**
     * API_KEY
     */
    public final static String API_KEY = "dBh8e9c4RjHDOD8NXAzwVKzq";
    /**
     * SECRET_KEY
     */
    public final static String SECRIT_KEY = "t2z6dn32kxd1r35GP3L9Fjn3XKnt508R";
    public static final String SP_FILE_NAME = "push_msg_sp";
    /**
     * 预定义的头像
     */
    public static final int[] heads = { R.drawable.h0, R.drawable.h1, R.drawable.h2, R.drawable.h3, R.drawable.h4,
            R.drawable.h5, R.drawable.h6, R.drawable.h7, R.drawable.h8, R.drawable.h9, R.drawable.h10, R.drawable.h11,
            R.drawable.h12, R.drawable.h13, R.drawable.h14, R.drawable.h15, R.drawable.h16, R.drawable.h17,
            R.drawable.h18 };

    private static PushApplication mApplication;

    private BaiduPush mBaiduPushServer;

    private SharePreferenceUtil mSpUtil;

    private NotificationManager mNotificationManager;
    private Notification mNotification;
    private Gson mGson;

    private UserDB userDB;
    private MessageDB messageDB;
    
    // 自己和对方的channelid
    public static String MY_CHANNEL_ID = "";
    public static String YOUR_CHANNEL_ID = "";
    public static String APP_ID = "";
    public static String USER_ID = "";
    // 是否建立了会话连接
    public static boolean buildConOrNot = false;

    public synchronized static PushApplication getInstance() {
        return mApplication;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mApplication = this;
        initData();
    }

    private void initData() {
        mBaiduPushServer = new BaiduPush(BaiduPush.HTTP_METHOD_POST, SECRIT_KEY, API_KEY);
        // 不转换没有 @Expose 注解的字段
        mGson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
        mSpUtil = new SharePreferenceUtil(this, SP_FILE_NAME);
        mNotificationManager = (NotificationManager) getSystemService(android.content.Context.NOTIFICATION_SERVICE);
        userDB = new UserDB(this);
        messageDB = new MessageDB(this);
    }

    public synchronized BaiduPush getBaiduPush() {
        if (mBaiduPushServer == null)
            mBaiduPushServer = new BaiduPush(BaiduPush.HTTP_METHOD_POST, SECRIT_KEY, API_KEY);
        return mBaiduPushServer;
    }

    public synchronized Gson getGson() {
        if (mGson == null)
            // 不转换没有 @Expose 注解的字段
            mGson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
        return mGson;
    }

    public NotificationManager getNotificationManager() {
        if (mNotificationManager == null)
            mNotificationManager = (NotificationManager) getSystemService(android.content.Context.NOTIFICATION_SERVICE);
        return mNotificationManager;
    }

    public synchronized SharePreferenceUtil getSpUtil() {
        if (mSpUtil == null)
            mSpUtil = new SharePreferenceUtil(this, SP_FILE_NAME);
        return mSpUtil;
    }

    /**
     * 创建挂机图标
     */
    @SuppressWarnings("deprecation")
    public void showNotification() {
        if (!mSpUtil.getMsgNotify())// 如果用户设置不显示挂机图标，直接返回
            return;

        int icon = R.drawable.notify_general;
        CharSequence tickerText = "随缘正在后台运行";

        long when = System.currentTimeMillis();
        mNotification = new Notification(icon, tickerText, when);

        // 放置在"正在运行"栏目中
        mNotification.flags = Notification.FLAG_ONGOING_EVENT;

        RemoteViews contentView = new RemoteViews(getPackageName(), R.layout.notify_status_bar_latest_event_view);
        contentView.setImageViewResource(R.id.icon, heads[mSpUtil.getHeadIcon()]);
        contentView.setTextViewText(R.id.title, mSpUtil.getNick());
        contentView.setTextViewText(R.id.text, tickerText);
        contentView.setLong(R.id.time, "setTime", when);
        // 指定个性化视图
        mNotification.contentView = contentView;

        Intent intent = new Intent(this, MainActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        // 指定内容意图
        mNotification.contentIntent = contentIntent;

        mNotificationManager.notify(0x000, mNotification);
    }

    public MessageDB getMessageDB() {
        return messageDB;
    }

    public UserDB getUserDB() {
        return userDB;
    }

}
