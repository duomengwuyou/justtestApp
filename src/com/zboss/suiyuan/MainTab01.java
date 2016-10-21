package com.zboss.suiyuan;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.json.JSONObject;

import com.alibaba.sdk.android.oss.ClientException;
import com.alibaba.sdk.android.oss.OSS;
import com.alibaba.sdk.android.oss.OSSClient;
import com.alibaba.sdk.android.oss.ServiceException;
import com.alibaba.sdk.android.oss.callback.OSSCompletedCallback;
import com.alibaba.sdk.android.oss.internal.OSSAsyncTask;
import com.alibaba.sdk.android.oss.model.PutObjectRequest;
import com.alibaba.sdk.android.oss.model.PutObjectResult;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.zboss.suiyuan.bean.ChatMessage;
import com.zboss.suiyuan.bean.Message;
import com.zboss.suiyuan.chat.ChatConstant;
import com.zboss.suiyuan.chat.ChatMessageAdapter;
import com.zboss.suiyuan.chat.ConnectServer;
import com.zboss.suiyuan.enums.TitleEnum;
import com.zboss.suiyuan.utils.GeneralUtil;
import com.zboss.suiyuan.utils.SendMsgAsyncTask;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

public class MainTab01 extends Fragment {

    private EditText mMsgInput;
    private Button mMsgSend;
    public static Button buildCon;
    public Button voiceBtn;

    public static final String SPLIT_TOKEN = "_#_#_";

    public static ListView mChatMessagesListView;
    public static List<ChatMessage> mDatas = new ArrayList<ChatMessage>();
    public static ChatMessageAdapter mAdapter;

    private View rootView;
    private PushApplication mApplication;
    public static RequestQueue requestQueue;
    private Gson mGson;

    public static FragmentActivity activity;

    /**
     * 选择文件
     */
    public static final int TO_SELECT_PHOTO = 3;

    /**
     * 录音结束
     */
    public static final int TO_SEND_VOICE = 4;

    private String picPath = null;
    private String voicePath = null;
    private float voiceSecondes = 0f;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.main_tab_01, container, false);
        initView();
        initEvent();
        if (requestQueue == null) {
            requestQueue = Volley.newRequestQueue(getActivity());
        }

        if (activity == null) {
            activity = getActivity();
        }

        if (mAdapter == null) {
            mAdapter = new ChatMessageAdapter(getActivity(), mDatas);
        }
        mChatMessagesListView.setAdapter(mAdapter);

        ViewGroup parent = (ViewGroup) rootView.getParent();
        if (parent != null) {
            parent.removeView(rootView);
        }

        return rootView;
    }

    /**
     * 初始化视图
     */
    private void initView() {
        mChatMessagesListView = (ListView) rootView.findViewById(R.id.id_chat_listView);
        mMsgInput = (EditText) rootView.findViewById(R.id.id_chat_msg);
        mMsgSend = (Button) rootView.findViewById(R.id.id_chat_send);
        buildCon = MainActivity.enterChat;
        voiceBtn = (Button) rootView.findViewById(R.id.id_chat_voice);

        if (PushApplication.buildConOrNot) {
            buildCon.setText("断开");
        } else {
            buildCon.setText("连接");
        }

        mApplication = (PushApplication) getActivity().getApplication();
        mGson = mApplication.getGson();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // 用于接收图片返回结果
        if (resultCode == Activity.RESULT_OK && requestCode == TO_SELECT_PHOTO) {
            picPath = data.getStringExtra(SelectPicActivity.KEY_PHOTO_PATH);
            sendPic(picPath);
        } else if (resultCode == Activity.RESULT_OK && requestCode == TO_SEND_VOICE) {
            // 用于接收录音信息
            voicePath = data.getStringExtra(VoiceActivity.KEY_VOICE_PATH);
            voiceSecondes = data.getFloatExtra(VoiceActivity.KEY_VOICE_SECONDS, 1f);
            if (voiceSecondes > 180) {
                Toast.makeText(activity, "语音太长，无法发送！", Toast.LENGTH_SHORT).show();
            } else {
                sendVoice(voicePath, voiceSecondes);
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    // 发送语音给对方
    private void sendVoice(String voicePath, float voiceSeconds) {
        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setIsComing(1);
        chatMessage.setDate(new Date());
        chatMessage.setNickname("寻缘人");
        chatMessage.setSeconds(voiceSeconds);
        chatMessage.setVoicePath(voicePath);

        // 构建消息
        String voiceSecondsStr = String.valueOf(voiceSeconds);
        String message = "语音消息: " + voiceSecondsStr.substring(0, 4) + "秒(点击播放)";
        chatMessage.setMessage(message);

        mDatas.add(chatMessage);
        mAdapter.notifyDataSetChanged();
        mChatMessagesListView.setSelection(mDatas.size() - 1);

        // 上传声音到服务器
        uploadVoiceFileToOSS(voiceSeconds, voicePath);

    }

    // 发送图片给对方
    private void sendPic(String picPath) {
        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setIsComing(1);
        chatMessage.setDate(new Date());
        chatMessage.setNickname("寻缘人");
        chatMessage.setImagePath(picPath);

        mDatas.add(chatMessage);
        mAdapter.notifyDataSetChanged();
        mChatMessagesListView.setSelection(mDatas.size() - 1);

        // 上传图片到服务器
        uploadFileToOSS(picPath);
    }

    // 上传语音到服务器
    private void uploadVoiceFileToOSS(float voiceSeconds, String voicePath) {
        final String randomKey =
                voiceSeconds + SPLIT_TOKEN + PushApplication.MY_CHANNEL_ID + "_" + GeneralUtil.getRandomString(10);
        OSS oss = new OSSClient(getActivity(), PushApplication.endpoint, PushApplication.credentialProvider);
        PutObjectRequest put = new PutObjectRequest(PushApplication.bucketName, randomKey, voicePath);

        OSSAsyncTask task = oss.asyncPutObject(put, new OSSCompletedCallback<PutObjectRequest, PutObjectResult>() {
            @Override
            public void onSuccess(PutObjectRequest request, PutObjectResult result) {
                sendNoticeMessage(TitleEnum.VOICE_SUCCESS, randomKey);
            }

            @Override
            public void onFailure(PutObjectRequest request, ClientException clientExcepion,
                    ServiceException serviceException) {
                // 请求异常
                if (clientExcepion != null) {
                    // 本地异常如网络异常等
                    clientExcepion.printStackTrace();
                    sendNoticeMessage(TitleEnum.VOICE_FAIL, randomKey);
                }
                if (serviceException != null) {
                    // 服务异常
                    sendNoticeMessage(TitleEnum.VOICE_FAIL, randomKey);
                }
            }
        });
    }

    // 上传图片到服务器
    private void uploadFileToOSS(String picPath) {
        final String randomKey = PushApplication.MY_CHANNEL_ID + "_" + GeneralUtil.getRandomString(10);
        OSS oss = new OSSClient(getActivity(), PushApplication.endpoint, PushApplication.credentialProvider);
        PutObjectRequest put = new PutObjectRequest(PushApplication.bucketName, randomKey, picPath);

        OSSAsyncTask task = oss.asyncPutObject(put, new OSSCompletedCallback<PutObjectRequest, PutObjectResult>() {
            @Override
            public void onSuccess(PutObjectRequest request, PutObjectResult result) {
                sendNoticeMessage(TitleEnum.PIC_SUCCESS, randomKey);
            }

            @Override
            public void onFailure(PutObjectRequest request, ClientException clientExcepion,
                    ServiceException serviceException) {
                // 请求异常
                if (clientExcepion != null) {
                    // 本地异常如网络异常等
                    clientExcepion.printStackTrace();
                    sendNoticeMessage(TitleEnum.PIC_FAIL, randomKey);
                }
                if (serviceException != null) {
                    // 服务异常
                    sendNoticeMessage(TitleEnum.PIC_FAIL, randomKey);
                }
            }
        });
    }

    // 发送通知消息
    private void sendNoticeMessage(TitleEnum msg, String randomKey) {

        if (msg == TitleEnum.PIC_SUCCESS) {
            // 构建新的会话
            Message message = new Message(randomKey, PushApplication.YOUR_CHANNEL_ID);
            message.setTitle(msg.getStatus());

            // 发送消息
            Looper.prepare();
            SendMsgAsyncTask newTask = new SendMsgAsyncTask(mGson.toJson(message), PushApplication.YOUR_CHANNEL_ID);
            newTask.send();

            // 界面上失败
            Toast.makeText(activity, "图片发送成功！", Toast.LENGTH_SHORT).show();
            Looper.loop();
        } else if (msg == TitleEnum.VOICE_SUCCESS) {
            // 构建新的会话
            Message message = new Message(randomKey, PushApplication.YOUR_CHANNEL_ID);
            message.setTitle(msg.getStatus());

            // 发送消息
            Looper.prepare();
            SendMsgAsyncTask newTask = new SendMsgAsyncTask(mGson.toJson(message), PushApplication.YOUR_CHANNEL_ID);
            newTask.send();

            // 界面上失败
            Toast.makeText(activity, "语音发送成功！", Toast.LENGTH_SHORT).show();
            Looper.loop();

        } else {
            // 界面上失败
            Looper.prepare();
            Toast.makeText(activity, "抱歉，内容发送失败，请重新发送！", Toast.LENGTH_SHORT).show();
            Looper.loop();
        }

    }

    /**
     * 初始化视图
     */
    private void initEvent() {

        // 发送语音按钮
        voiceBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // 如果尚未建立连接 不能发送对话
                beforeSendMsg();
                if (!PushApplication.buildConOrNot) {
                    Toast.makeText(getActivity(), "建立连接之后才可以发送语音", Toast.LENGTH_SHORT).show();
                    return;
                }

                // 发送语音
                Intent intent = new Intent(activity, VoiceActivity.class);
                startActivityForResult(intent, TO_SEND_VOICE);
            }
        });

        // 文本框变化事件
        mMsgInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String input = mMsgInput.getText().toString();
                input = input.trim();
                if (input.length() > 0) {
                    mMsgSend.setText(ChatConstant.SEND);
                } else {
                    mMsgSend.setText(ChatConstant.PIC);
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        mMsgSend.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // 判断发送内容框是否为空
                String msg = mMsgInput.getText().toString();

                // 如果尚未建立连接 不能发送对话
                beforeSendMsg();
                if (!PushApplication.buildConOrNot) {
                    Toast.makeText(getActivity(), "建立连接之后才可以发送对话", Toast.LENGTH_SHORT).show();
                    return;
                }

                // 获取按钮文字 不同文字触发不同时间
                String mMsgSendText = mMsgSend.getText().toString();
                // 如果是文字
                if (mMsgSendText.equals(ChatConstant.SEND)) {
                    if (TextUtils.isEmpty(msg)) {
                        Toast.makeText(getActivity(), "对话内容不能为空", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    // 构建新的会话
                    Message message = new Message(msg, PushApplication.YOUR_CHANNEL_ID);
                    // 发送消息
                    SendMsgAsyncTask newTask =
                            new SendMsgAsyncTask(mGson.toJson(message), PushApplication.YOUR_CHANNEL_ID);
                    newTask.send();

                    // 界面上构建信息
                    ChatMessage chatMessage = new ChatMessage();
                    chatMessage.setIsComing(1);
                    chatMessage.setDate(new Date());
                    chatMessage.setMessage(msg);
                    chatMessage.setNickname("寻缘人");

                    mDatas.add(chatMessage);
                    mAdapter.notifyDataSetChanged();
                    mChatMessagesListView.setSelection(mDatas.size() - 1);
                    mMsgInput.setText("");
                } else if (mMsgSendText.equals(ChatConstant.PIC)) {
                    // 发送图片
                    Intent intent = new Intent(activity, SelectPicActivity.class);
                    startActivityForResult(intent, TO_SELECT_PHOTO);

                } else {

                }
            }
        });

        buildCon.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // 清空数据
                mDatas.clear();
                mAdapter.notifyDataSetChanged();
                mMsgInput.setText("");

                if (PushApplication.buildConOrNot == false) {
                    buildConnection();
                } else {
                    closeConnection();
                }
                return;
            }
        });
    }

    private void buildConnection() {
        String JSONDataUrl =
                ConnectServer.getUploadInfoPath(PushApplication.APP_ID, PushApplication.USER_ID,
                        PushApplication.MY_CHANNEL_ID);
        final ProgressDialog progressDialog = ProgressDialog.show(getActivity(), "寻找有缘人...", "请稍等...", true, false);

        JsonObjectRequest jsonObjectRequest =
                new JsonObjectRequest(Request.Method.GET, JSONDataUrl, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // 处理返回的JSON数据
                        if (progressDialog.isShowing() && progressDialog != null) {
                            try {
                                int status = response.getInt("status");
                                // 排队
                                if (status == -1) {
                                    Toast.makeText(getActivity(), "恭喜你，已加入聊天队列，等待有缘人联系你吧！", Toast.LENGTH_SHORT).show();
                                    PushApplication.YOUR_CHANNEL_ID = null;
                                    PushApplication.buildConOrNot = false;
                                    // 奖励用户图片
                                    if (PushApplication.wifiOrNot) {
                                        MainTab02.loadMoreImages(true);
                                    }
                                    progressDialog.dismiss();
                                    // 找到了
                                } else if (status == 1) {
                                    progressDialog.dismiss();

                                    // 数据有误
                                } else if (status == 0) {
                                    Toast.makeText(getActivity(), "抱歉，认证信息有误，请重启软件或者确定网络连接！", Toast.LENGTH_SHORT)
                                            .show();
                                    PushApplication.YOUR_CHANNEL_ID = null;
                                    PushApplication.buildConOrNot = false;
                                    // 奖励用户图片
                                    if (PushApplication.wifiOrNot) {
                                        MainTab02.loadMoreImages(true);
                                    }
                                    progressDialog.dismiss();
                                } else {
                                    Toast.makeText(getActivity(), "抱歉，目前聊天人数过多，请稍后再来！", Toast.LENGTH_SHORT).show();
                                    PushApplication.YOUR_CHANNEL_ID = null;
                                    PushApplication.buildConOrNot = false;
                                    // 奖励用户图片
                                    if (PushApplication.wifiOrNot) {
                                        MainTab02.loadMoreImages(true);
                                    }
                                    progressDialog.dismiss();
                                }

                            } catch (Exception e) {
                                Toast.makeText(getActivity(), "抱歉，目前聊天人数过多，请稍后再来！", Toast.LENGTH_SHORT).show();
                                PushApplication.YOUR_CHANNEL_ID = null;
                                PushApplication.buildConOrNot = false;
                                // 奖励用户图片
                                if (PushApplication.wifiOrNot) {
                                    MainTab02.loadMoreImages(true);
                                }
                                progressDialog.dismiss();
                            }

                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError arg0) {
                        Toast.makeText(getActivity(), "抱歉，目前聊天人数过多，请稍后再来！", Toast.LENGTH_SHORT).show();
                        PushApplication.YOUR_CHANNEL_ID = null;
                        PushApplication.buildConOrNot = false;
                        // 奖励用户图片
                        if (PushApplication.wifiOrNot) {
                            MainTab02.loadMoreImages(true);
                        }
                        progressDialog.dismiss();
                    }
                });
        requestQueue.add(jsonObjectRequest);
    }

    private void closeConnection() {
        String JSONDataUrl =
                ConnectServer.getCloseCon(PushApplication.APP_ID, PushApplication.USER_ID,
                        PushApplication.MY_CHANNEL_ID);
        final ProgressDialog progressDialog = ProgressDialog.show(getActivity(), "断开缘分...", "请稍等...", true, false);

        JsonObjectRequest jsonObjectRequest =
                new JsonObjectRequest(Request.Method.GET, JSONDataUrl, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // 处理返回的JSON数据
                        if (progressDialog.isShowing() && progressDialog != null) {
                            Toast.makeText(getActivity(), "断开连接成功", Toast.LENGTH_SHORT).show();
                            PushApplication.YOUR_CHANNEL_ID = null;
                            PushApplication.buildConOrNot = false;
                            buildCon.setText("连接");
                            progressDialog.dismiss();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError arg0) {
                        Toast.makeText(getActivity(), "断开连接成功，", Toast.LENGTH_SHORT).show();
                        PushApplication.YOUR_CHANNEL_ID = null;
                        PushApplication.buildConOrNot = false;
                        buildCon.setText("连接");
                        progressDialog.dismiss();
                    }
                });
        requestQueue.add(jsonObjectRequest);
    }

    /**
     * 发送消息前验证
     */
    public static void beforeSendMsg() {

        // 如果目前没有连接对象
        if (PushApplication.YOUR_CHANNEL_ID == null) {
            PushApplication.buildConOrNot = false;
            buildCon.setText("连接");
        }
    }

    class Recorder {
        float time;
        String filePathString;

        public Recorder(float time, String filePathString) {
            super();
            this.time = time;
            this.filePathString = filePathString;
        }

        public float getTime() {
            return time;
        }

        public void setTime(float time) {
            this.time = time;
        }

        public String getFilePathString() {
            return filePathString;
        }

        public void setFilePathString(String filePathString) {
            this.filePathString = filePathString;
        }

    }

}
