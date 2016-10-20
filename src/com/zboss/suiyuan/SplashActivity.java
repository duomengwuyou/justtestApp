package com.zboss.suiyuan;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.widget.TextView;

/**
 * 闪屏页
 * 
 * @author xinglong
 *
 */
public class SplashActivity extends Activity {

    private MyCountDownTimer mc;
    private Handler handler = new Handler();
    private TextView tv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_main);

        tv = (TextView) findViewById(R.id.timetiker);
        mc = new MyCountDownTimer(3000, 1000);
        mc.start();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                startActivity(intent);
                SplashActivity.this.finish();
            }
        }, 3000);
    }

    /**
     * 继承 CountDownTimer 防范
     * 
     * 重写 父类的方法 onTick() 、 onFinish()
     */
    class MyCountDownTimer extends CountDownTimer {
        /**
         * 
         * @param millisInFuture 表示以毫秒为单位 倒计时的总数
         * 
         *            例如 millisInFuture=1000 表示1秒
         * 
         * @param countDownInterval 表示 间隔 多少微秒 调用一次 onTick 方法
         * 
         *            例如: countDownInterval =1000 ; 表示每1000毫秒调用一次onTick()
         * 
         */
        public MyCountDownTimer(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        public void onFinish() {
            tv.setText("正在跳转...");
        }

        public void onTick(long millisUntilFinished) {
            tv.setText("倒计时(" + millisUntilFinished / 1000 + "s)");
        }
    }

}