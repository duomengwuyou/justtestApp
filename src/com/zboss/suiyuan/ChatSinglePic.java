package com.zboss.suiyuan;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Date;

import com.zboss.suiyuan.bean.ChatMessage;
import com.zboss.suiyuan.chat.ChatConstant;
import com.zboss.suiyuan.newspic.ZoomImageView;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Toast;

public class ChatSinglePic extends Activity {

    private ZoomImageView image;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.chatsinglepic);
        image = (ZoomImageView) findViewById(R.id.chatsingleimg);

        if (ChatConstant.bitMap != null) {
            image.setImage(ChatConstant.bitMap);
        }

        double num = Math.random();
        if (num < 0.8) {
            Toast.makeText(ChatSinglePic.this, "提示：用两个手指可以实现图片放大缩小", Toast.LENGTH_SHORT).show();
        }

        image.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(ChatSinglePic.this);
                builder.setItems(new String[] { "保存图片" }, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        image.setDrawingCacheEnabled(true);
                        Bitmap imageBitmap = image.getDrawingCache();
                        if (imageBitmap != null) {
                            new SaveImageTask().execute(imageBitmap);
                        }
                    }
                });
                builder.show();
                return true;
            }
        });
    }

    private class SaveImageTask extends AsyncTask<Bitmap, Void, String> {
        @Override
        protected String doInBackground(Bitmap...params) {
            String result = "抱歉，保存图片失败";
            try {
                String sdcard = Environment.getExternalStorageDirectory().toString();

                File file = new File(sdcard + "/Download");
                if (!file.exists()) {
                    file.mkdirs();
                }

                File imageFile = new File(file.getAbsolutePath(), new Date().getTime() + ".jpg");
                FileOutputStream outStream = null;
                outStream = new FileOutputStream(imageFile);
                Bitmap image = params[0];
                image.compress(Bitmap.CompressFormat.JPEG, 100, outStream);
                outStream.flush();
                outStream.close();
                result = "图片保存成功！" + file.getAbsolutePath();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            Toast.makeText(getApplicationContext(), result, Toast.LENGTH_SHORT).show();
            image.setDrawingCacheEnabled(false);
        }
    }

}