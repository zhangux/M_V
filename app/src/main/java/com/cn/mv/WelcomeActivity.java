package com.cn.mv;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.widget.TextView;
import android.widget.Toast;

import com.cn.mv.util.SharedPreferencesUtils;

/**
 * Created by Administrator on 2016/7/5.
 */
public class WelcomeActivity extends AppCompatActivity {
    private Handler handler = new Handler();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.action_welcome);
        if (Environment.getExternalStorageDirectory().equals(Environment.MEDIA_REMOVED)) {
            Toast.makeText(this, "没有SD卡！", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
        Intent in = new Intent(WelcomeActivity.this, MainActivity.class);
        startActivity(in);
        finish();
            }
        }, 500);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
