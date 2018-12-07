package com.dkp.shopping.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.dkp.shopping.R;
import com.dkp.shopping.utils.XLogUtils;
import com.elvishew.xlog.Logger;
import com.elvishew.xlog.XLog;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class LogTestActivity extends AppCompatActivity {
    private static final String TAG = "LogTestActivity";

    /**
     * 崩溃日志收集测试按钮
     */
    @BindView(R.id.crash_btn)
    Button mCrashBtn;

    /**
     * 切换去table页的按钮
     */
    @BindView(R.id.createLogFile_btn)
    Button mCreateLogFileBtn;

    Logger mLog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        XLog.d("onCreate");
        setContentView(R.layout.activity_log_test);
        ButterKnife.bind(this);
        /**
         * 将登录相关日志存储到登录日志文件中
         */
        mLog = XLogUtils.createLoggerWithTag(TAG);
    }

    int i = 0;

    @OnClick({R.id.crash_btn, R.id.createLogFile_btn})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.crash_btn:
                //手动捕获崩溃日志测试代码，空指针
                String empty = null;
                int n = empty.length();
                break;
            case R.id.createLogFile_btn:
                mLog.d("i=" + i);
                i++;
                break;
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
