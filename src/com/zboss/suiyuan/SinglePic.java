package com.zboss.suiyuan;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.Volley;
import com.zboss.suiyuan.newspic.ZoomImageView;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.os.Bundle;

public class SinglePic extends Activity {

    private ZoomImageView zoomImg;
    public static RequestQueue mQueue;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(mQueue == null) {
            mQueue = Volley.newRequestQueue(SinglePic.this);
        }

        Bundle bundle = getIntent().getExtras();
        String imagePath = bundle.getString("imagePath");// 读出数据

        setContentView(R.layout.singlepic);
        zoomImg = (ZoomImageView) findViewById(R.id.singleimg);
        final Bitmap bitmap = BitmapFactory.decodeResource(this.getResources(), R.drawable.icon);
        zoomImg.setImage(bitmap);
        getImage(imagePath);
        
    }
    
    public void getImage(String imagePath) {
        ImageRequest imageRequest = new ImageRequest(imagePath, new Response.Listener<Bitmap>() {
            @Override
            public void onResponse(Bitmap response) {
                zoomImg.setImage(response);
            }
        }, 0, 0, Config.RGB_565, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                
            }
        });
        mQueue.add(imageRequest);
    }
}