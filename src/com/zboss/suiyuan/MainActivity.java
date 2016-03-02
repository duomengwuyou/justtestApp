package com.zboss.suiyuan;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.baidu.android.pushservice.PushConstants;
import com.baidu.android.pushservice.PushManager;
import com.jauker.widget.BadgeView;

public class MainActivity extends FragmentActivity {
    private ViewPager mViewPager;
    private FragmentPagerAdapter mAdapter;
    private List<Fragment> mFragments = new ArrayList<Fragment>();

    public static LinearLayout mTabLiaotian;
    private LinearLayout mTabFaxian;
    private LinearLayout mTabTongxunlu;

    private TextView mLiaotian;
    private TextView mFaxian;
    private TextView mTongxunlu;

    public static BadgeView mBadgeViewforLiaotian;
    private BadgeView mBadgeViewforFaxian;
    private BadgeView mBadgeViewforTongxunlu;

    private ImageView mTabLine;

    public static int currentIndex;
    private int screenWidth;

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

        // 页面改变的时候触发事件
        mViewPager.setOnPageChangeListener(new OnPageChangeListener() {

            @Override
            public void onPageSelected(int position) {
                resetTextView();
                switch (position) {
                    case 0:
                        mTabLiaotian.removeView(mBadgeViewforLiaotian);
                        mBadgeViewforLiaotian.setBadgeCount(0);
                        mLiaotian.setTextColor(getResources().getColor(R.color.green));
                        break;
                    case 1:
                        mFaxian.setTextColor(getResources().getColor(R.color.green));

                        // mTabFaxian.removeView(mBadgeViewforFaxian);
                        // mBadgeViewforFaxian.setBadgeCount(15);
                        // mTabFaxian.addView(mBadgeViewforFaxian);
                        break;
                    case 2:
                        mTongxunlu.setTextColor(getResources().getColor(R.color.green));

                        break;
                }
                currentIndex = position;
            }

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                if (currentIndex == 0 && position == 0)// 0->1
                {
                    LinearLayout.LayoutParams lp =
                            (android.widget.LinearLayout.LayoutParams) mTabLine.getLayoutParams();
                    lp.leftMargin = (int) (positionOffset * (screenWidth * 1.0 / 3) + currentIndex * (screenWidth / 3));
                    mTabLine.setLayoutParams(lp);
                } else if (currentIndex == 1 && position == 0) // 1->0
                {
                    LinearLayout.LayoutParams lp =
                            (android.widget.LinearLayout.LayoutParams) mTabLine.getLayoutParams();
                    lp.leftMargin =
                            (int) (-(1 - positionOffset) * (screenWidth * 1.0 / 3) + currentIndex * (screenWidth / 3));
                    mTabLine.setLayoutParams(lp);

                } else if (currentIndex == 1 && position == 1) // 1->2
                {
                    LinearLayout.LayoutParams lp =
                            (android.widget.LinearLayout.LayoutParams) mTabLine.getLayoutParams();
                    lp.leftMargin = (int) (positionOffset * (screenWidth * 1.0 / 3) + currentIndex * (screenWidth / 3));
                    mTabLine.setLayoutParams(lp);
                } else if (currentIndex == 2 && position == 1) // 2->1
                {
                    LinearLayout.LayoutParams lp =
                            (android.widget.LinearLayout.LayoutParams) mTabLine.getLayoutParams();
                    lp.leftMargin =
                            (int) (-(1 - positionOffset) * (screenWidth * 1.0 / 3) + currentIndex * (screenWidth / 3));
                    mTabLine.setLayoutParams(lp);

                }
                Log.e("xxx", positionOffset + " 0000 " + positionOffsetPixels + " ---- " + position);

            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

        mTabLiaotian.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mViewPager.setCurrentItem(0);
                return;
            }
        });
        
        mFaxian.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mViewPager.setCurrentItem(1);
                return;
            }
        });
        
        mTongxunlu.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mViewPager.setCurrentItem(2);
                return;
            }
        });
        
        // 默认选择第二个标签
        mViewPager.setCurrentItem(1);
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
}
