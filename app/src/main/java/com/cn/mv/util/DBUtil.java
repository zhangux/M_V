package com.cn.mv.util;

import android.os.Environment;

import com.activeandroid.query.Select;
import com.cn.mv.molder.FileInfo;

import java.io.File;
import java.util.List;

/**
 * Created by Administrator on 2016/7/5.
 */
public class DBUtil {
    public static List<File> fileList;

    public static void initRoot() {
        FileInfo q = new Select().from(FileInfo.class).executeSingle();
        if (q == null) {
            FileInfo fileInfo = new FileInfo();
            if (fileList != null) {
                fileInfo.setFileList(fileList);
            } else {
                File files = new File(Environment.getExternalStorageDirectory().getPath());
                fileInfo.setFiles(files.listFiles());
            }
            fileInfo.save();
        }
    }

    public static List queryAllMode(Class clz) {
        return new Select().from(clz).execute();
    }

}
