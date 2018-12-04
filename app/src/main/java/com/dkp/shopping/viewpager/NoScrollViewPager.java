package com.dkp.shopping.viewpager;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * 可设置滑动与否的ViewPager，默认可滑动
 * @author Administrator
 *
 */
public class NoScrollViewPager extends ViewPager {

	private boolean mScrollble = true;

	public NoScrollViewPager(Context context) {
		super(context);
	}

	public NoScrollViewPager(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		if (!mScrollble) {
			return false;
		}
		try {
			return super.onTouchEvent(ev);
		} catch (Exception e) {
			return false;
		}
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		if (!mScrollble) {
			return false;
		}
		try {
			return super.onInterceptTouchEvent(ev);
		} catch (Exception e) {
			return false;
		}
	}

	public boolean isCanScrollble() {
		return mScrollble;
	}

	/**
	 * 设置能否滑动
	 * @param scrollble
	 */
	public void setCanScrollble(boolean scrollble) {
		this.mScrollble = scrollble;
	}
}