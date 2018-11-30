package com.dkp.shopping.ui;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.dkp.shopping.R;

import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {
//    @BindView(R.id.title) TextView name;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("dkp====","onCreate");
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
//        findViewById(R.id.anniu).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent mIntent =new Intent(MainActivity.this,DemoActivity.class);
//                startActivity(mIntent);
//            }
//        });
    }


    private void showDialog() {
        final AlertDialog.Builder normalDialog =
                new AlertDialog.Builder(MainActivity.this);
        normalDialog.setIcon(R.drawable.ic_launcher_background);
        normalDialog.setTitle("我是一个普通Dialog");
        normalDialog.setMessage("你要点击哪一个按钮呢?");
        normalDialog.setPositiveButton("确定",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //...To-do
                    }
                });
        normalDialog.setNegativeButton("关闭",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //...To-do
                    }
                });
        // 显示
        normalDialog.show();

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
