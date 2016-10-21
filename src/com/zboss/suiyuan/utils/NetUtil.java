package com.zboss.suiyuan.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * 网络util
 * 
 * @author xinglong
 *
 */
public class NetUtil {
    // 判断网络是否可用
    public static boolean isNetConnected(Context context) {
        boolean isNetConnected;
        ConnectivityManager connManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = connManager.getActiveNetworkInfo();
        if (info != null && info.isAvailable()) {
            isNetConnected = true;
        } else {
            isNetConnected = false;
        }
        return isNetConnected;
    }
}
