package com.zboss.suiyuan;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.zboss.suiyuan.bean.ChatMessage;
import com.zboss.suiyuan.bean.Message;
import com.zboss.suiyuan.chat.ChatMessageAdapter;
import com.zboss.suiyuan.chat.ConnectServer;
import com.zboss.suiyuan.enums.TitleEnum;
import com.zboss.suiyuan.utils.SendMsgAsyncTask;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

public class MainTab01 extends Fragment {

    private EditText mMsgInput;
    private Button mMsgSend;
    public static Button buildCon;

    public static ListView mChatMessagesListView;
    public static List<ChatMessage> mDatas = new ArrayList<ChatMessage>();
    public static ChatMessageAdapter mAdapter;

    private View rootView;

    private PushApplication mApplication;

    // private User mFromUser;
    // private UserDB mUserDB;
    // private SharePreferenceUtil mSpUtil;

    private Gson mGson;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.main_tab_01, container, false);
        initView();
        initEvent();

        if (mAdapter == null) {
            mAdapter = new ChatMessageAdapter(getActivity(), mDatas);
        }
        mChatMessagesListView.setAdapter(mAdapter);
        return rootView;
    }

    /**
     * 初始化视图
     */
    private void initView() {
        mChatMessagesListView = (ListView) rootView.findViewById(R.id.id_chat_listView);
        mMsgInput = (EditText) rootView.findViewById(R.id.id_chat_msg);
        mMsgSend = (Button) rootView.findViewById(R.id.id_chat_send);
        buildCon = (Button) rootView.findViewById(R.id.build_chat_con);

        mApplication = (PushApplication) getActivity().getApplication();
        mGson = mApplication.getGson();
    }

    /**
     * 初始化视图
     */
    private void initEvent() {
        mMsgSend.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // 判断发送内容框是否为空
                String msg = mMsgInput.getText().toString();
                // 如果尚未建立连接 不能发送对话
                if (!PushApplication.buildConOrNot) {
                    Toast.makeText(getActivity(), "建立连接之后才可以发送对话", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(msg)) {
                    Toast.makeText(getActivity(), "对话内容不能为空", Toast.LENGTH_SHORT).show();
                    return;
                }

                // 构建新的会话
                Message message = new Message(msg, PushApplication.YOUR_CHANNEL_ID);
                // 发送消息
                SendMsgAsyncTask newTask = new SendMsgAsyncTask(mGson.toJson(message), PushApplication.YOUR_CHANNEL_ID);
                newTask.send();

                // 界面上构建信息
                ChatMessage chatMessage = new ChatMessage();
                chatMessage.setComing(false);
                chatMessage.setDate(new Date());
                chatMessage.setMessage(msg);
                chatMessage.setNickname("寻缘人");

                mDatas.add(chatMessage);
                mAdapter.notifyDataSetChanged();
                mChatMessagesListView.setSelection(mDatas.size() - 1);
                mMsgInput.setText("");

                // 键盘自动关闭
                InputMethodManager imm =
                        (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                // 得到InputMethodManager的实例
                if (imm.isActive()) {
                    // 如果开启
                    imm.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, InputMethodManager.HIDE_NOT_ALWAYS);
                    // 关闭软键盘，开启方法相同，这个方法是切换开启与关闭状态的
                }

            }
        });

        buildCon.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
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
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
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
                                    progressDialog.dismiss();
                                    // 找到了
                                } else if (status == 1) {
                                    String loveChannel = response.getString("loveChannelId");
                                    Toast.makeText(getActivity(), "恭喜你，找到了有缘人，可以开始对话了！", Toast.LENGTH_SHORT).show();
                                    PushApplication.YOUR_CHANNEL_ID = loveChannel;
                                    PushApplication.buildConOrNot = true;
                                    buildCon.setText("断开");
                                    progressDialog.dismiss();

                                    // 马上给对方发送消息
                                    Message message = new Message(PushApplication.MY_CHANNEL_ID, PushApplication.YOUR_CHANNEL_ID);
                                    message.setTitle(TitleEnum.BUILD_CONNECTION.getStatus());
                                    // 发送消息
                                    SendMsgAsyncTask newTask =
                                            new SendMsgAsyncTask(mGson.toJson(message), PushApplication.YOUR_CHANNEL_ID);
                                    newTask.send();

                                } else {
                                    Toast.makeText(getActivity(), "抱歉，目前聊天人数过多，请稍后再来！", Toast.LENGTH_SHORT).show();
                                    PushApplication.YOUR_CHANNEL_ID = null;
                                    PushApplication.buildConOrNot = false;
                                    progressDialog.dismiss();
                                }

                            } catch (Exception e) {
                                Toast.makeText(getActivity(), "抱歉，目前聊天人数过多，请稍后再来！", Toast.LENGTH_SHORT).show();
                                PushApplication.YOUR_CHANNEL_ID = null;
                                PushApplication.buildConOrNot = false;
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
                        progressDialog.dismiss();
                    }
                });
        requestQueue.add(jsonObjectRequest);
    }

    private void closeConnection() {
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
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
                            // 马上给对方发送消息
                            Message message = new Message(PushApplication.MY_CHANNEL_ID, PushApplication.YOUR_CHANNEL_ID);
                            message.setTitle(TitleEnum.CLOSE_CONNECTION.getStatus());
                            // 发送消息
                            SendMsgAsyncTask newTask =
                                    new SendMsgAsyncTask(mGson.toJson(message), PushApplication.YOUR_CHANNEL_ID);
                            newTask.send();
                            
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

}
