package com.cn.mv;

import android.app.Application;

import com.cn.mv.util.ToastUtil;

/**
 * Created by Administrator on 2016/7/5.
 */
public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        ToastUtil.init(this);
    }
}
