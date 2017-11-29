package com.cn.mv.util;

import android.content.Context;
import android.support.annotation.StringRes;
import android.widget.Toast;

/**
 * Created by Administrator on 2016/7/5.
 */
public class ToastUtil {
    private static Context mContext;

    /***
     * 初始化Context
     *
     * @param context
     */
    public static void init(Context context) {
        mContext = context;
    }

    public static void show(String text) {
        Toast.makeText(mContext, text, Toast.LENGTH_SHORT).show();
    }

    public static void show(@StringRes int resId) {
        Toast.makeText(mContext, resId, Toast.LENGTH_SHORT).show();
    }
}
