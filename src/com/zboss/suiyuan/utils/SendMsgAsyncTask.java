package com.zboss.suiyuan.utils;

import com.zboss.suiyuan.PushApplication;
import com.zboss.suiyuan.R;
import com.zboss.suiyuan.chat.BaiduPush;

import android.os.AsyncTask;
import android.os.Handler;
import android.text.TextUtils;

/**
 * 异步发送消息
 * 
 * @author xinglong
 *
 */
public class SendMsgAsyncTask {
    private BaiduPush mBaiduPush;
    private Handler mHandler;
    
    private String mMessage;
    private MyAsyncTask mTask;
    private String mUserId;
    private OnSendScuessListener mListener;

    public interface OnSendScuessListener {
        void sendScuess();
    }

    public void setOnSendScuessListener(OnSendScuessListener listener) {
        this.mListener = listener;
    }

    Runnable reSend = new Runnable() {
        @Override
        public void run() {
            // TODO Auto-generated method stub
            L.i("resend msg...");
            send();// 重发
        }
    };
    
    // 发送
    public void send() {
        if (NetUtil.isNetConnected(PushApplication.getInstance())) {// 如果网络可用
            mTask = new MyAsyncTask();
            mTask.execute();
        } else {
            T.showLong(PushApplication.getInstance(), R.string.net_error_tip);
        }
    }

    public SendMsgAsyncTask(String jsonMsg, String useId) {
        mBaiduPush = PushApplication.getInstance().getBaiduPush();
        mMessage = jsonMsg;
        mUserId = useId;
        mHandler = new Handler();
    }


    // 停止
    public void stop() {
        if (mTask != null)
            mTask.cancel(true);
    }

    /**
     * 异步任务
     * 
     * @author xinglong
     *
     */
    class MyAsyncTask extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void...message) {
            String result = "";
            if (TextUtils.isEmpty(mUserId))
                result = mBaiduPush.PushMessage(mMessage);
            else
                result = mBaiduPush.PushMessage(mMessage, mUserId);
            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            L.i("send msg result:" + result);
            if (result.contains(BaiduPush.SEND_MSG_ERROR)) {// 如果消息发送失败，则1s后重发
                mHandler.postDelayed(reSend, 1000);
            } else {
                if (mListener != null)
                    mListener.sendScuess();
            }
        }
    }
}
