package com.cn.mv.constants;

/**
 * Created by Administrator on 2016/8/16.
 */
public class Constants {
    public static final int ITEM_CHECKED_CHANGED = 120;

    public static class Url {
        public static final String URL = "http://localhost:8080/Work/";
        public static final String VIDEOS = "videos";

        public static String getUrl(String url) {
            return URL + url;
        }

    }
}
