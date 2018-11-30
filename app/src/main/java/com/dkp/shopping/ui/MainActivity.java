package com.dkp.shopping.ui;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.dkp.shopping.R;
import com.dkp.shopping.utils.SlidingMenu;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {
    @BindView(R.id.menu)
    SlidingMenu menu;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("dkp====","onCreate");
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
    }


    @Override
    protected void onResume() {
        Log.d("dkp====","onResume");
        super.onResume();
    }

    @Override
    protected void onPause() {
        Log.d("dkp====","onPause");
        super.onPause();
    }

    @Override
    protected void onStop() {
        Log.d("dkp====","onStop");
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        Log.d("dkp====","onDestroy");
        super.onDestroy();
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        Log.d("dkp====","onSaveInstanceState");
       super.onSaveInstanceState(outState, outPersistentState);
    }
}
