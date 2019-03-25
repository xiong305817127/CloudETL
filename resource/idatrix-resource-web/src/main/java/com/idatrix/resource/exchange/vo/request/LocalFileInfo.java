package com.idatrix.resource.exchange.vo.request;

import lombok.Data;

import java.util.List;

/**
 * 用来存储从HDFS下载文件以后，资源的文件信息
 */
@Data
public class LocalFileInfo {

    /*本地文件路径*/
    private String fileDir;

    /*文件名列表*/
    private List<String> fileNameList;

    /*文件名列表，文件名之间用"|"间隔*/
    private String fileMask;

}
