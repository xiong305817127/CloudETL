package com.ys.idatrix.db.api.hdfs.service;


import com.ys.idatrix.db.api.common.RespResult;
import com.ys.idatrix.db.api.hdfs.dto.FileFetchDto;
import com.ys.idatrix.db.api.hdfs.dto.FileQueryDto;

import java.io.InputStream;
import java.util.List;

/**
 * @ClassName: HdfsService
 * @Description:
 * @Author: ZhouJian
 * @Date: 2019/3/4
 */
public interface HdfsService {


    /**
     * 列出指定目录下的文件\子目录信息（非递归）
     * 获取文件元数据信息
     *
     * @param username                    -- 用户id。用做权限
     * @param remoteFileAbsolutePath    -- 文件全路径
     * @return
     */
    RespResult<List<FileQueryDto>> getListFiles(String username, String remoteFileAbsolutePath);


    /**
     * 创建文件/目录
     *
     * @param username                    --必须 用户id。用做权限
     * @param remoteFileAbsolutePath    --必须 文件全路径
     * @param fileName                  --必须 文件名称
     * @return
     */
    RespResult<Boolean> createDir(String username, String remoteFileAbsolutePath, String fileName);


    /**
     * 删除目录或文件(如果有子目录,则级联删除)
     *
     * @param username                    --必须 用户id。用做权限
     * @param remoteFileAbsolutePaths   --必须 文件全路径（单个或同主目录下的多个文件）
     * @return
     */
    RespResult<Boolean> deleteFile(String username, List<String> remoteFileAbsolutePaths);


    /**
     * 文件/目录重命名
     *
     * @param username                    --必须 用户id。用做权限
     * @param remoteFileAbsolutePath    --必须 文件全路径
     * @param newName                   --必须 新文件名称
     * @return
     */
    RespResult<Boolean> renameFile(String username, String remoteFileAbsolutePath, String newName);

    /**
     * 文件上传
     *
     * @param username                    --必须 用户id。用做权限
     * @param remoteFileAbsolutePath    --必须 文件全路径
     * @param fileContents              --必须  上传文件内容
     * @return
     */
    RespResult<Boolean> uploadFile(String username, String remoteFileAbsolutePath, byte[] fileContents);


    /**
     * 文件上传  流对象操作
     * @param username
     * @param remoteFileAbsolutePath
     * @return
     */
    RespResult<Boolean> uploadFileByStream(String username, String remoteFileAbsolutePath, InputStream inputStream);


    /**
     * 文件下载 输入流对象操作
     * @param username
     * @param remoteFileAbsolutePath
     * @return
     */
    InputStream downloadFileByStream(String username, String remoteFileAbsolutePath);


    /**
     * 文件下载(多块下载)
     *
     * @param username                    --必须 用户id。用做权限
     * @param remoteFileAbsolutePath    --必须 文件全路径
     * @param blockSeq                  --必须  文件提取的顺序数
     * @param blockSize                 --必须  每次获取的文件大小
     * @return
     */
    RespResult<FileFetchDto> downloadFile(String username, String remoteFileAbsolutePath, int blockSeq, int blockSize);


    /**
     * 读取文件内容
     *
     * @param username                    --必须 用户id。用做权限
     * @param remoteFileAbsolutePath    --必须 文件全路径
     * @return
     */
    RespResult<FileFetchDto> readFile(String username, String remoteFileAbsolutePath);

}
