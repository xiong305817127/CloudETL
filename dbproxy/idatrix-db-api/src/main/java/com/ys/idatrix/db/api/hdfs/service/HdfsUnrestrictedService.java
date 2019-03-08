package com.ys.idatrix.db.api.hdfs.service;


import com.ys.idatrix.db.api.common.RespResult;
import com.ys.idatrix.db.api.hdfs.dto.FileQueryDto;

import java.io.InputStream;
import java.util.List;

/**
 * @ClassName: HdfsUnrestrictedService
 * @Description: 元数据注册目录增、删、改操作
 * @Author: ZhouJian
 * @Date: 2019/3/4
 */
public interface HdfsUnrestrictedService {


    /**
     * 创建目录
     *
     * @param owner   文件创建者，拥有者
     * @param dirPath 创建目录的全路径（路径+目录名称）
     * @return
     */
    RespResult<Boolean> createDir(String owner, String dirPath);

    /**
     * 文件是否存在
     * @param remoteFilePath
     * @return 存在返回true 否则返回false
     */
    RespResult<Boolean> fileExists(String remoteFilePath);

    /**
     * 删除目录
     *
     * @param dirPaths 删除的目录全路径列表
     * @return
     */
    RespResult<Boolean> deleteDir(List<String> dirPaths, boolean bForced);


    /**
     * 删除文件
     *
     * @param remoteFileAbsolutePaths 删除的文件全路径列表
     * @return
     */
    RespResult<Boolean> deleteFile(List<String> remoteFileAbsolutePaths);


    /**
     * 目录重命名
     *
     * @param oldDirPath 待修改目录全路径
     * @param newName    新的目录名称
     * @return
     */
    RespResult<Boolean> renameDir(String oldDirPath, String newName);


    /**
     * 文件上传（scheduler） 输出流对象操作
     *
     * @param remoteFileAbsolutePath
     * @return
     */
    RespResult<Boolean> uploadFileByStream(String remoteFileAbsolutePath, InputStream inputStream);


    /**
     * 文件下载 输入流对象操作
     *
     * @param remoteFileAbsolutePath
     * @return
     */
    InputStream downloadFileByStream(String remoteFileAbsolutePath);


    /**
     * 元数据获取目录下的文件数据（仅限文件）
     *
     * @param remoteFileAbsolutePath
     * @return
     */
    RespResult<List<FileQueryDto>>  getListFiles(String remoteFileAbsolutePath);


    /**
     * 创建平台用户目录
     *
     * @param owner
     * @return
     */
    RespResult<Boolean> createPlatformUserDir(String owner);

}
