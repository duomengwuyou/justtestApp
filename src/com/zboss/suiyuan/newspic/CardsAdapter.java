package com.zboss.suiyuan.newspic;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.ImageLoader.ImageCache;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.Volley;
import com.zboss.suiyuan.R;
import com.zboss.suiyuan.bean.PictureObj;
import com.zboss.suiyuan.utils.AsyncImageLoader;
import com.zboss.suiyuan.utils.AsyncImageLoader.ImageCallback;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.support.v4.util.LruCache;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class CardsAdapter extends BaseAdapter {

    private List<PictureObj> items;
    private final OnClickListener itemButtonClickListener;
    private final Context context;
    
    private static RequestQueue requestQueue;
    private static LruCache<String, Bitmap> lruCache;
    private static ImageCache imageCache;
    private static ImageLoader imageLoader;


    private static Map<String, Boolean> picMap = new HashMap<String, Boolean>();

    public CardsAdapter(Context context, List<PictureObj> items, OnClickListener itemButtonClickListener) {
        this.context = context;
        this.items = items;
        this.itemButtonClickListener = itemButtonClickListener;
        requestQueue = Volley.newRequestQueue(context);
        lruCache = new LruCache<String, Bitmap>(100);
        
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
            //holder.itemButton1 = (Button) convertView.findViewById(R.id.list_item_card_button_1);
            //holder.itemButton2 = (Button) convertView.findViewById(R.id.list_item_card_button_2);
            convertView.setTag(holder);

        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.Card_Title.setText(items.get(position).getTitle());
        
        // Load the image and set it on the ImageView
        String imageUrl = items.get(position).getPic();
        showImageByNetworkImageView(holder.Card_Pic, imageUrl);
        

        // if (itemButtonClickListener != null) {
        // holder.itemButton1.setOnClickListener(itemButtonClickListener);
        // holder.itemButton2.setOnClickListener(itemButtonClickListener);
        // }

        // holder.itemButton1.setOnClickListener(new OnClickListener() {
        //
        // @Override
        // public void onClick(View v) {
        // // TODO Auto-generated method stub
        // Toast.makeText(context, "Clicked on Left Action Button of List Item " + position, Toast.LENGTH_SHORT)
        // .show();
        // }
        // });
        // holder.itemButton2.setOnClickListener(new OnClickListener() {
        //
        // @Override
        // public void onClick(View v) {
        // // TODO Auto-generated method stub
        // Toast.makeText(context, "Clicked on right Action Button of List Item " + position, Toast.LENGTH_SHORT)
        // .show();
        // }
        // });

        return convertView;
    }

    private static class ViewHolder {
        private TextView Card_Title;
        private NetworkImageView Card_Pic;
        // private Button itemButton1;
        // private Button itemButton2;
    }
    
    /**
     * 利用NetworkImageView显示网络图片
     */
    private void showImageByNetworkImageView(NetworkImageView mNetworkImageView, String imageUrl) {
        mNetworkImageView.setTag("url");
        mNetworkImageView.setImageUrl(imageUrl, imageLoader);
    }

}
