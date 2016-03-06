package com.zboss.suiyuan.newspic;


import java.util.List;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.ImageLoader.ImageCache;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.Volley;
import com.zboss.suiyuan.R;
import com.zboss.suiyuan.SingleNews;
import com.zboss.suiyuan.SinglePic;
import com.zboss.suiyuan.bean.NewsObj;
import com.zboss.suiyuan.bean.PictureObj;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v4.util.LruCache;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class NewsCardsAdapter extends BaseAdapter {

    private List<NewsObj> items;
    private final Context context;
    

    public NewsCardsAdapter(Context context, List<NewsObj> items) {
        this.context = context;
        this.items = items;
    }

    public List<NewsObj> getItems() {
        return items;
    }

    public void setItems(List<NewsObj> items) {
        this.items = items;
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public NewsObj getItem(int position) {
        return items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        final ViewHolder holder;

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.list_news_card, null);
            holder = new ViewHolder();
            holder.newsTitle = (TextView) convertView.findViewById(R.id.news_title);
            holder.newsContent = (TextView) convertView.findViewById(R.id.news_content);
            holder.newsDate = (TextView) convertView.findViewById(R.id.news_date);
            holder.readMore = (TextView) convertView.findViewById(R.id.news_read_more);
            convertView.setTag(holder);

        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.newsTitle.setText(items.get(position).getWebsite());
        holder.newsContent.setText(items.get(position).getContent());
        holder.newsDate.setText(items.get(position).getDatetime());
       
        final String newsPath = items.get(position).getSrcpath();
        holder.readMore.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, SingleNews.class);
                intent.putExtra("newsPath", newsPath); 
                context.startActivity(intent);
            }
        });
        
        return convertView;
    }

    private static class ViewHolder {
        private TextView newsTitle;
        private TextView newsContent;
        private TextView newsDate;
        private TextView readMore;
        
    }
}
