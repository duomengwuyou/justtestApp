package com.zboss.suiyuan.chat;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import com.alibaba.sdk.android.oss.ClientException;
import com.alibaba.sdk.android.oss.OSS;
import com.alibaba.sdk.android.oss.OSSClient;
import com.alibaba.sdk.android.oss.ServiceException;
import com.alibaba.sdk.android.oss.callback.OSSCompletedCallback;
import com.alibaba.sdk.android.oss.internal.OSSAsyncTask;
import com.alibaba.sdk.android.oss.model.GetObjectRequest;
import com.alibaba.sdk.android.oss.model.GetObjectResult;
import com.baidu.android.pushservice.PushMessageReceiver;
import com.zboss.suiyuan.MainActivity;
import com.zboss.suiyuan.MainTab01;
import com.zboss.suiyuan.PushApplication;
import com.zboss.suiyuan.bean.ChatMessage;
import com.zboss.suiyuan.enums.TitleEnum;

/*
 * Push消息处理receiver。请编写您需要的回调函数， 一般来说： onBind是必须的，用来处理startWork返回值；
 *onMessage用来接收透传消息； onSetTags、onDelTags、onListTags是tag相关操作的回调；
 *onNotificationClicked在通知被点击时回调； onUnbind是stopWork接口的返回值回调

 * 返回值中的errorCode，解释如下：
 *0 - Success
 *10001 - Network Problem
 *10101  Integrate Check Error
 *30600 - Internal Server Error
 *30601 - Method Not Allowed
 *30602 - Request Params Not Valid
 *30603 - Authentication Failed
 *30604 - Quota Use Up Payment Required
 *30605 -Data Required Not Found
 *30606 - Request Time Expires Timeout
 *30607 - Channel Token Timeout
 *30608 - Bind Relation Not Found
 *30609 - Bind Number Too Many

 * 当您遇到以上返回错误时，如果解释不了您的问题，请用同一请求的返回值requestId和errorCode联系我们追查问题。
 *
 */

public class MyPushMessageReceiver extends PushMessageReceiver {
    /** TAG to Log */
    public static final String TAG = MyPushMessageReceiver.class.getSimpleName();

    // 记录上次收到信息的时间
    public static Long lastTime = null;

    public static long timeInternal = 1000 * 30;

    /**
     * 调用PushManager.startWork后，sdk将对push server发起绑定请求，这个过程是异步的。绑定请求的结果通过onBind返回。 如果您需要用单播推送，需要把这里获取的channel id和user
     * id上传到应用server中，再调用server接口用channel id和user id给单个手机或者用户推送。
     *
     * @param context BroadcastReceiver的执行Context
     * @param errorCode 绑定接口返回值，0 - 成功
     * @param appid 应用id。errorCode非0时为null
     * @param userId 应用user id。errorCode非0时为null
     * @param channelId 应用channel id。errorCode非0时为null
     * @param requestId 向服务端发起的请求id。在追查问题时有用；
     * @return none
     */
    @Override
    public void onBind(Context context, int errorCode, String appid, String userId, String channelId, String requestId) {
        String responseString =
                "onBind errorCode=" + errorCode + " appid=" + appid + " userId=" + userId + " channelId=" + channelId
                        + " requestId=" + requestId;
        Log.d(TAG, responseString);

        if (errorCode == 0) {
            // 绑定成功 设置自己的channelId和对方channelId为自己
            PushApplication.MY_CHANNEL_ID = channelId;
            PushApplication.YOUR_CHANNEL_ID = null;
            PushApplication.APP_ID = appid;
            PushApplication.USER_ID = userId;

            GetNetIp();
        }
    }

    public static void GetNetIp() {
        Thread t = new Thread(new Runnable() {
            public void run() {
                try {
                    String address = "http://ip.taobao.com/service/getIpInfo2.php?ip=myip";
                    URL url = new URL(address);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setConnectTimeout(6 * 1000);
                    connection.setUseCaches(false);

                    if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                        InputStream in = connection.getInputStream();

                        // 将流转化为字符串
                        BufferedReader reader = new BufferedReader(new InputStreamReader(in));

                        String tmpString = "";
                        StringBuilder retJSON = new StringBuilder();
                        while ((tmpString = reader.readLine()) != null) {
                            retJSON.append(tmpString + "\n");
                        }

                        JSONObject jsonObject = new JSONObject(retJSON.toString());
                        String code = jsonObject.getString("code");
                        if (code.equals("0")) {
                            JSONObject data = jsonObject.getJSONObject("data");
                            PushApplication.phoneIp =
                                    data.getString("ip") + "(" + data.getString("country") + data.getString("area")
                                            + "区" + data.getString("region") + data.getString("city")
                                            + data.getString("isp") + ")";

                        } else {
                            PushApplication.phoneIp = "unknown";
                        }
                    } else {
                        PushApplication.phoneIp = "unknown";
                    }
                } catch (Exception e) {
                    PushApplication.phoneIp = "unknown";
                }
                uploadAppinfo();
            }
        });
        t.start();
    }

    public static void uploadAppinfo() {
        Thread t = new Thread(new Runnable() {
            public void run() {
                try {
                    String phoneIp = URLEncoder.encode(PushApplication.phoneIp, "utf-8");
                    String parameters = "ip=" + phoneIp + "&channelId=" + PushApplication.MY_CHANNEL_ID;
                    String path = ChatConstant.IP_PORT + ChatConstant.PATH_MAP.get(ChatConstant.UPLOAD_APP_INFO);
                    URL url = new URL(path + parameters);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setConnectTimeout(6 * 1000);
                    connection.setRequestMethod("POST");
                    connection.setRequestProperty("Content-type", "text/html");
                    connection.setRequestProperty("Accept-Charset", "utf-8");
                    connection.setRequestProperty("contentType", "utf-8");
                    connection.setUseCaches(false);
                    if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                        InputStream in = connection.getInputStream();
                    }

                } catch (Exception e) {
                    System.out.println("error");
                } finally {

                }
            }
        });
        t.start();
    }

    /**
     * 接收透传消息的函数。
     *
     * @param context 上下文
     * @param message 推送的消息
     * @param customContentString 自定义内容,为空或者json字符串
     */
    @Override
    public void onMessage(Context context, String message, String customContentString) {
        String messageString = "透传消息 message=\"" + message + "\" customContentString=" + customContentString;
        Log.d(TAG, messageString);

        // 自定义内容获取方式，mykey和myvalue对应透传消息推送时自定义内容中设置的键和值
        // if (!TextUtils.isEmpty(customContentString)) {
        // JSONObject customJson = null;
        // try {
        // customJson = new JSONObject(customContentString);
        // String myvalue = null;
        // if (!customJson.isNull("mykey")) {
        // myvalue = customJson.getString("mykey");
        // }
        // } catch (JSONException e) {
        // // TODO Auto-generated catch block
        // e.printStackTrace();
        // }
        // }

        // 解析获取对话内容
        String chatContent = "等会哈，有点忙";
        String title = "none";
        // 默认为收到信息
        int isComing = 2;
        if (!TextUtils.isEmpty(message)) {
            JSONObject customJson = null;
            try {
                customJson = new JSONObject(message);
                if (!customJson.isNull("description")) {
                    chatContent = customJson.getString("description");
                }

                if (!customJson.isNull("title")) {
                    title = customJson.getString("title");
                    // 别人和自己建立了连接
                    if (title.equals(TitleEnum.BUILD_CONNECTION.getStatus())) {
                        PushApplication.YOUR_CHANNEL_ID = chatContent;
                        PushApplication.buildConOrNot = true;
                        chatContent = "恭喜，已经联系上有缘人，你们可以聊天了！";
                        MainTab01.buildCon.setText("断开");
                        isComing = 3;
                    } else if (title.equals(TitleEnum.CLOSE_CONNECTION.getStatus())) {
                        PushApplication.YOUR_CHANNEL_ID = null;
                        PushApplication.buildConOrNot = false;
                        chatContent = "抱歉，对方已断开，你们缘分已尽，请寻找其他有缘人！";
                        MainTab01.buildCon.setText("连接");
                        isComing = 3;
                    } else if (title.equals(TitleEnum.PIC_SUCCESS.getStatus())) {
                        downloadImage(chatContent);
                        chatContent = "接收到对方图片，加载中......";
                        isComing = 3;
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        
        // 发送时间
        sendTime();

        // Demo更新界面展示代码，应用请在这里加入自己的处理逻辑
        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setIsComing(isComing);
        if (isComing == 3) {
            chatMessage.setDate(new Date());
            chatMessage.setDateStr(chatContent);
        } else {
            chatMessage.setDate(new Date());
        }
        chatMessage.setMessage(chatContent);
        chatMessage.setNickname("有缘人");
        chatMessage.setReaded(true);
        MainTab01.mDatas.add(chatMessage);
        MainTab01.mAdapter.notifyDataSetChanged();
        MainTab01.mChatMessagesListView.setSelection(MainTab01.mDatas.size() - 1);
        showRedPoint();
    }

    private void sendTime() {
        if (lastTime == null || (System.currentTimeMillis() - lastTime > timeInternal)) {
            ChatMessage chatMessage = new ChatMessage();
            chatMessage.setIsComing(3);
            chatMessage.setDate(new Date());
            MainTab01.mDatas.add(chatMessage);
            MainTab01.mAdapter.notifyDataSetChanged();
            MainTab01.mChatMessagesListView.setSelection(MainTab01.mDatas.size() - 1);
            lastTime = System.currentTimeMillis();
        }
    }

    private void showRedPoint() {
        // 红点提示
        if (MainActivity.currentIndex != 0) {
            MainActivity.mTabLiaotian.removeView(MainActivity.mBadgeViewforLiaotian);
            int nowCount = MainActivity.mBadgeViewforLiaotian.getBadgeCount();
            MainActivity.mBadgeViewforLiaotian.setBadgeCount(nowCount + 1);
            MainActivity.mTabLiaotian.addView(MainActivity.mBadgeViewforLiaotian);
        }
    }

    // 异步下载图片
    private void downloadImage(final String picPath) {
        GetObjectRequest get = new GetObjectRequest(PushApplication.bucketName, picPath);
        OSS oss = new OSSClient(MainTab01.activity, PushApplication.endpoint, PushApplication.credentialProvider);

        OSSAsyncTask task = oss.asyncGetObject(get, new OSSCompletedCallback<GetObjectRequest, GetObjectResult>() {
            @Override
            public void onSuccess(GetObjectRequest request, GetObjectResult result) {
                try {
                    // 请求成功
                    InputStream inputStream = result.getObjectContent();
                    BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
                    bitmapOptions.inSampleSize = 4;
                    Bitmap img = BitmapFactory.decodeStream(inputStream, null, bitmapOptions);

                    // 接收消息
                    final ChatMessage chatMessage = new ChatMessage();
                    chatMessage.setIsComing(2);
                    chatMessage.setBitmap(img);
                    chatMessage.setDate(new Date());
                    chatMessage.setNickname("有缘人");
                    chatMessage.setReaded(true);

                    new Handler(MainTab01.activity.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            MainTab01.mDatas.add(chatMessage);
                            MainTab01.mAdapter.notifyDataSetChanged();
                            MainTab01.mChatMessagesListView.setSelection(MainTab01.mDatas.size() - 1);
                            showRedPoint();
                        }
                    });
                } catch (Exception e) {
                    System.out.println(e.getLocalizedMessage());
                }
            }

            @Override
            public void onFailure(GetObjectRequest request, ClientException clientExcepion,
                    ServiceException serviceException) {
                // 请求异常
                if (clientExcepion != null) {
                    // 本地异常如网络异常等
                    clientExcepion.printStackTrace();
                }
                if (serviceException != null) {
                    // 服务异常
                    Log.e("ErrorCode", serviceException.getErrorCode());
                    Log.e("RequestId", serviceException.getRequestId());
                    Log.e("HostId", serviceException.getHostId());
                    Log.e("RawMessage", serviceException.getRawMessage());
                }
            }

        });
    }

    /**
     * 接收通知点击的函数。
     *
     * @param context 上下文
     * @param title 推送的通知的标题
     * @param description 推送的通知的描述
     * @param customContentString 自定义内容，为空或者json字符串
     */
    @Override
    public void onNotificationClicked(Context context, String title, String description, String customContentString) {
        String notifyString =
                "通知点击 title=\"" + title + "\" description=\"" + description + "\" customContent=" + customContentString;
        Log.d(TAG, notifyString);

        // 自定义内容获取方式，mykey和myvalue对应通知推送时自定义内容中设置的键和值
        if (!TextUtils.isEmpty(customContentString)) {
            JSONObject customJson = null;
            try {
                customJson = new JSONObject(customContentString);
                String myvalue = null;
                if (!customJson.isNull("mykey")) {
                    myvalue = customJson.getString("mykey");
                }
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        // Demo更新界面展示代码，应用请在这里加入自己的处理逻辑
        updateContent(context, notifyString);
    }

    /**
     * 接收通知到达的函数。
     *
     * @param context 上下文
     * @param title 推送的通知的标题
     * @param description 推送的通知的描述
     * @param customContentString 自定义内容，为空或者json字符串
     */

    @Override
    public void onNotificationArrived(Context context, String title, String description, String customContentString) {

        String notifyString =
                "onNotificationArrived  title=\"" + title + "\" description=\"" + description + "\" customContent="
                        + customContentString;
        Log.d(TAG, notifyString);

        // 自定义内容获取方式，mykey和myvalue对应通知推送时自定义内容中设置的键和值
        if (!TextUtils.isEmpty(customContentString)) {
            JSONObject customJson = null;
            try {
                customJson = new JSONObject(customContentString);
                String myvalue = null;
                if (!customJson.isNull("mykey")) {
                    myvalue = customJson.getString("mykey");
                }
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        // Demo更新界面展示代码，应用请在这里加入自己的处理逻辑
        // 你可以參考 onNotificationClicked中的提示从自定义内容获取具体值
        updateContent(context, notifyString);
    }

    /**
     * setTags() 的回调函数。
     *
     * @param context 上下文
     * @param errorCode 错误码。0表示某些tag已经设置成功；非0表示所有tag的设置均失败。
     * @param successTags 设置成功的tag
     * @param failTags 设置失败的tag
     * @param requestId 分配给对云推送的请求的id
     */
    @Override
    public void onSetTags(Context context, int errorCode, List<String> sucessTags, List<String> failTags,
            String requestId) {
        String responseString =
                "onSetTags errorCode=" + errorCode + " sucessTags=" + sucessTags + " failTags=" + failTags
                        + " requestId=" + requestId;
        Log.d(TAG, responseString);

        // Demo更新界面展示代码，应用请在这里加入自己的处理逻辑
        updateContent(context, responseString);
    }

    /**
     * delTags() 的回调函数。
     *
     * @param context 上下文
     * @param errorCode 错误码。0表示某些tag已经删除成功；非0表示所有tag均删除失败。
     * @param successTags 成功删除的tag
     * @param failTags 删除失败的tag
     * @param requestId 分配给对云推送的请求的id
     */
    @Override
    public void onDelTags(Context context, int errorCode, List<String> sucessTags, List<String> failTags,
            String requestId) {
        String responseString =
                "onDelTags errorCode=" + errorCode + " sucessTags=" + sucessTags + " failTags=" + failTags
                        + " requestId=" + requestId;
        Log.d(TAG, responseString);

        // Demo更新界面展示代码，应用请在这里加入自己的处理逻辑
        updateContent(context, responseString);
    }

    /**
     * listTags() 的回调函数。
     *
     * @param context 上下文
     * @param errorCode 错误码。0表示列举tag成功；非0表示失败。
     * @param tags 当前应用设置的所有tag。
     * @param requestId 分配给对云推送的请求的id
     */
    @Override
    public void onListTags(Context context, int errorCode, List<String> tags, String requestId) {
        String responseString = "onListTags errorCode=" + errorCode + " tags=" + tags;
        Log.d(TAG, responseString);

        // Demo更新界面展示代码，应用请在这里加入自己的处理逻辑
        updateContent(context, responseString);
    }

    /**
     * PushManager.stopWork() 的回调函数。
     *
     * @param context 上下文
     * @param errorCode 错误码。0表示从云推送解绑定成功；非0表示失败。
     * @param requestId 分配给对云推送的请求的id
     */
    @Override
    public void onUnbind(Context context, int errorCode, String requestId) {
        String responseString = "onUnbind errorCode=" + errorCode + " requestId = " + requestId;
        Log.d(TAG, responseString);

        if (errorCode == 0) {
            // 解绑定成功
        }
        // Demo更新界面展示代码，应用请在这里加入自己的处理逻辑
        updateContent(context, responseString);
    }

    private void updateContent(Context context, String content) {
        Log.d(TAG, "updateContent");
        String logText = "";

        if (!logText.equals("")) {
            logText += "\n";
        }

        SimpleDateFormat sDateFormat = new SimpleDateFormat("HH-mm-ss");
        logText += sDateFormat.format(new Date()) + ": ";
        logText += content;

        Log.d(TAG, logText);
    }

}
