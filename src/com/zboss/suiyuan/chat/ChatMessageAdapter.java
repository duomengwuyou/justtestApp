package com.zboss.suiyuan.chat;

import java.util.List;

import com.zboss.suiyuan.R;
import com.zboss.suiyuan.R.id;
import com.zboss.suiyuan.R.layout;
import com.zboss.suiyuan.bean.ChatMessage;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class ChatMessageAdapter extends BaseAdapter {
    private LayoutInflater mInflater;
    private List<ChatMessage> mDatas;

    public ChatMessageAdapter(Context context, List<ChatMessage> datas) {
        mInflater = LayoutInflater.from(context);
        mDatas = datas;
    }

    @Override
    public int getCount() {
        return mDatas.size();
    }

    @Override
    public Object getItem(int position) {
        return mDatas.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        ChatMessage msg = mDatas.get(position);
        return msg.getIsComing() == 2 ? 1 : 0;
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ChatMessage chatMessage = mDatas.get(position);

        ViewHolder viewHolder = null;

        if (convertView == null) {
            viewHolder = new ViewHolder();
            // 收到消息
            if (chatMessage.getIsComing() == 2) {
                convertView = mInflater.inflate(R.layout.main_chat_from_msg, parent, false);
                viewHolder.createDate = (TextView) convertView.findViewById(R.id.chat_from_createDate);
                viewHolder.content = (TextView) convertView.findViewById(R.id.chat_from_content);
                viewHolder.nickname = (TextView) convertView.findViewById(R.id.chat_from_name);
                viewHolder.chatImage = (ImageView) convertView.findViewById(R.id.chat_send_image);
                convertView.setTag(viewHolder);
                // 发出消息
            } else if (chatMessage.getIsComing() == 1) {
                convertView = mInflater.inflate(R.layout.main_chat_send_msg, null);
                viewHolder.createDate = (TextView) convertView.findViewById(R.id.chat_send_createDate);
                viewHolder.content = (TextView) convertView.findViewById(R.id.chat_send_content);
                viewHolder.nickname = (TextView) convertView.findViewById(R.id.chat_send_name);
                viewHolder.chatImage = (ImageView) convertView.findViewById(R.id.chat_send_image);
                convertView.setTag(viewHolder);
                // 系统消息
            } else {
                convertView = mInflater.inflate(R.layout.main_chat_send_msg, null);
                viewHolder.content = (TextView) convertView.findViewById(R.id.chat_send_content);
                viewHolder.nickname = (TextView) convertView.findViewById(R.id.chat_send_name);
                viewHolder.createDate = (TextView) convertView.findViewById(R.id.chat_send_createDate);
                viewHolder.chatImage = (ImageView) convertView.findViewById(R.id.chat_send_image);
                convertView.setTag(viewHolder);
            }

        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        // 非系统消息
        if (chatMessage.getIsComing() != 3) {
            // 判断当前是否是图片类型
            if (chatMessage.getImagePath() != null) {
                Bitmap bm = BitmapFactory.decodeFile(chatMessage.getImagePath());
                viewHolder.chatImage.setImageBitmap(bm);
                viewHolder.chatImage.setVisibility(View.VISIBLE);

                viewHolder.content.setVisibility(View.GONE);
                viewHolder.nickname.setVisibility(View.VISIBLE);
                if (convertView.findViewById(R.id.chat_send_icon) != null) {
                    convertView.findViewById(R.id.chat_send_icon).setVisibility(View.VISIBLE);
                }
                viewHolder.createDate.setText(chatMessage.getDateStr());
                viewHolder.nickname.setText(chatMessage.getNickname());
            } else {
                viewHolder.chatImage.setVisibility(View.GONE);
                viewHolder.content.setVisibility(View.VISIBLE);
                viewHolder.nickname.setVisibility(View.VISIBLE);
                if (convertView.findViewById(R.id.chat_send_icon) != null) {
                    convertView.findViewById(R.id.chat_send_icon).setVisibility(View.VISIBLE);
                }

                viewHolder.content.setText(chatMessage.getMessage());
                viewHolder.createDate.setText(chatMessage.getDateStr());
                viewHolder.nickname.setText(chatMessage.getNickname());
            }
            // 系统消息
        } else {
            viewHolder.createDate.setText(chatMessage.getDateStr());

            viewHolder.content.setVisibility(View.GONE);
            viewHolder.chatImage.setVisibility(View.GONE);
            viewHolder.nickname.setVisibility(View.GONE);
            convertView.findViewById(R.id.chat_send_icon).setVisibility(View.GONE);
        }

        return convertView;
    }

    private class ViewHolder {
        public TextView createDate;
        public TextView nickname;
        public TextView content;
        public ImageView chatImage;
    }

}
