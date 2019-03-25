package com.ys.idatrix.db.service.external.provider.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.ys.idatrix.db.api.common.RespResult;
import com.ys.idatrix.db.api.hdfs.dto.FileFetchDto;
import com.ys.idatrix.db.api.hdfs.dto.FileQueryDto;
import com.ys.idatrix.db.api.hdfs.service.HdfsService;
import com.ys.idatrix.db.core.hdfs.HdfsExecService;
import com.ys.idatrix.db.exception.DbProxyException;
import com.ys.idatrix.db.service.external.consumer.MetadataConsumer;
import com.ys.idatrix.metacube.api.beans.ActionTypeEnum;
import com.ys.idatrix.metacube.api.beans.ResultBean;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.io.InputStream;
import java.util.List;

/**
 * @ClassName: HdfsServiceImpl
 * @Description: hdfs proxy service
 * @Author: ZhouJian
 * @Date: 2017/4/25
 */
@Slf4j
@Service(protocol = {"dubbo", "hessian"}, timeout = 50000, interfaceClass = HdfsService.class)
@Component
public class HdfsServiceImpl implements HdfsService {

    @Autowired(required = false)
    private HdfsExecService hdfsExecService;

    @Autowired(required = false)
    private MetadataConsumer metadataConsumer;

    private final String FILE_SEPARATOR = "/";

    @Override
    public RespResult<List<FileQueryDto>> getListFiles(String username, String remoteFileAbsolutePath) {

        try {

            checkPermission(username, remoteFileAbsolutePath, ActionTypeEnum.READ);

            if (!hdfsExecService.fileExists(remoteFileAbsolutePath)) {
                return RespResult.buildFailWithMsg(remoteFileAbsolutePath + " 不存在");
            }

            List<FileQueryDto> fileQueryDtoList = hdfsExecService.getFileList(username, remoteFileAbsolutePath);
            return RespResult.buildSuccessWithData(fileQueryDtoList);
        } catch (Exception e) {
            log.error("getListFiles 执行异常:{}", e.getMessage());
            return RespResult.buildFailWithMsg(e.getMessage());
        }
    }


    @Override
    public RespResult<Boolean> createDir(String username, String remoteFileAbsolutePath, String fileName) {
        try {

            checkPermission(username, remoteFileAbsolutePath, ActionTypeEnum.WRITE);

            String remoteFilePath = remoteFileAbsolutePath + FILE_SEPARATOR + fileName;
            if (hdfsExecService.fileExists(remoteFilePath)) {
                return RespResult.buildFailWithMsg("目录已存在");
            }

            Boolean isSuccess = hdfsExecService.createDir(null, remoteFilePath);
            return RespResult.buildSuccessWithData(isSuccess);
        } catch (Exception e) {
            log.error("createDir 执行异常:{}", e.getMessage());
            return RespResult.buildFailWithMsg(e.getMessage());
        }
    }


    @Override
    public RespResult<Boolean> renameFile(String username, String remoteFileAbsolutePath, String newName) {
        try {

            checkPermission(username, remoteFileAbsolutePath, ActionTypeEnum.WRITE);

            if (!hdfsExecService.fileExists(remoteFileAbsolutePath)) {
                return RespResult.buildFailWithMsg(remoteFileAbsolutePath + " 不存在");
            }

            //获取待修改文件父目录全路径
            String remoteParentPath = "/";
            switch (remoteFileAbsolutePath.lastIndexOf("/")) {
                case -1:
                    return RespResult.buildFailWithMsg("远程文件路径无效");
                case 0:
                    break;
                default:
                    remoteParentPath = remoteFileAbsolutePath.substring(0, remoteFileAbsolutePath.lastIndexOf('/') + 1);
                    break;
            }

            //新的文件全路径
            String newRemoteFilePath = remoteParentPath + newName;
            if (hdfsExecService.fileExists(newRemoteFilePath)) {
                return RespResult.buildFailWithMsg(newName + " 已存在");
            }

            Boolean isSuccess = hdfsExecService.renameFile(username, remoteFileAbsolutePath, newRemoteFilePath);

            return RespResult.buildSuccessWithData(isSuccess);

        } catch (Exception e) {
            log.error("renameFile 执行异常:{}", e.getMessage());
            return RespResult.buildFailWithMsg(e.getMessage());
        }
    }


    @Override
    public RespResult<Boolean> deleteFile(String username, List<String> remoteFileAbsolutePaths) {
        try {
            if (!CollectionUtils.isEmpty(remoteFileAbsolutePaths)) {

                List<String> errorFiles = Lists.newArrayList();

                for (String filePath : remoteFileAbsolutePaths) {
                    checkPermission(username, filePath, ActionTypeEnum.WRITE);
                    if (!hdfsExecService.fileExists(filePath)) {
                        return RespResult.buildFailWithMsg(filePath + " 不存在");
                    } else {
                        try {
                            hdfsExecService.deleteFile(username, filePath);
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


    @Override
    public RespResult<Boolean> uploadFile(String username, String remoteFileAbsolutePath, byte[] fileContents) {
        try {

            checkPermission(username, remoteFileAbsolutePath, ActionTypeEnum.WRITE);

            if (hdfsExecService.fileExists(remoteFileAbsolutePath)) {
                return RespResult.buildFailWithMsg("文件已存在");
            }

            Boolean isSuccess = hdfsExecService.uploadFile(remoteFileAbsolutePath, fileContents);
            return RespResult.buildSuccessWithData(isSuccess);
        } catch (Exception e) {
            log.error("uploadFile 执行异常:{}", e.getMessage());
            return RespResult.buildFailWithMsg(e.getMessage());
        }
    }


    @Override
    public RespResult<Boolean> uploadFileByStream(String username, String remoteFileAbsolutePath, InputStream
            inputStream) {
        try {

            checkPermission(username, remoteFileAbsolutePath, ActionTypeEnum.WRITE);

            if (hdfsExecService.fileExists(remoteFileAbsolutePath)) {
                return RespResult.buildFailWithMsg("文件已存在");
            }

            Boolean isSuccess = hdfsExecService.uploadFileByStream(remoteFileAbsolutePath, inputStream);

            return RespResult.buildSuccessWithData(isSuccess);

        } catch (Exception e) {
            log.error("uploadFileByStream 执行异常:{}", e.getMessage());
            return RespResult.buildFailWithMsg(e.getMessage());
        } finally {
            IOUtils.closeStream(inputStream);
        }
    }


    @Override
    public InputStream downloadFileByStream(String username, String remoteFileAbsolutePath) {
        InputStream inputStream = null;
        try {

            checkPermission(username, remoteFileAbsolutePath, ActionTypeEnum.READ);

            if (!hdfsExecService.fileExists(remoteFileAbsolutePath)) {
                log.warn("文件下载失败：{}", remoteFileAbsolutePath + " 文件不存在");
                return null;
            }

            inputStream = hdfsExecService.downloadFileByStream(username, remoteFileAbsolutePath);
            log.info("HDFS 文件：{}下载成功!", remoteFileAbsolutePath);

        } catch (Exception e) {
            log.error("HDFS 文件下载异常", e);
            e.printStackTrace();
        }
        return inputStream;
    }


    @Override
    public RespResult<FileFetchDto> downloadFile(String username, String remoteFileAbsolutePath, int blockSeq,
                                                 int blockSize) {
        try {

            checkPermission(username, remoteFileAbsolutePath, ActionTypeEnum.READ);

            if (!hdfsExecService.fileExists(remoteFileAbsolutePath)) {
                return RespResult.buildFailWithMsg(remoteFileAbsolutePath + " 文件不存在");
            }

            FileFetchDto fileFetchDto = hdfsExecService.downloadFile(username, remoteFileAbsolutePath, blockSeq, blockSize);

            return RespResult.buildSuccessWithData(fileFetchDto);

        } catch (Exception e) {
            log.error("downloadFile 执行异常:{}", e.getMessage());
            return RespResult.buildFailWithMsg(e.getMessage());
        }

    }


    @Override
    public RespResult<FileFetchDto> readFile(String username, String remoteFileAbsolutePath) {
        try {

            checkPermission(username, remoteFileAbsolutePath, ActionTypeEnum.READ);

            if (!hdfsExecService.fileExists(remoteFileAbsolutePath)) {
                return RespResult.buildFailWithMsg(remoteFileAbsolutePath + " 不存在");
            }

            FileFetchDto fileFetchDto = hdfsExecService.readFile(username, remoteFileAbsolutePath);

            return RespResult.buildSuccessWithData(fileFetchDto);

        } catch (Exception e) {
            log.error("readFile 执行异常:{}", e.getMessage());
            return RespResult.buildFailWithMsg(e.getMessage());
        }

    }


    /**
     * 检查操作权限
     *
     * @param username
     * @param remoteFileAbsolutePath
     * @param permission
     * @throws Exception
     */
    private void checkPermission(String username, String remoteFileAbsolutePath, ActionTypeEnum permission) {

        if (StringUtils.isBlank(username)) {
            log.error("username is null");
            throw new DbProxyException("username is null");
        }

        ActionTypeEnum actionTypeEnum;
        try {
            ResultBean<ActionTypeEnum> result = metadataConsumer.getHdfsPermiss(username, remoteFileAbsolutePath);
            if (result.isSuccess()) {
                actionTypeEnum = result.getData();
            } else {
                log.error("查询元数据文件路径权限失败:{}", result.getMsg());
                throw new DbProxyException("查询元数据文件路径权限失败");
            }
        } catch (Exception e) {
            log.error("调用元数据接口：{}，输入参数：user={},filePath={}，执行异常：{}", "getHdfsPermiss", username, remoteFileAbsolutePath, e);
            throw new DbProxyException("查询元数据文件路径权限异常");
        }

        Preconditions.checkNotNull(actionTypeEnum, "元数据权限未定义，禁止操作");

        if (actionTypeEnum.getCode() < permission.getCode()) {
            String msg = "没有文件的可" + permission.getName() + "权限";
            log.error("用户：{} 没有文件：{} 的可{}权限", username, remoteFileAbsolutePath, permission.getName());
            throw new DbProxyException(msg);
        }

    }

}
