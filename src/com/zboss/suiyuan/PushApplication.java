package com.zboss.suiyuan;

import android.app.Application;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.alibaba.sdk.android.oss.common.auth.OSSCredentialProvider;
import com.alibaba.sdk.android.oss.common.auth.OSSPlainTextAKSKCredentialProvider;
import com.baidu.android.pushservice.PushConstants;
import com.baidu.android.pushservice.PushManager;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.zboss.suiyuan.chat.BaiduPush;
import com.zboss.suiyuan.utils.SharePreferenceUtil;

/**
 * 初始化数据
 * 
 * @author xinglong
 *
 */
public class PushApplication extends Application {
    /**
     * 百度推送相关
     */
    public final static String API_KEY = "dBh8e9c4RjHDOD8NXAzwVKzq";
    public final static String SECRIT_KEY = "t2z6dn32kxd1r35GP3L9Fjn3XKnt508R";
    public static final String SP_FILE_NAME = "push_msg_sp";

    private static PushApplication mApplication;
    private BaiduPush mBaiduPushServer;
    private SharePreferenceUtil mSpUtil;

    private NotificationManager mNotificationManager;
    private Notification mNotification;
    private Gson mGson;

    // 自己和对方的channelid
    public static String MY_CHANNEL_ID = "";
    public static String YOUR_CHANNEL_ID = "";
    public static String APP_ID = "";
    public static String USER_ID = "";

    // 是否建立了会话连接
    public static boolean buildConOrNot = false;

    // 图片类型
    public static Integer DISPLAY_TYPE = null;
    public static Long NEWS_DISPLAY_TYPE = null;

    // 图片选项
    public static String[] pictypes = null;
    public static Integer[] picids = null;

    // 新闻选项
    public static String[] newstypes = null;
    public static Integer[] newsids = null;

    // 手机ip以及网络状态
    public static String phoneIp;
    public static boolean wifiOrNot = false;

    // oss配置
    public static String endpoint = "http://oss-cn-beijing.aliyuncs.com";
    public static String accessKey = "F1EiuNOYlilE6D7S";
    public static String accessSecret = "WCFB4JpKKEqyHUTqWtKKNQCa0ryhv7";
    public static String bucketName = "zbosssuiyuan";

    // 是否已显示帮助
    public static boolean showHelpOrNot = false;

    // 凭证提供
    public static OSSCredentialProvider credentialProvider = new OSSPlainTextAKSKCredentialProvider(accessKey,
            accessSecret);

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
        baiduPush();

        mGson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
        mSpUtil = new SharePreferenceUtil(this, SP_FILE_NAME);
        wifiOrNot = wifiOrNot();
    }
    
    private void baiduPush() {
        mBaiduPushServer = new BaiduPush(BaiduPush.HTTP_METHOD_POST, SECRIT_KEY, API_KEY);
        // 初始化百度推送
        PushManager.startWork(getApplicationContext(), PushConstants.LOGIN_TYPE_API_KEY, PushApplication.API_KEY);
    }

    /**
     * 是否wifi环境
     * 
     * @return
     */
    private boolean wifiOrNot() {
        ConnectivityManager connectMgr = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = connectMgr.getActiveNetworkInfo();
        if (info == null) {
            return false;
        } else {
            if (info.getType() == ConnectivityManager.TYPE_WIFI) {
                return true;
            } else {
                return false;
            }
        }
    }

    /**
     * 百度推送获取
     * 
     * @return
     */
    public synchronized BaiduPush getBaiduPush() {
        if (mBaiduPushServer == null)
            mBaiduPushServer = new BaiduPush(BaiduPush.HTTP_METHOD_POST, SECRIT_KEY, API_KEY);
        return mBaiduPushServer;
    }

    /**
     * gson实例获取
     * 
     * @return
     */
    public synchronized Gson getGson() {
        if (mGson == null)
            mGson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
        return mGson;
    }

    /**
     * 获取通过管理
     * 
     * @return
     */
    public NotificationManager getNotificationManager() {
        if (mNotificationManager == null)
            mNotificationManager = (NotificationManager) getSystemService(android.content.Context.NOTIFICATION_SERVICE);
        return mNotificationManager;
    }

    /**
     * 获取存储工具类
     * 
     */
    public synchronized SharePreferenceUtil getSpUtil() {
        if (mSpUtil == null)
            mSpUtil = new SharePreferenceUtil(this, SP_FILE_NAME);
        return mSpUtil;
    }

}
