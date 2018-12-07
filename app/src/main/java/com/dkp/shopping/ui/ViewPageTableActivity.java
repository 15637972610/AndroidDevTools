package com.dkp.shopping.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;

import com.dkp.shopping.R;
import com.dkp.shopping.adapter.FgPagerAdapter;
import com.dkp.shopping.fragment.FirstFragment;
import com.dkp.shopping.fragment.SecondFragment;
import com.dkp.shopping.fragment.ThirdFragment;
import com.dkp.shopping.viewpager.ActionBarMainView;
import com.dkp.shopping.viewpager.NoScrollViewPager;
import com.dkp.shopping.viewpager.TabViewPager;
import com.elvishew.xlog.XLog;

import java.util.ArrayList;

import butterknife.BindView;

import static com.dkp.shopping.viewpager.ActionBarMainView.ACTIONBAR_MAIN_FIRST;
import static com.dkp.shopping.viewpager.ActionBarMainView.ACTIONBAR_MAIN_SECOND;
import static com.dkp.shopping.viewpager.ActionBarMainView.ACTIONBAR_MAIN_THIRD;

/**
 * Created by dkp on 2018/12/3.
 */

public class ViewPageTableActivity extends BaseActivity implements ViewPager.OnPageChangeListener{

    private static final String TAG = "ViewPageTableActivity";
    @BindView(R.id.actionBar_view)
    ActionBarMainView actionBarView;
    @BindView(R.id.viewpage)
    NoScrollViewPager viewPager;
    @BindView(R.id.tab_view)
    TabViewPager tabView;
    private ArrayList<Fragment> mFragmentArray = new ArrayList<Fragment>();
    FgPagerAdapter mFgPagerAdapter;
    private int m_nSelFragment = 0;
    public FirstFragment mFirstFragment;
    public SecondFragment mSsecondFragment;
    public ThirdFragment mThirdFragment;



    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
    }

    @Override
    protected int getStatusBarColor() {
        return 0;
    }

    @Override
    int getContentViewId() {
        return R.layout.activity_viewpager;
    }

    @Override
    void initData(Bundle savedInstanceState) {

    }

    @Override
    void initView() {
        tabView.setTabViewPager(actionBarView);
        mFirstFragment = new FirstFragment();
        mSsecondFragment =new SecondFragment();
        mThirdFragment = new ThirdFragment();
        mFragmentArray.add(mFirstFragment);
        mFragmentArray.add(mSsecondFragment);
        mFragmentArray.add(mThirdFragment);
        //控制三个界面切换不需要重载
        viewPager.setOffscreenPageLimit(3);

        mFgPagerAdapter = new FgPagerAdapter(getSupportFragmentManager(), mFragmentArray, 2);
        viewPager.setAdapter(mFgPagerAdapter);
        viewPager.addOnPageChangeListener(this);
        viewPager.setCurrentItem(0);
        viewPager.setCanScrollble(false);
        tabView.setViewPager(viewPager);

        tabView.setOnItemClickListner(new TabViewPager.iOnItemClickListener() {

            @Override
            public void onClicked(int nItem) {
                switch (nItem) {
                    case 0:
                        actionBarView.updateView(ACTIONBAR_MAIN_FIRST);
                        break;
                    case 1:
                        actionBarView.updateView(ACTIONBAR_MAIN_SECOND);
                        break;
                    case 2:
                        actionBarView.updateView(ACTIONBAR_MAIN_THIRD);
                        break;
                    case 3:
                        actionBarView.updateView(ACTIONBAR_MAIN_THIRD);
                        break;
                    default:
                        break;

                }
                setCurSelFragment(nItem);
            }
        });


    }


    @Override
//    @BehaviorTrace("滑动统计")
    public void onPageSelected(int position) {
        XLog.d(TAG," position="+position);
        tabView.setSelector(position);
        //控制按需加载的第三页，目前三页全部加载不需要这个
//        if (mFgPagerAdapter != null) {
//            mFgPagerAdapter.replaceFragment(mFragmentArray.get(position), position);
//        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    /**
     * 获取当前选中的页面
     */
    public int getCurSelFragment(){
        return m_nSelFragment;
    }

    private void setCurSelFragment(int nSel){
        m_nSelFragment = nSel;
    }

    @Override
    protected void onResume() {
        XLog.d(TAG,"onResume");
        super.onResume();
    }

    @Override
    protected void onPause() {
        XLog.d(TAG,"onPause");
        super.onPause();
    }

    @Override
    protected void onStop() {
        XLog.d(TAG,"onStop");
        super.onStop();
    }

}
