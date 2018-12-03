package com.dkp.shopping.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.dkp.shopping.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {
    @BindView(R.id.sliding_btn)
    Button mSlingBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("dkp====","onCreate");
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
    }


    @OnClick(R.id.sliding_btn)
    public void onViewClicked(View view){
        switch (view.getId()){
            case R.id.sliding_btn:
                Intent sliding_menu = new Intent(MainActivity.this,SlidingMenuActivity.class);
                startActivity(sliding_menu);
                break;
            default:
                    break;
        }
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
