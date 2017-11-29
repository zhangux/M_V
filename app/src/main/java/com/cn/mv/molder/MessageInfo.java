package com.cn.mv.molder;

/**
 * Created by Administrator on 2016/7/4.
 */
public class MessageInfo {
    public int what;
    public Object obj;
    public String str1;
    public String str2;

    public MessageInfo(int what) {
        this.what = what;
    }

    public MessageInfo(int what, Object obj) {
        this.what = what;
        this.obj = obj;
    }

    public MessageInfo(int what, Object obj, String str1, String str2) {
        this.what = what;
        this.obj = obj;
        this.str1 = str1;
        this.str2 = str2;
    }
}
