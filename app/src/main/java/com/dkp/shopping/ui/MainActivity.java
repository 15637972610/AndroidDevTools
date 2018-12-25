package com.dkp.shopping.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.dkp.shopping.R;
import com.dkp.shopping.utils.SharePreferenceUtils;
import com.dkp.shopping.utils.ToastUitl;
import com.elvishew.xlog.Logger;
import com.elvishew.xlog.XLog;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    /**
     * 切换去侧滑菜单的按钮
     */
    @BindView(R.id.sliding_btn)
    Button mSlingBtn;

    /**
     * 切换去table页的按钮
     */
    @BindView(R.id.viewpager_btn)
    Button mViewpagerBtn;

    /**
     * 切换去table页的按钮
     */
    @BindView(R.id.to_LogActivity_btn)
    Button mToSPpage_btn;

    /**
     * 切换去table页的按钮
     */
    @BindView(R.id.to_sppage_btn)
    Button mToLogActivity_btn;

    Logger mLog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        XLog.d("onCreate");
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        SharePreferenceUtils.setDBParam(MainActivity.this,"sptest","数据存储测试成功");
    }

    int i = 0;

    @OnClick({R.id.sliding_btn, R.id.viewpager_btn, R.id.to_LogActivity_btn,R.id.to_sppage_btn})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.sliding_btn:
                Intent sliding_menu = new Intent(MainActivity.this, SlidingMenuActivity.class);
                startActivity(sliding_menu);
                break;
            case R.id.viewpager_btn:
                Intent viewpagertable = new Intent(MainActivity.this, ViewPageTableActivity.class);
                startActivity(viewpagertable);
                break;

            case R.id.to_LogActivity_btn:
                Intent logIntent = new Intent(MainActivity.this,LogTestActivity.class);
                startActivity(logIntent);
            case R.id.to_sppage_btn:
                String ss = (String) SharePreferenceUtils.getDBParam(MainActivity.this,"sptest","测试失败");
                ToastUitl.show(ss);

            default:
                break;
        }
    }


    @Override
    protected void onResume() {
        XLog.d(TAG, "onResume");
        super.onResume();
    }

    @Override
    protected void onPause() {
        XLog.d(TAG, "onPause");
        super.onPause();
    }

    @Override
    protected void onStop() {
        XLog.d(TAG, "onStop");
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        XLog.d(TAG, "onDestroy");
        super.onDestroy();
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        XLog.d(TAG, "onSaveInstanceState");
        super.onSaveInstanceState(outState, outPersistentState);
    }
}
