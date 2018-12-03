package com.dkp.shopping.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.dkp.shopping.R;
import com.dkp.shopping.utils.SlidingMenu;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SlidingMenuActivity extends AppCompatActivity {

    @BindView(R.id.menu)
    SlidingMenu menu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_slidingmenu);
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
}
