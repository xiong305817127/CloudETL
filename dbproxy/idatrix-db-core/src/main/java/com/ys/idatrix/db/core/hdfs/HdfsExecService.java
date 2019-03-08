package com.ys.idatrix.db.core.hdfs;

import com.google.common.collect.Lists;
import com.ys.idatrix.db.api.hdfs.dto.*;
import com.ys.idatrix.db.core.security.HadoopSecurityManager;
import com.ys.idatrix.db.exception.DbProxyException;
import com.ys.idatrix.db.exception.HadoopSecurityManagerException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.hadoop.fs.*;
import org.apache.hadoop.fs.permission.FsAction;
import org.apache.hadoop.fs.permission.FsPermission;
import org.apache.hadoop.io.IOUtils;
import org.iq80.leveldb.DBException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.*;

/**
 * FileSystem及FsPermission:区分 数据分析、元数据、服务开放、任务调度
 * FileSystem			FsPermission    数据查询IDE	  元数据	  服务开放	    任务调度
 * 目录文件查询		    getFSAsUser				/				是		  不涉及		是			不涉及
 * getFSAsDefaultUser
 * 创建目录			    getFSAsDefaultUser		700				是		  是			不涉及		不涉及
 * 修改目录、文件		    getFSAsUser				/				是		  是			不涉及		不涉及
 * getFSAsDefaultUser
 * 删除目录、文件		    getFSAsUser				/				是		  是			不涉及		不涉及
 * getFSAsDefaultUser
 * 文件上传			    getFSAsDefaultUser		700				是		  不涉及		不涉及		是
 * 文件下载			    getFSAsUser				/				是		  不涉及		是			是
 *
 * @ClassName: HdfsExecService
 * @Description: HDFS CRUD & Upload & Download 服务
 * @Author: ZhouJian
 * @Date:2019/3/4
 */
@Slf4j
@Service
public class HdfsExecService {

    @Autowired(required = false)
    private HadoopSecurityManager hadoopSecurityManager;


    /**
     * 文件是否存在
     *
     * @param remoteFilePath
     * @return
     */
    public boolean fileExists(String remoteFilePath) throws Exception {
        FileSystem fs = hadoopSecurityManager.getFSAsDefaultUser();
        return fs.exists(new Path(remoteFilePath));
    }

    /**
     * 列出指定目录下的文件\子目录信息（非递归）
     * 获取文件元数据信息
     *
     * @param remoteDirPath -必须 查询的远程目录路径
     * @return
     */
    public List<FileQueryDto> getFileList(String username, String remoteDirPath) throws Exception {
        List<FileQueryDto> queryDtoList = Lists.newArrayList();
        FileSystem fs;
        if (StringUtils.isNotEmpty(username)) {
            fs = hadoopSecurityManager.getFSAsUser(username);
        } else {
            fs = hadoopSecurityManager.getFSAsDefaultUser();
        }

        Path srcPath = new Path(remoteDirPath);
        FileStatus[] fileStatuses = fs.listStatus(srcPath);
        if (ArrayUtils.isNotEmpty(fileStatuses)) {
            //先按时间倒序排序
            Comparator<FileStatus> fileComparator = Comparator.comparing(f -> Long.valueOf(f.getModificationTime()));
            Arrays.asList(fileStatuses).sort(fileComparator.reversed());
            for (FileStatus fileStatus : fileStatuses) {
                FileQueryDto queryDto = new FileQueryDto();
                queryDto.setFileName(fileStatus.getPath().getName());
                queryDto.setFile(fileStatus.isFile());
                String filePath = fileStatus.getPath().toString();
                //获取core-sit.xml fs.defaultFS的值（hdfs uri），返回文件路径中过滤此部分
                if (filePath.contains(fs.getUri().toString())) {
                    filePath = filePath.split(fs.getUri().toString())[1];
                }
                queryDto.setFilePath(filePath);
                queryDto.setFileLen(fileStatus.getLen());
                if (fileStatus.getModificationTime() != 0L) {
                    queryDto.setModifiedTime(DateFormatUtils.format(fileStatus.getModificationTime(), "yyyy-MM-dd HH:mm:ss"));
                }
                if (fileStatus.getAccessTime() != 0L) {
                    queryDto.setAccessTime(DateFormatUtils.format(fileStatus.getAccessTime(), "yyyy-MM-dd HH:mm:ss"));
                }
                queryDto.setReplicates(fileStatus.getReplication());
                queryDto.setOwner(fileStatus.getOwner());
                queryDto.setGroupName(fileStatus.getGroup());
                queryDto.setPermissions(fileStatus.getPermission().toString());
                queryDtoList.add(queryDto);
            }
        }

        return queryDtoList;
    }


    /**
     * 创建文件/目录
     *
     * @param owner          -用户
     * @param remoteFilePath -必须 创建文件全路径
     * @return
     */
    public Boolean createDir(String owner, String remoteFilePath) throws Exception {
        FileSystem fs = hadoopSecurityManager.getFSAsDefaultUser();
        Path remotePath = new Path(remoteFilePath);
        boolean isSuccess = fs.mkdirs(remotePath);
        FsPermission permission = new FsPermission(FsAction.ALL, FsAction.NONE, FsAction.NONE);
        fs.setPermission(remotePath, permission);
        //数据安全新增，创建用户时在HDFS中创建用户的文件夹。具体路径在HDFS上的：/user/<owner>
        if (StringUtils.isNotBlank(owner)) {
            fs.setOwner(remotePath, owner, null);
        }
        return isSuccess;
    }


    /**
     * 文件重命名
     *
     * @param oldFilePath --必须 旧文件名全路径
     * @param newFilePath --必须 新文件名全路径
     * @return
     */
    public Boolean renameFile(String username, String oldFilePath, String newFilePath) throws Exception {
        Path oldPath = new Path(oldFilePath);
        Path newPath = new Path(newFilePath);
        boolean isSuccess;
        if (StringUtils.isNotEmpty(username)) {
            isSuccess = hadoopSecurityManager.getFSAsUser(username).rename(oldPath, newPath);
        } else {
            isSuccess = hadoopSecurityManager.getFSAsDefaultUser().rename(oldPath, newPath);
        }
        return isSuccess;
    }


    /**
     * 删除目录或文件(如果有子目录,则级联删除)
     *
     * @param remotePath --必须 hdfs文件或目录路径
     * @return
     */
    public Boolean deleteFile(String username, String remotePath) throws Exception {
        FileSystem fs;
        if (StringUtils.isNotEmpty(username)) {
            fs = hadoopSecurityManager.getFSAsUser(username);
        } else {
            fs = hadoopSecurityManager.getFSAsDefaultUser();
        }
        Path path = new Path(remotePath);
        if (!fs.exists(path)) {
            throw new DbProxyException(remotePath + " 不存在");
        } else {
            return fs.delete(path, true);
        }
    }


    /**
     * 文件上传 字节数组操作
     *
     * @param remoteFilePath --必须 远程文件路径
     * @param fileContents   --必须 文件内容
     * @return
     */
    public Boolean uploadFile(String remoteFilePath, byte[] fileContents) throws Exception {
        FSDataOutputStream outputStream = null;
        try {
            FileSystem fs = hadoopSecurityManager.getFSAsDefaultUser();
            Path path = new Path(remoteFilePath);
            outputStream = fs.create(path);
            outputStream.write(fileContents);
            FsPermission permission = new FsPermission(FsAction.ALL, FsAction.NONE, FsAction.NONE);
            fs.setPermission(path, permission);
            return Boolean.TRUE;
        } finally {
            IOUtils.closeStream(outputStream);
        }
    }


    /**
     * 文件上传 流对象操作
     *
     * @param remoteFilePath
     * @return
     */
    public Boolean uploadFileByStream(String remoteFilePath, InputStream inputStream) throws Exception {
        OutputStream outputStream = null;
        BufferedOutputStream bos = null;
        try {
            FileSystem fs = hadoopSecurityManager.getFSAsDefaultUser();
            Path path = new Path(remoteFilePath);
            outputStream = fs.create(path);
            bos = new BufferedOutputStream(outputStream);
            byte[] buff = new byte[1024];
            int bytesRead;
            while (-1 != (bytesRead = inputStream.read(buff, 0, buff.length))) {
                bos.write(buff, 0, bytesRead);
            }
            FsPermission permission = new FsPermission(FsAction.ALL, FsAction.NONE, FsAction.NONE);
            fs.setPermission(path, permission);
            return Boolean.TRUE;
        } finally {
            IOUtils.closeStream(bos);
            IOUtils.closeStream(outputStream);
        }
    }


    /**
     * 文件下载  流对象操作
     *
     * @param remoteFilePath
     * @return
     */
    public InputStream downloadFileByStream(String username, String remoteFilePath) {
        InputStream inputStream = null;
        try {
            Path path = new Path(remoteFilePath);
            if (StringUtils.isNotEmpty(username)) {
                inputStream = hadoopSecurityManager.getFSAsUser(username).open(path);
            } else {
                inputStream = hadoopSecurityManager.getFSAsDefaultUser().open(path);
            }
        } catch (IOException e) {
            log.error("download file:{} is error:{}", remoteFilePath, e);
        } catch (HadoopSecurityManagerException e) {
            log.error("get FileSystem by username:{} is error:{}", username, e);
        }
        return inputStream;
    }


    /**
     * 下载 hdfs上的文件
     *
     * @param remoteFilePath --必须 文件路径
     * @param blockSeq       --必须 提取序号。从0开始
     * @param blockSize      --必须 文件分块大小
     * @return
     */
    public FileFetchDto downloadFile(String username, String remoteFilePath, int blockSeq, int blockSize) throws Exception {
        FileFetchDto fetchDto = new FileFetchDto();
        FileSystem fs = hadoopSecurityManager.getFSAsUser(username);
        Path srcPath = new Path(remoteFilePath);
        FileStatus fileStatus = fs.getFileStatus(srcPath);

        /*** 目录直接返回 ***/
        if (fileStatus.isDirectory()) {
            return null;
        }

        /*** 文件名称 ***/
        fetchDto.setFileName(srcPath.getName());

        /*** 文件大小 ***/
        long fileSize = fileStatus.getLen();
        fetchDto.setFileSize(fileSize);
        if (0L == fileSize) {
            throw new DBException(remoteFilePath + " 文件长度为0");
        }

        /*** 文件分块数 ***/
        long blockSum = 0L;
        blockSum = fileSize / blockSize;
        if (fileSize % blockSize != 0) {
            blockSum = blockSum + 1;
        }

        /*** 当前传入的分块数 == 总的分块数，则设置为最后的读取块数  “-1” 从 0 开始***/
        if (blockSeq == blockSum - 1) {
            fetchDto.setBEnd(true);
        }

        /*** 根据传入的分块数 * 分块size 设置HDFS指定偏移量读取文件 ***/
        FSDataInputStream fsins = fs.open(srcPath);
        fsins.seek(blockSeq == 0 ? 0 : blockSeq * blockSize);

        /*** 本次读取的数据的大小 **/
        int current_fetchBytesSize = blockSize;
        if (fetchDto.isBEnd()) {
            current_fetchBytesSize = fileSize > blockSize ? (int) (fileSize - blockSeq * blockSize) : (int) fileSize;
        }

        /*** 开始字节读取 ***/
        ByteArrayOutputStream bos = new ByteArrayOutputStream(current_fetchBytesSize);
        // 记录总的已读字节数,记录单次已读字节数
        int buf_size = 1024, sum_readLength = 0, roll_readLength = 0;
        // 暂存容器(字节数组)
        byte[] bufArray = new byte[buf_size];
        while (sum_readLength <= current_fetchBytesSize - buf_size) {
            roll_readLength = fsins.read(bufArray, 0, buf_size);
            sum_readLength += buf_size;
            bos.write(bufArray, 0, roll_readLength);
        }
        if (sum_readLength <= current_fetchBytesSize) {
            roll_readLength = fsins.read(bufArray, 0, (current_fetchBytesSize - sum_readLength));
            bos.write(bufArray, 0, roll_readLength);
        }

        //文件内容做Base64加密处理
        byte[] blockBytes = Base64.getEncoder().encode(bos.toByteArray());
        fetchDto.setBlockBytes(blockBytes);

        if (log.isDebugEnabled()) {
            log.debug("######## remote file:{} download fetch blockSeq:{},blockBytes.length:{}", remoteFilePath, blockSeq, blockBytes.length);
        }
        return fetchDto;
    }


    /**
     * 读取文件内容
     *
     * @param remoteFilePath -必须 读取的远程文件路径
     * @return
     */
    public FileFetchDto readFile(String username, String remoteFilePath) throws Exception {
        InputStream inputStream = null;
        ByteArrayOutputStream outputStream = null;
        try {
            FileSystem fs = hadoopSecurityManager.getFSAsUser(username);
            Path path = new Path(remoteFilePath);
            if (fs.exists(path)) {
                inputStream = fs.open(path);
                outputStream = new ByteArrayOutputStream(inputStream.available());
                IOUtils.copyBytes(inputStream, outputStream, 4096);
                //文件内容做Base64加密处理
                byte[] fileContent = Base64.getEncoder().encode(outputStream.toByteArray());
                FileFetchDto fetchDto = new FileFetchDto();
                fetchDto.setBlockBytes(fileContent);
                fetchDto.setFileName(path.getName());
                return fetchDto;
            } else {
                throw new DBException(remoteFilePath + " 不存在");
            }
        } finally {
            IOUtils.closeStream(inputStream);
            IOUtils.closeStream(outputStream);
        }
    }

}
