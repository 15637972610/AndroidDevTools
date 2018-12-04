package com.dkp.shopping.viewpager;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dkp.shopping.R;

import java.util.ArrayList;

import static android.support.constraint.Constraints.TAG;

/**
 * 文件名 ：TabViewPager.java
 * 描 述 ：继承自LinearLayout的自带Title的自定义ViewPager
 * 作 者 ：Administrator
 * 时 间 ：2017年8月8日
 */
public class TabViewPager extends LinearLayout implements View.OnClickListener {
    public static final String TAG = "TabViewPager";
    private LayoutInflater mInflater;

    private ArrayList<TextView> mMenuTextViews;
    private ArrayList<ImageView> mMenuImages;
    private LinearLayout mLinearLayout;

    private View iconNewMessage;
    private ActionBarMainView mBarView;
    private ViewPager viewPager;

    private iOnItemClickListener mOnItemClickListner;

    public void setOnItemClickListner(iOnItemClickListener itemClickListner) {
        this.mOnItemClickListner = itemClickListner;
    }

    /**
     * 方法名 ：TabViewPager 功 能 ：本类的构造方法 参 数 ：context对象、属性集 返回值 ：无
     */
    public TabViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    /**
     * 方法名 ：TabViewPager 功 能 ：本类的构造方法 参 数 ：context对象 返回值 ：无
     */
    public TabViewPager(Context context) {
        super(context, null);
        init();
    }

    /**
     * 方法名 ：init 功 能 ：本类的初始化方法 参 数 ：无 返回值 ：无
     */
    @SuppressWarnings("deprecation")
    private void init() {
        mInflater = LayoutInflater.from(getContext());
        initView();
    }

    /**
     * 初始化View
     */
    private void initView() {
        super.onFinishInflate();
        // 获取各个View
        View mTabView = mInflater.inflate(R.layout.tab_view_theme, this, false);
        mLinearLayout = mTabView.findViewById(R.id.ll_main);
        Log.e(TAG,"mLinearLayout.size="+mLinearLayout.getChildCount());
        iconNewMessage = mTabView.findViewById(R.id.icon_newMessage);
        addView(mTabView);
        initTitle();
        setSelector(0);
    }


    /**
     * 方法名 ：initTitle 功 能 ：设置本View的Title 参 数 ：title数组 返回值 ：无
     */
    private void initTitle() {
        mMenuTextViews = new ArrayList<>();
        mMenuImages = new ArrayList<>();
        // 动态添加title控件
        for (int i = 0; i < mLinearLayout.getChildCount(); i++) {
            TextView textView = null;
            ImageView imageView = null;
            View titleView = mLinearLayout.getChildAt(i);
            if (titleView instanceof TextView) {
                textView = (TextView) titleView;
            } else if (titleView instanceof RelativeLayout) {
                textView = (TextView) ((RelativeLayout) titleView).getChildAt(1);
                imageView = (ImageView) ((RelativeLayout) titleView).getChildAt(0);
            }
            Log.e(TAG,"id="+i);
            titleView.setId(i);
            titleView.setOnClickListener(this);
            mMenuTextViews.add(textView);
            mMenuImages.add(imageView);
        }
    }

    /**
     * 处理title的点击事件 参 数 ：点中的对应title控件 返回值 ：无
     */
    @Override
    public void onClick(View v) {
        int id = v.getId();
        setSelector(id);
        if (viewPager != null) {
            viewPager.setCurrentItem(id);
        }

        if (null != mOnItemClickListner){
            mOnItemClickListner.onClicked(id);
        }
    }

    /**
     * 方法名 ：setSelector 功 能 ：设置title的选中效果 参 数 ：对应title控件的id 返回值 ：无
     */
    @SuppressLint("ResourceAsColor")
    public void setSelector(int id) {
        if (mBarView != null) {
            mBarView.updateView(id);
        }
        for (int i = 0; i < mMenuTextViews.size(); i++) {
            if (id == i) {
                // 设置选中的title视觉效果
                mMenuTextViews.get(i).setSelected(true);
                mMenuImages.get(i).setSelected(true);
            } else {
                // 设置未选中的title视觉效果
                mMenuTextViews.get(i).setSelected(false);
                mMenuImages.get(i).setSelected(false);
            }
            // 消息页面，隐藏新消息提醒红点
            if (id == 1) {
                hideNewMessageTip();
            }
        }
    }

    public void setTabViewPager(ActionBarMainView view) {
        this.mBarView = view;
    }

    public void setViewPager(ViewPager viewPager) {
        this.viewPager = viewPager;
    }

    public void hideNewMessageTip() {
        if (this.iconNewMessage != null) {
            this.iconNewMessage.setVisibility(View.INVISIBLE);
        }
    }

    public void showNewMessageTip() {
        if (this.iconNewMessage != null) {
            this.iconNewMessage.setVisibility(View.VISIBLE);
        }
    }

    public interface iOnItemClickListener{
        public void onClicked(int nItem);
    }
}
