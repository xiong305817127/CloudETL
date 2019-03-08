package com.ys.idatrix.db.service.external.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.google.common.collect.Lists;
import com.ys.idatrix.db.api.common.RespResult;
import com.ys.idatrix.db.api.hdfs.dto.FileQueryDto;
import com.ys.idatrix.db.api.hdfs.service.HdfsUnrestrictedService;
import com.ys.idatrix.db.core.hdfs.HdfsExecService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @ClassName: HdfsUnrestrictedServiceImpl
 * @Description: 元数据注册目录增、删、改操作
 * @Author: ZhouJian
 * @Date: 2017/4/25
 */
@Slf4j
@Service(protocol = {"dubbo", "hessian"}, timeout = 50000, interfaceClass = HdfsUnrestrictedService.class)
@Component
public class HdfsUnrestrictedServiceImpl implements HdfsUnrestrictedService {

    @Autowired(required = false)
    private HdfsExecService hdfsExecService;

    /**
     * 创建目录
     */
    @Override
    public RespResult<Boolean> createDir(String owner, String dirPath) {
        try {
            if (hdfsExecService.fileExists(dirPath)) {
                return RespResult.buildFailWithMsg(dirPath + " 已存在");
            }
            return RespResult.buildSuccessWithData(hdfsExecService.createDir(null, dirPath));
        } catch (Exception e) {
            log.error("createDir 执行异常:{}", e.getMessage());
            return RespResult.buildFailWithMsg(e.getMessage());
        }
    }

    /**
     * 文件是否存在
     *
     * @return 存在返回true 否则返回false
     */
    @Override
    public RespResult<Boolean> fileExists(String remoteFilePath) {
        try {
            boolean hasExists = hdfsExecService.fileExists(remoteFilePath);
            return RespResult.buildSuccessWithData(hasExists);
        } catch (Exception e) {
            log.error("createDir 执行异常:{}", e.getMessage());
            return RespResult.buildFailWithMsg(e.getMessage());
        }
    }


    /**
     * 删除目录
     */
    @Override
    public RespResult<Boolean> deleteDir(List<String> dirPaths, boolean bForced) {

        try {
            if (!CollectionUtils.isEmpty(dirPaths)) {

                List<String> errorFiles = Lists.newArrayList();

                for (String filePath : dirPaths) {
                    if (!hdfsExecService.fileExists(filePath)) {
                        return RespResult.buildFailWithMsg(filePath + " 不存在");
                    }

                    /**
                     * 是否强制删除
                     * 否：查看是否包含子文件。有则提示错误
                     * 是：直接删除
                     */
                    if (!bForced) {
                        List<FileQueryDto> fileList = hdfsExecService.getFileList(null, filePath);
                        if (CollectionUtils.isNotEmpty(fileList)) {
                            return RespResult.buildFailWithMsg("存在子文件");
                        }
                    }
                    try {
                        hdfsExecService.deleteFile(null, filePath);
                    } catch (Exception e) {
                        errorFiles.add(filePath);
                    }
                }
                if (errorFiles.size() > 0) {
                    return RespResult.buildFailWithMsg("目录：" + errorFiles + " 删除失败");
                }

                return RespResult.buildSuccessWithData(Boolean.TRUE);
            } else {
                return RespResult.buildFailWithMsg("待删除目录为空");
            }
        } catch (Exception e) {
            log.error("deleteFile 执行异常:{}", e.getMessage());
            return RespResult.buildFailWithMsg(e.getMessage());
        }
    }


    @Override
    public RespResult<Boolean> deleteFile(List<String> remoteFileAbsolutePaths) {
        try {
            if (!CollectionUtils.isEmpty(remoteFileAbsolutePaths)) {

                List<String> errorFiles = Lists.newArrayList();

                for (String filePath : remoteFileAbsolutePaths) {
                    if (!hdfsExecService.fileExists(filePath)) {
                        return RespResult.buildFailWithMsg(filePath + " 不存在");
                    } else {
                        try {
                            hdfsExecService.deleteFile(null, filePath);
                        } catch (Exception e) {
                            errorFiles.add(filePath);
                        }
                    }
                }
                if (errorFiles.size() > 0) {
                    return RespResult.buildFailWithMsg("文件：" + errorFiles + " 删除失败");
                }

                return RespResult.buildSuccessWithData(Boolean.TRUE);

            } else {
                return RespResult.buildFailWithMsg("待删除文件为空");
            }
        } catch (Exception e) {
            log.error("deleteFile 执行异常:{}", e.getMessage());
            return RespResult.buildFailWithMsg(e.getMessage());
        }
    }


    /**
     * 目录重命名
     */
    @Override
    public RespResult<Boolean> renameDir(String oldDirPath, String newName) {
        try {
            if (!hdfsExecService.fileExists(oldDirPath)) {
                return RespResult.buildFailWithMsg(oldDirPath + " 不存在");
            }

            //获取待修改目录的父目录全路径
            String remoteParentPath = "/";
            switch (oldDirPath.lastIndexOf("/")) {
                case -1:
                    return RespResult.buildFailWithMsg("远程文件路径无效");
                case 0:
                    break;
                default:
                    remoteParentPath = oldDirPath.substring(0, oldDirPath.lastIndexOf('/') + 1);
                    break;
            }

            //新的文件全路径
            String newRemoteFilePath = remoteParentPath + newName;
            if (hdfsExecService.fileExists(newRemoteFilePath)) {
                return RespResult.buildFailWithMsg(newRemoteFilePath + " 已存在");
            }

            Boolean isSuccess = hdfsExecService.renameFile(null, oldDirPath, newRemoteFilePath);
            return RespResult.buildSuccessWithData(isSuccess);
        } catch (Exception e) {
            log.error("renameDir 执行异常:{}", e.getMessage());
            return RespResult.buildFailWithMsg(e.getMessage());
        }

    }


    @Override
    public RespResult<Boolean> uploadFileByStream(String remoteFileAbsolutePath,
                                                  InputStream inputStream) {

        try {

            if (hdfsExecService.fileExists(remoteFileAbsolutePath)) {
                return RespResult.buildFailWithMsg("文件已存在");
            }

            Boolean isSuccess = hdfsExecService.uploadFileByStream(remoteFileAbsolutePath, inputStream);
            return RespResult.buildSuccessWithData(isSuccess);
        } catch (Exception e) {
            log.error("uploadFileByStream 执行异常:{}", e.getMessage());
            e.printStackTrace();
            return RespResult.buildFailWithMsg(e.getMessage());
        } finally {
            IOUtils.closeStream(inputStream);
        }
    }


    @Override
    public InputStream downloadFileByStream(String remoteFileAbsolutePath) {
        InputStream inputStream = null;
        try {
            if (!hdfsExecService.fileExists(remoteFileAbsolutePath)) {
                log.warn("文件下载失败：{}", remoteFileAbsolutePath + " 文件不存在");
                return null;
            }
            inputStream = hdfsExecService.downloadFileByStream(null, remoteFileAbsolutePath);
            log.info("HDFS 文件：{}下载成功", remoteFileAbsolutePath);
        } catch (Exception e) {
            log.error("HDFS 文件下载异常", e);
            e.printStackTrace();
        }
        return inputStream;
    }


    @Override
    public RespResult<List<FileQueryDto>> getListFiles(String remoteFileAbsolutePath) {
        try {
            if (!hdfsExecService.fileExists(remoteFileAbsolutePath)) {
                return RespResult.buildFailWithMsg(remoteFileAbsolutePath + " 不存在");
            }

            List<FileQueryDto> fileList = hdfsExecService.getFileList(null, remoteFileAbsolutePath);

            List<FileQueryDto> files = fileList.stream()
                    .filter(fileQueryInfo -> fileQueryInfo.isFile()).collect(Collectors.toList());
            return RespResult.buildSuccessWithData(files);
        } catch (Exception e) {
            log.error("getListFiles 执行异常:{}", e.getMessage());
            return RespResult.buildFailWithMsg(e.getMessage());
        }

    }


    /**
     * 创建平台用户目录
     */
    @Override
    public RespResult<Boolean> createPlatformUserDir(String owner) {
        try {
            if (StringUtils.isBlank(owner)) {
                return RespResult.buildFailWithMsg("用户为空");
            }

            String fullPath = "/user/" + owner;
            if (hdfsExecService.fileExists(fullPath)) {
                return RespResult.buildFailWithMsg("用户目录已存在");
            }
            return RespResult.buildSuccessWithData(hdfsExecService.createDir(owner, fullPath));
        } catch (Exception e) {
            log.error("createPlatformUserDir 执行异常:{}", e.getMessage());
            return RespResult.buildFailWithMsg(e.getMessage());
        }


    }

}
