package com.dkp.shopping.adapter;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.Log;
import android.view.ViewGroup;

import java.util.ArrayList;


/**
 * 主界面的放Fragment的PagerAdapter
 */
public class FgPagerAdapter extends FragmentStatePagerAdapter {

    private static String TAG = "FragmentPagerAdapter";

    private static final int FragmentCount = 3;

    private Handler mHandler = new Handler();
    private ArrayList<Fragment> fragmentArray = new ArrayList<Fragment>();
    private boolean[] mShouldLoadedFlag = new boolean[FragmentCount];
   /**
    * 默认显示第几个fragment
    * position 默认显示
    **/
    public FgPagerAdapter(FragmentManager fm, ArrayList<Fragment> fragmentList, int position) {
        this(fm);
        for (int i = 0; i < FragmentCount; i++) {
            if (i <= position) {
                fragmentArray.add(fragmentList.get(i));
                mShouldLoadedFlag[position] = true;
            } else {
                fragmentArray.add(new LoadingFragment());
            }
        }
        // 为提高第一次加载速度，其他两页先用空白页，在页面展现后再替换
        // 在onWindowFocusChanged替换 此时只是刚显示数据还未加载，考虑等数据加载完在加载另外两页
//        if (position < FragmentCount) {
//            mShouldLoadedFlag[position] = true;
//        }
    }

    public FgPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    // 只是临时用来填充页的，不需要显示
    @SuppressLint("ValidFragment")
    public static class LoadingFragment extends Fragment {
        public LoadingFragment() {
        }
    }

    @Override
    public int getItemPosition(Object object) {
        if (object instanceof LoadingFragment) {
            Log.d(TAG, "getItemPosition POSITION_NONE:" + object.toString());
            return POSITION_NONE;
        } else {
            Log.d(TAG, "getItemPosition POSITION_UNCHANGEED:" + object.toString());
            return POSITION_UNCHANGED;
        }
    }

    @Override
    public Fragment getItem(int arg0) {
        Fragment fragment = fragmentArray.get(arg0);
        Log.d(TAG, "getItem POSITION:" + Integer.valueOf(arg0) + "  " + fragment.toString());
        return fragment;
    }

    @Override
    public int getCount() {
        return this.fragmentArray.size();
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        try {
            Log.d(TAG, "destoryItem POSITION:" + Integer.toString(position) + " " + object.toString());
            // container.removeView(((Fragment)object).getView());
            super.destroyItem(container, position, object);
        } catch (Exception e) {
            Log.d(TAG, "destroyItem()异常：" + e);
            e.printStackTrace();
        }
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        return super.instantiateItem(container, position);
    }

    public void replaceFragment(final Fragment fragment, final int postion) {
        if (postion < FragmentCount) {
            if (!mShouldLoadedFlag[postion]) {
                mShouldLoadedFlag[postion] = true;
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            fragmentArray.set(postion, fragment);
                            notifyDataSetChanged();
                        } catch (Exception e) {
                            Log.d(TAG, "replaceFragment---run()异常：" + e);
                            e.printStackTrace();
                        }
                    }
                }, 200);
            }
        }
    }
}

