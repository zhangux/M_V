package com.cn.mv.util;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

/**
 * Created by Administrator on 2016/7/4.
 */
public class FileUtil {
    private static final long KB = 1024;
    private static final long MB = KB * 1024;
    private static final long GB = MB * 1024;

    /***
     * 将字节文件大小转化为G、M、KB
     *
     * @param size 文件字节尺寸
     * @return 如：4GB，5MB
     */
    public static String convertToString(long size) {
        String str = null;
        if (size >= GB) {
            str = String.format("%.2fGB", (float) size / GB);
        } else if (size >= MB) {
            str = String.format("%.2fMB", (float) size / MB);
        } else if (size >= KB) {
            str = String.format("%.2fKB", (float) size / KB);
        } else {
            str = String.format("%dB", size);
        }
        return str;
    }

    public static List<File> getChildrenFiles(final List<File> tmp, int sortType) {
        if (tmp != null && tmp.size() > 0) {
            switch (sortType) {
                case 0:
                    Collections.sort(tmp, fileNameComparator);
                    break;
                case 1:
                    Collections.sort(tmp, fileBigDateComparator);
                    break;
                case 2:
                    Collections.sort(tmp, fileDateComparator);
                    break;
                case 3:
                    Collections.sort(tmp, fileBigSizeComparator);
                    break;
                case 4:
                    Collections.sort(tmp, fileSizeComparator);
                    break;
            }
            return tmp;
        }
        return null;
    }

    /***
     * 按文件名比较
     */
    private static Comparator<File> fileNameComparator = new Comparator<File>() {
        @Override
        public int compare(File file, File t1) {
            return file.getName().compareToIgnoreCase(t1.getName());
        }
    };

    /***
     * 按文件修改日期比较
     */
    private static Comparator<File> fileDateComparator = new Comparator<File>() {
        @Override
        public int compare(File file, File t1) {
            long c = file.lastModified() - t1.lastModified();
            if (c / 1000 > Integer.MAX_VALUE) {
                return Integer.MAX_VALUE;
            } else if (c / 1000 < Integer.MIN_VALUE) {
                return Integer.MIN_VALUE;
            }
            return (int) (c / 1000);
        }
    };
    private static Comparator<File> fileBigDateComparator = new Comparator<File>() {
        @Override
        public int compare(File file, File t1) {
            long c = t1.lastModified() - file.lastModified();
            Log.e("aaaaa","=="+file.getName()+"=="+t1.getName());
            if (c / 1000 > Integer.MAX_VALUE) {
                return Integer.MAX_VALUE;
            } else if (c / 1000 < Integer.MIN_VALUE) {
                return Integer.MIN_VALUE;
            }
            return (int) (c / 1000);
        }
    };
    private static Comparator<File> fileBigSizeComparator = new Comparator<File>() {
        @Override
        public int compare(File file, File t1) {
            if (file.isDirectory() && t1.isDirectory()) {
                //都是文件夹 按名称排列
                return t1.getName().compareToIgnoreCase(file.getName());
            } else if (file.isDirectory() && t1.isFile()) {
                return Integer.MAX_VALUE;
            } else if (file.isFile() && t1.isDirectory()) {
                return Integer.MIN_VALUE;
            } else {
                long size = t1.length() - file.length();
                if (size > Integer.MAX_VALUE) {
                    return Integer.MAX_VALUE;
                } else if (size < Integer.MIN_VALUE) {
                    return Integer.MIN_VALUE;
                } else {
                    return (int) size;
                }
            }
        }
    };

    private static Comparator<File> fileSizeComparator = new Comparator<File>() {
        @Override
        public int compare(File file, File t1) {
            if (file.isDirectory() && t1.isDirectory()) {
                //都是文件夹 按名称排列
                return file.getName().compareToIgnoreCase(t1.getName());
            } else if (file.isDirectory() && t1.isFile()) {
                return Integer.MAX_VALUE;
            } else if (file.isFile() && t1.isDirectory()) {
                return Integer.MIN_VALUE;
            } else {
                long size = file.length() - t1.length();
                if (size > Integer.MAX_VALUE) {
                    return Integer.MAX_VALUE;
                } else if (size < Integer.MIN_VALUE) {
                    return Integer.MIN_VALUE;
                } else {
                    return (int) size;
                }
            }
        }
    };

    /***
     * 文件重命名
     *
     * @param name
     * @param file
     */
    public static int rename(String name, File file) {
        try {
            if (name.matches("^.*(/|\\|:|\\*|\\?|\"|>|<|\\|)+.*$")) {
                return -1;
            }
            File dest = new File(file.getParent(), name);
            if (dest.exists()) {
                return 0;
            }
            if (file.isFile()) {
                dest.createNewFile();
            } else {
                dest.mkdir();
            }

            file.renameTo(dest);
            return 1;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public static String getMINType(String name) {
        String miniType = "unknown";
        if (name.endsWith(".mp4")) {
            miniType = "video/mp4";
        }
        return miniType;
    }
    private static StringBuffer sb = new StringBuffer();
    private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd hh:mm:ss");
    public static String getInfo(File file) {
        //清楚原本内容
        sb.setLength(0);
        //将文件的最后修改日期创建成对象
        Date data = new Date(file.lastModified());
        sb.append(sdf.format(data));
        return sb.toString();
    }

    public static void delete(File delFile) {
        if (delFile.exists()) {
            if (delFile.isFile()) {
                delFile.delete();
            }
        }
    }
}
