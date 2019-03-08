package com.idatrix.resource.exchange.vo.request;

import java.util.List;

/**
 * 用来存储从HDFS下载文件以后，资源的文件信息
 */
public class LocalFileInfo {

    /*本地文件路径*/
    private String fileDir;

    /*文件名列表*/
    private List<String> fileNameList;

    /*文件名列表，文件名之间用"|"间隔*/
    private String fileMask;

    public String getFileDir() {
        return fileDir;
    }

    public void setFileDir(String fileDir) {
        this.fileDir = fileDir;
    }

    public List<String> getFileNameList() {
        return fileNameList;
    }

    public void setFileNameList(List<String> fileNameList) {
        this.fileNameList = fileNameList;
    }

    public String getFileMask() {
        return fileMask;
    }

    public void setFileMask(String fileMask) {
        this.fileMask = fileMask;
    }
}
