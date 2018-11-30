package com.dkp.shopping.utils;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;

import com.dkp.shopping.R;
import com.nineoldandroids.view.ViewHelper;


/**
 * Created by dkp on 2018/11/30.
 */

public class SlidingMenu extends HorizontalScrollView {

    /**
     * 手指滑动的距离，用于判断是否让菜单回弹
     */
    private static final int CHANGE_X = 50;
    /**
     * 屏幕宽度
     */
    private  int mScreenWidth;

    /**
     * dp 菜单距右侧屏幕尺寸
     */
    private int mMenuRightPadding = 20;

    private boolean once = false;
    /**
     * 菜单是否是打开状态
     */
    private boolean isOpen = false;
    /**
     * 菜单的宽度
     */
    private int mMenuWidth;
    /**
     * 菜单的一半宽度
     */
    private int mHalfMenuWidth;
    /**
     * 菜单布局
     */
    ViewGroup menu;

    /**
     * 内容布局
     */
    ViewGroup content;

    public SlidingMenu(Context context, AttributeSet attrs) {
        super(context, attrs);
        mScreenWidth = ScreenUtils.getScreenWidth(context);
    }

    public SlidingMenu(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
//        mScreenWidth = ScreenUtils.getScreenWidth(context);
//        TypedArray a =context.getTheme().obtainStyledAttributes(attrs, R.styleable.SlidingMenu,defStyleAttr,0);
//        int n = a.getIndexCount();
//        for (int i = 0; i < n; i++)
//        {
//            int attr = a.getIndex(i);
//            switch (attr)
//            {
//                case R.styleable.SlidingMenu_rightPadding:
//                    // 默认50
//                    mMenuRightPadding = a.getDimensionPixelSize(attr,
//                            (int) TypedValue.applyDimension(
//                                    TypedValue.COMPLEX_UNIT_DIP, 10f,
//                                    getResources().getDisplayMetrics()));// 默认为10DP
//                    break;
//            }
//        }
//        a.recycle();

    }

    public SlidingMenu(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }
    //测量设置菜单，内容布局的宽度
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (!once){
            LinearLayout wrapper = (LinearLayout) getChildAt(0);
            //获取菜单根布局
            menu = (ViewGroup) wrapper.getChildAt(0);
            //获取内容根布局
            content = (ViewGroup) wrapper.getChildAt(1);
            //菜单居右侧的距离转换成dp
            mMenuRightPadding = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,mMenuRightPadding,content.getResources().getDisplayMetrics());
            //菜单宽度
            mMenuWidth = mScreenWidth - mMenuRightPadding;
            //计算一半的菜单宽度
            mHalfMenuWidth = mMenuWidth / 2 ;
            //给菜单宽度赋值
            menu.getLayoutParams().width = mMenuWidth;
            //给内容宽度赋值
            content.getLayoutParams().width = mScreenWidth;

        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        if (changed){
            this.scrollTo(mMenuWidth,0);
            once =true;

        }
    }
    int startX=0;
    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        int action =ev.getAction();
        int endX;
        int changedX;
        int dtX;
        switch (action){
            case MotionEvent.ACTION_DOWN:
                 startX =getScrollX();
                 break;

            // Up时，进行判断，如果显示区域大于菜单宽度一半则完全显示，否则隐藏
            case MotionEvent.ACTION_UP:
                endX = getScrollX();
                //手指滑动的距离
                changedX = startX - endX;
                //手指滑动距离的绝对值
                dtX = Math.abs(changedX);

                if (dtX>CHANGE_X){
                    if (changedX>0) {
                        this.smoothScrollTo(0, 0);//展开
                        isOpen = true;
                    }else {
                        this.smoothScrollTo(mMenuWidth, 0);//收起
                        isOpen = false;
                    }

                }else{//如果滑动距离太小，之前是什么状态就保持之前的状态
                    if (isOpen){
                        this.smoothScrollTo(0, 0);//展开
                    }else{
                        this.smoothScrollTo(mMenuWidth, 0);//收起
                    }
                }

                return true;

        }

        return super.onTouchEvent(ev);
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        //内容区域的缩放比例
        float scale = l*1.0f / mMenuWidth;//计算菜单1.0-0.0变化率，假设是1.0-0.8
        float rightScale = 0.8f+scale*0.2f;
        //菜单区域的缩放比例，假设是从0.7-1
        float leftScale = 1-0.3f*scale;
        //菜单透明度，假设是0.6-1.0
        float translate = 0.6f + 0.4f * (1 - scale);
        //X方向的偏移量
        float tranlateX = mMenuWidth * scale * 0.6f;


        ViewHelper.setScaleX(menu, leftScale);
        ViewHelper.setScaleY(menu, leftScale);
        ViewHelper.setAlpha(menu, translate);
        ViewHelper.setTranslationX(menu, tranlateX);

        ViewHelper.setPivotX(content, 0);
        ViewHelper.setPivotY(content, content.getHeight() / 2);
        ViewHelper.setScaleX(content, rightScale);
        ViewHelper.setScaleY(content, rightScale);

    }

    /**
     * 打开菜单
     */
    public void openMenu()
    {
        if (isOpen)
            return;
        this.smoothScrollTo(0, 0);
        isOpen = true;
    }

    /**
     * 关闭菜单
     */
    public void closeMenu()
    {
        if (isOpen)
        {
            this.smoothScrollTo(mMenuWidth, 0);
            isOpen = false;
        }
    }

    /**
     * 切换菜单状态
     */
    public void toggle()
    {
        if (isOpen)
        {
            closeMenu();
        } else
        {
            openMenu();
        }
    }


}
