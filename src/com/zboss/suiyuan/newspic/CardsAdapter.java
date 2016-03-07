package com.zboss.suiyuan.newspic;


import java.util.List;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.ImageLoader.ImageCache;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.Volley;
import com.zboss.suiyuan.R;
import com.zboss.suiyuan.SinglePic;
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
import android.widget.TextView;
import android.widget.Toast;

public class CardsAdapter extends BaseAdapter {

    private List<PictureObj> items;
    private final Context context;
    
    public static RequestQueue requestQueue;
    public static LruCache<String, Bitmap> lruCache;
    public static ImageCache imageCache;
    public static ImageLoader imageLoader;

    public CardsAdapter(Context context, List<PictureObj> items) {
        this.context = context;
        this.items = items;
        requestQueue = Volley.newRequestQueue(context);
        lruCache = new LruCache<String, Bitmap>(200);
        
        imageCache = new ImageCache() {
            @Override
            public void putBitmap(String key, Bitmap value) {
                lruCache.put(key, value);
            }

            @Override
            public Bitmap getBitmap(String key) {
                return lruCache.get(key);
            }
        };
        
        imageLoader = new ImageLoader(requestQueue, imageCache);

    }

    public List<PictureObj> getItems() {
        return items;
    }

    public void setItems(List<PictureObj> items) {
        this.items = items;
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public PictureObj getItem(int position) {
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
            convertView = LayoutInflater.from(context).inflate(R.layout.list_item_card, null);
            holder = new ViewHolder();
            holder.Card_Title = (TextView) convertView.findViewById(R.id.Card_Title);
            holder.Card_Pic = (NetworkImageView) convertView.findViewById(R.id.Card_Pic);
            convertView.setTag(holder);

        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.Card_Title.setText(items.get(position).getTitle());

        holder.Card_Pic.setDefaultImageResId(R.drawable.icon);
        holder.Card_Pic.setErrorImageResId(R.drawable.error);
        // Load the image and set it on the ImageView
        final String imageUrl = items.get(position).getPic();
        showImageByNetworkImageView(holder.Card_Pic, imageUrl);
        
        holder.Card_Pic.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, SinglePic.class);
                intent.putExtra("imagePath", imageUrl); 
                context.startActivity(intent);
            }
        });
        return convertView;
    }

    private static class ViewHolder {
        private TextView Card_Title;
        private NetworkImageView Card_Pic;
    }
    
    /**
     * 利用NetworkImageView显示网络图片
     */
    private void showImageByNetworkImageView(NetworkImageView mNetworkImageView, String imageUrl) {
        mNetworkImageView.setTag("url");
        mNetworkImageView.setImageUrl(imageUrl, imageLoader);
    }

}
