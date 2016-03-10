package com.zboss.suiyuan;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.baidu.android.pushservice.PushConstants;
import com.baidu.android.pushservice.PushManager;
import com.jauker.widget.BadgeView;
import com.zboss.suiyuan.bean.Message;
import com.zboss.suiyuan.chat.ConnectServer;
import com.zboss.suiyuan.enums.TitleEnum;
import com.zboss.suiyuan.utils.SendMsgAsyncTask;

public class MainActivity extends FragmentActivity {
    public static ViewPager mViewPager;
    private static FragmentPagerAdapter mAdapter;
    private static List<Fragment> mFragments = new ArrayList<Fragment>();

    public static LinearLayout mTabLiaotian;
    public static LinearLayout mTabFaxian;
    public static LinearLayout mTabTongxunlu;

    private static TextView mLiaotian;
    private static TextView mFaxian;
    private static TextView mTongxunlu;

    public static BadgeView mBadgeViewforLiaotian;
    public static BadgeView mBadgeViewforFaxian;
    public static BadgeView mBadgeViewforTongxunlu;

    private static ImageView mTabLine;

    // 设置图表
    private static ImageView setImage;
    private static ImageView newsSet;
    private static ImageView liaotianset;

    public static int currentIndex;
    private static int screenWidth;

    // 图片类型选择相关
    private RadioOnClick radioOnClick = new RadioOnClick(1);
    private NewsRadioOnClick newsRadioOnClick = new NewsRadioOnClick(1);
    private static ListView areaRadioListView;
    private static ListView newsRadioListView;
    
    private static FaxianClick faxianClickListener;
    private static LiaotianClick liaotianClickListener;
    private static TongxunluClick tongxunluClickListener;
    private static PageOnPageChangeListener pageChangeLister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // 消息推送
        PushManager.startWork(getApplicationContext(), PushConstants.LOGIN_TYPE_API_KEY, PushApplication.API_KEY);

        mViewPager = (ViewPager) findViewById(R.id.id_viewpager);
        

        // 实例化视图
        initView();
        // 初始化tab line长度
        initTabLine();

        mAdapter = new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public int getCount() {
                return mFragments.size();
            }

            @Override
            public Fragment getItem(int arg0) {
                return mFragments.get(arg0);
            }
            
        };

        // 设置碎片适配器
        mViewPager.setAdapter(mAdapter);
        
        faxianClickListener = new FaxianClick();
        liaotianClickListener = new LiaotianClick();
        tongxunluClickListener = new TongxunluClick();
        pageChangeLister = new PageOnPageChangeListener();

        // 页面改变的时候触发事件
        mViewPager.setOnPageChangeListener(pageChangeLister);

        mTabLiaotian.setOnClickListener(liaotianClickListener);
        mFaxian.setOnClickListener(faxianClickListener);
        mTongxunlu.setOnClickListener(tongxunluClickListener);

        setImage.setOnClickListener(new RadioClickListener());
        newsSet.setOnClickListener(new NewsRadioClickListener());
        liaotianset.setOnClickListener(new FindMore());

        // 默认选择第一个标签
        mViewPager.setCurrentItem(0);
    }
    
    
    

    class PageOnPageChangeListener implements OnPageChangeListener {
        @Override
        public void onPageSelected(int position) {
            resetTextView();
            switch (position) {
                case 0:
                    mTabLiaotian.removeView(mBadgeViewforLiaotian);
                    mBadgeViewforLiaotian.setBadgeCount(0);
                    mLiaotian.setTextColor(getResources().getColor(R.color.green));

                    // 隐藏图标
                    liaotianset.setVisibility(View.VISIBLE);
                    setImage.setVisibility(View.GONE);
                    newsSet.setVisibility(View.GONE);
                    break;
                case 1:
                    mFaxian.setTextColor(getResources().getColor(R.color.green));

                    mTabFaxian.removeView(mBadgeViewforFaxian);
                    mBadgeViewforFaxian.setBadgeCount(0);
                    // mTabFaxian.addView(mBadgeViewforFaxian);

                    // 隐藏图标
                    liaotianset.setVisibility(View.GONE);
                    setImage.setVisibility(View.VISIBLE);
                    newsSet.setVisibility(View.GONE);
                    break;
                case 2:
                    mTongxunlu.setTextColor(getResources().getColor(R.color.green));
                    mTabTongxunlu.removeView(mBadgeViewforTongxunlu);
                    mBadgeViewforTongxunlu.setBadgeCount(0);

                    // 隐藏图标
                    liaotianset.setVisibility(View.GONE);
                    setImage.setVisibility(View.GONE);
                    newsSet.setVisibility(View.VISIBLE);
                    break;
            }
            currentIndex = position;
        }

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            if (currentIndex == 0 && position == 0)// 0->1
            {
                LinearLayout.LayoutParams lp = (android.widget.LinearLayout.LayoutParams) mTabLine.getLayoutParams();
                lp.leftMargin = (int) (positionOffset * (screenWidth * 1.0 / 3) + currentIndex * (screenWidth / 3));
                mTabLine.setLayoutParams(lp);
            } else if (currentIndex == 1 && position == 0) // 1->0
            {
                LinearLayout.LayoutParams lp = (android.widget.LinearLayout.LayoutParams) mTabLine.getLayoutParams();
                lp.leftMargin =
                        (int) (-(1 - positionOffset) * (screenWidth * 1.0 / 3) + currentIndex * (screenWidth / 3));
                mTabLine.setLayoutParams(lp);

            } else if (currentIndex == 1 && position == 1) // 1->2
            {
                LinearLayout.LayoutParams lp = (android.widget.LinearLayout.LayoutParams) mTabLine.getLayoutParams();
                lp.leftMargin = (int) (positionOffset * (screenWidth * 1.0 / 3) + currentIndex * (screenWidth / 3));
                mTabLine.setLayoutParams(lp);
            } else if (currentIndex == 2 && position == 1) // 2->1
            {
                LinearLayout.LayoutParams lp = (android.widget.LinearLayout.LayoutParams) mTabLine.getLayoutParams();
                lp.leftMargin =
                        (int) (-(1 - positionOffset) * (screenWidth * 1.0 / 3) + currentIndex * (screenWidth / 3));
                mTabLine.setLayoutParams(lp);

            } else if (currentIndex == 2 && position == 0) // 2->0
            {
                LinearLayout.LayoutParams lp = (android.widget.LinearLayout.LayoutParams) mTabLine.getLayoutParams();
                lp.leftMargin =
                        (int) (-(1 - positionOffset) * (screenWidth * 1.0 / 3) + currentIndex * (screenWidth / 3));
                mTabLine.setLayoutParams(lp);

            } else if (currentIndex == 0 && position == 2) // 0->2
            {
                LinearLayout.LayoutParams lp = (android.widget.LinearLayout.LayoutParams) mTabLine.getLayoutParams();
                lp.leftMargin =
                        (int) (-(1 - positionOffset) * (screenWidth * 1.0 / 3) + currentIndex * (screenWidth / 3));
                mTabLine.setLayoutParams(lp);

            } else if (currentIndex == 2 && position == 2) // 2->0
            {
                LinearLayout.LayoutParams lp = (android.widget.LinearLayout.LayoutParams) mTabLine.getLayoutParams();
                lp.leftMargin = (int) (positionOffset * (screenWidth * 1.0 / 3) + currentIndex * (screenWidth / 3));
                mTabLine.setLayoutParams(lp);
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {
        }

    }
    
    class FaxianClick implements OnClickListener{
        @Override
        public void onClick(View v) {
            mViewPager.setCurrentItem(1);
            return;
        }
    }
    
    class LiaotianClick implements OnClickListener{
        @Override
        public void onClick(View v) {
            mViewPager.setCurrentItem(0);
            return;
        }
    }
    
    class TongxunluClick implements OnClickListener{
        @Override
        public void onClick(View v) {
            mViewPager.setCurrentItem(2);
            return;
        }
    }
    
    class FindMore implements OnClickListener{
        @Override
        public void onClick(View v) {
            AlertDialog.Builder builder = new Builder(MainActivity.this);
            builder.setMessage("亲，查看更多精彩内容?");
            builder.setTitle("福利来袭");
            builder.setIcon(android.R.drawable.btn_star);
            builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    Uri uri = Uri.parse("http://www.suiyuan521.com/");  
                    Intent it = new Intent(Intent.ACTION_VIEW, uri);  
                    startActivity(it);
                }
            });

            builder.setNegativeButton("取消", new android.content.DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });

            builder.create().show();
        }
    }

    /**
     * 选择图片按钮事件
     * 
     * @author xinglong
     *
     */
    class RadioClickListener implements OnClickListener {
        @Override
        public void onClick(View v) {
            int index = 0;
            if (PushApplication.DISPLAY_TYPE != null) {
                for (Integer i : PushApplication.picids) {
                    if (PushApplication.DISPLAY_TYPE != i) {
                        index++;
                    } else {
                        break;
                    }
                }
            }
            AlertDialog ad =
                    new AlertDialog.Builder(MainActivity.this).setTitle("选择图片类型")
                            .setSingleChoiceItems(PushApplication.pictypes, index, radioOnClick).create();
            areaRadioListView = ad.getListView();
            ad.show();
        }
    }

    class NewsRadioClickListener implements OnClickListener {
        @Override
        public void onClick(View v) {
            int index = 0;
            if (PushApplication.NEWS_DISPLAY_TYPE != null) {
                for (Integer i : PushApplication.newsids) {
                    if (PushApplication.NEWS_DISPLAY_TYPE != (long) i) {
                        index++;
                    } else {
                        break;
                    }
                }
            }
            AlertDialog ad =
                    new AlertDialog.Builder(MainActivity.this).setTitle("选择文章类型")
                            .setSingleChoiceItems(PushApplication.newstypes, index, newsRadioOnClick).create();
            newsRadioListView = ad.getListView();
            ad.show();
        }
    }

    /**
     * 点击单选框事件
     * 
     * @author xmz
     * 
     */
    class RadioOnClick implements DialogInterface.OnClickListener {
        private int index;

        public RadioOnClick(int index) {
            this.index = index;
        }

        public void setIndex(int index) {
            this.index = index;
        }

        public int getIndex() {
            return index;
        }

        public void onClick(DialogInterface dialog, int whichButton) {
            setIndex(whichButton);
            Toast.makeText(MainActivity.this, "您选择了： " + PushApplication.pictypes[index], Toast.LENGTH_LONG).show();
            if (index == 0) {
                PushApplication.DISPLAY_TYPE = null;
                MainTab02.resetPicList();
            } else {
                // 设置图片类型
                PushApplication.DISPLAY_TYPE = PushApplication.picids[index];
                MainTab02.resetPicList();
            }
            dialog.dismiss();
        }
    }

    class NewsRadioOnClick implements DialogInterface.OnClickListener {
        private int index;

        public NewsRadioOnClick(int index) {
            this.index = index;
        }

        public void setIndex(int index) {
            this.index = index;
        }

        public int getIndex() {
            return index;
        }

        public void onClick(DialogInterface dialog, int whichButton) {
            setIndex(whichButton);
            Toast.makeText(MainActivity.this, "您选择了： " + PushApplication.newstypes[index], Toast.LENGTH_LONG).show();
            if (index == 0) {
                PushApplication.NEWS_DISPLAY_TYPE = null;
                MainTab03.resetNewsList();
            } else {
                // 设置图片类型
                PushApplication.NEWS_DISPLAY_TYPE = (long) PushApplication.newsids[index];
                MainTab03.resetNewsList();
            }
            dialog.dismiss();
        }
    }

    // 设置tab line的宽度为屏幕的三分之一
    private void initTabLine() {
        mTabLine = (ImageView) findViewById(R.id.id_tab_line);
        // 获取屏幕宽度 除以3 设置为tabline的宽度
        DisplayMetrics outMetrics = new DisplayMetrics();
        getWindow().getWindowManager().getDefaultDisplay().getMetrics(outMetrics);
        screenWidth = outMetrics.widthPixels;
        LinearLayout.LayoutParams lp = (android.widget.LinearLayout.LayoutParams) mTabLine.getLayoutParams();
        lp.width = screenWidth / 3;
        mTabLine.setLayoutParams(lp);
    }

    // 重设textview字体颜色为黑色
    protected void resetTextView() {
        mLiaotian.setTextColor(getResources().getColor(R.color.black));
        mFaxian.setTextColor(getResources().getColor(R.color.black));
        mTongxunlu.setTextColor(getResources().getColor(R.color.black));
    }

    // 初始化视图
    private void initView() {

        // 获取三个布局
        mTabLiaotian = (LinearLayout) findViewById(R.id.id_tab_liaotian_ly);
        mTabFaxian = (LinearLayout) findViewById(R.id.id_tab_faxian_ly);
        mTabTongxunlu = (LinearLayout) findViewById(R.id.id_tab_tongxunlu_ly);

        // 获取三个文本
        mLiaotian = (TextView) findViewById(R.id.id_liaotian);
        mFaxian = (TextView) findViewById(R.id.id_faxian);
        mTongxunlu = (TextView) findViewById(R.id.id_tongxunlu);

        // 设置图标
        setImage = (ImageView) findViewById(R.id.set);
        newsSet = (ImageView) findViewById(R.id.newsset);
        liaotianset = (ImageView) findViewById(R.id.liaotianset);

        // 新建三个碎片
        MainTab01 tab01 = new MainTab01();
        MainTab02 tab02 = new MainTab02();
        MainTab03 tab03 = new MainTab03();
        mFragments.add(tab01);
        mFragments.add(tab02);
        mFragments.add(tab03);

        // 初始化红点提示
        mBadgeViewforFaxian = new BadgeView(this);
        mBadgeViewforLiaotian = new BadgeView(this);
        mBadgeViewforTongxunlu = new BadgeView(this);
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK) && event.getRepeatCount() == 0) {
            dialog_Exit(MainActivity.this);
        }
        return false;
    }

    public void dialog_Exit(Context context) {
        AlertDialog.Builder builder = new Builder(context);
        builder.setMessage("确定要退出吗?");
        builder.setTitle("提示");
        builder.setIcon(android.R.drawable.ic_dialog_alert);
        builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                // 关闭连接
                if((PushApplication.YOUR_CHANNEL_ID != null) && (PushApplication.MY_CHANNEL_ID != PushApplication.YOUR_CHANNEL_ID)) {
                    closeConnection();
                }else{
                    android.os.Process.killProcess(android.os.Process.myPid());
                }
            }
        });

        builder.setNegativeButton("取消", new android.content.DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builder.create().show();
    }

    // 退出时关闭连接
    private void closeConnection() {
        RequestQueue requestQueue = Volley.newRequestQueue(MainActivity.this);
        String JSONDataUrl =
                ConnectServer.getCloseCon(PushApplication.APP_ID, PushApplication.USER_ID,
                        PushApplication.MY_CHANNEL_ID);
        final ProgressDialog progressDialog = ProgressDialog.show(MainActivity.this, "断开与有缘人联系...", "请稍等...", true, false);

        JsonObjectRequest jsonObjectRequest =
                new JsonObjectRequest(Request.Method.GET, JSONDataUrl, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // 处理返回的JSON数据
                        Message message = new Message(PushApplication.MY_CHANNEL_ID, PushApplication.YOUR_CHANNEL_ID);
                        message.setTitle(TitleEnum.CLOSE_CONNECTION.getStatus());
                        // 发送消息
                        PushApplication application = (PushApplication) getApplication();
                        SendMsgAsyncTask newTask =
                                new SendMsgAsyncTask(application.getGson().toJson(message),
                                        PushApplication.YOUR_CHANNEL_ID);
                        newTask.send();
                        progressDialog.dismiss();
                        PushApplication.YOUR_CHANNEL_ID = null;
                        PushApplication.buildConOrNot = false;
                        // 关闭应用
                        android.os.Process.killProcess(android.os.Process.myPid());
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError arg0) {
                        PushApplication.YOUR_CHANNEL_ID = null;
                        PushApplication.buildConOrNot = false;
                        // 关闭应用
                        android.os.Process.killProcess(android.os.Process.myPid());
                    }
                });
        requestQueue.add(jsonObjectRequest);
    }

}
