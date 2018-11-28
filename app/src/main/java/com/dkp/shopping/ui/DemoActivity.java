package com.dkp.shopping.ui;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.dkp.shopping.R;

public class DemoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

   void anniu(View view){
        showDialog();
   }

    private void showDialog() {
        final AlertDialog.Builder normalDialog =
                new AlertDialog.Builder(DemoActivity.this);
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
}
