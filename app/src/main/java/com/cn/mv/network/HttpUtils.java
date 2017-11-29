package com.cn.mv.network;

import java.util.Map;
import java.util.Set;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

/**
 * Created by xjm on 2016/7/28.
 */
public class HttpUtils {
    private static OkHttpClient client = new OkHttpClient();

    public static Call get(String url, Callback callback) {
        return post(url, null, callback);
    }

    public static Call post(String url, Map<String, String> map, Callback callback) {
        //将map封装成RequestBody
        RequestBody body = getFormBody(map);
        //创建请求构建器
        Request.Builder builder = new Request.Builder()
                .url(url);
        //如果存在body则添加post
        if (body != null)
            builder.post(body);
        //创建请求
        Call call = client.newCall(builder.build());
        //执行请求
        call.enqueue(callback);
        return call;
    }

    private static FormBody getFormBody(Map<String, String> map) {

        if (map != null && !map.isEmpty()) {
            FormBody.Builder builder = new FormBody.Builder();
            Set<Map.Entry<String, String>> set = map.entrySet();
            for (Map.Entry<String, String> entry : set) {
                builder.add(entry.getKey(), entry.getValue());
            }
            return builder.build();
        }
        return null;
    }
}
