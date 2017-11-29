package com.cn.mv.molder;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

import java.io.File;
import java.util.List;

/**
 * Created by Administrator on 2016/7/17.
 */
@Table(name = "file_info")
public class FileInfo extends Model{
    @Column(name = "files")
    private File[] files;

    @Column(name = "file_lis")
    private List<File> fileList;

    public File[] getFiles() {
        return files;
    }

    public void setFiles(File[] files) {
        this.files = files;
    }

    public List<File> getFileList() {
        return fileList;
    }

    public void setFileList(List<File> fileList) {
        this.fileList = fileList;
    }
}
