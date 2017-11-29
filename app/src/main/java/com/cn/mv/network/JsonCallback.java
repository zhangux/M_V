package com.cn.mv.network;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.cn.mv.R;
import com.cn.mv.util.ToastUtil;

import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Created by xjm on 2016/7/28.
 */
public abstract class JsonCallback<T> implements Callback {
    private Type type;
    private final int SUCCESS = 0;
    private final int FAILURE = 1;

    private Handler handler = new Handler(Looper.getMainLooper());

    public JsonCallback() {
        //通过反射获取泛型的类型
        Type tmp = getClass().getGenericSuperclass();
        if (!(tmp instanceof ParameterizedType)) {
            throw new ExceptionInInitializerError("can't get type");
        }
        type = ((ParameterizedType) tmp).getActualTypeArguments()[0];
    }

    @Override
    public void onFailure(Call call, IOException e) {
        Log.e("aaaa","network connect error===========", e);
        handler.post(new MsgRunnable(FAILURE, call, 0));
    }

    @Override
    public void onResponse(Call call, Response response) throws IOException {
        int code = response.code();
        try {
            Log.e("aaaa","===========response code:" + code);
            if (code == 200) {
                String json = response.body().string();
                Log.e("aaaa","=========json:" + json);
                T t = JSON.parseObject(json, type);
                handler.post(new MsgRunnable(SUCCESS, call, t));
            } else {
                handler.post(new MsgRunnable(FAILURE, call, code));
            }
        } catch (IOException e) {
            Log.e("aaaa",e.getMessage(), e);
            handler.post(new MsgRunnable(FAILURE, call, code));
        }
    }

    public void onFailure(Call call, int code) {
        if (code == 0) {
            ToastUtil.show(R.string.network_address_error);
        } else if (code == 404) {
            ToastUtil.show(R.string.network_address_error);
        } else if (code == 500) {
            ToastUtil.show(R.string.server_error);
        } else {
            ToastUtil.show(R.string.network_connect_error);
        }
    }


    public abstract void onSuccess(Call call, T data);

    class MsgRunnable implements Runnable {
        private int what;
        private Call call;
        private T data;
        private int code;

        public MsgRunnable(int what, Call call, T data) {
            this.what = what;
            this.call = call;
            this.data = data;
        }

        public MsgRunnable(int what, Call call, int code) {
            this.what = what;
            this.call = call;
            this.code = code;
        }

        @Override
        public void run() {
            switch (what) {
                case SUCCESS:
                    onSuccess(call, data);
                    break;
                case FAILURE:
                    onFailure(call, code);
                    break;
            }
        }
    }
}
