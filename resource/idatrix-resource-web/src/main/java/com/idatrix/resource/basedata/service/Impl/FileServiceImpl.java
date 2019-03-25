package com.idatrix.resource.basedata.service.Impl;

import com.idatrix.resource.basedata.dao.FileDAO;
import com.idatrix.resource.basedata.po.FilePO;
import com.idatrix.resource.basedata.po.SystemConfigPO;
import com.idatrix.resource.basedata.service.IFileService;
import com.idatrix.resource.basedata.service.ISystemConfigService;
import com.idatrix.resource.basedata.vo.FileQueryVO;
import com.idatrix.resource.basedata.vo.FileVO;
import com.idatrix.resource.common.Exception.CommonServiceException;
import com.idatrix.resource.common.utils.CommonConstants;
import com.idatrix.resource.common.utils.CommonUtils;
import com.ys.idatrix.db.api.common.RespResult;
import com.ys.idatrix.db.api.hdfs.service.HdfsUnrestrictedService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 文件服务接口实现
 *
 * @author wzl
 */
@Service
public class FileServiceImpl implements IFileService {

    private static final Logger LOGGER = LoggerFactory.getLogger(FileServiceImpl.class);

    @Autowired
    private FileDAO fileDAO;

    @Autowired
    private ISystemConfigService systemConfigService;

    @Autowired
    private HdfsUnrestrictedService hdfsUnrestrictedDao;

    @Override
    public List<FilePO> getFilesBySourceAndParentIdAndCreator(Integer source, Long parentId,
            String creator) {
        return fileDAO.getFilesBySourceAndParentIdAndCreator(source, parentId, creator);
    }

    /**
     * @param files 要上传的文件
     * @param source 文件来源 若为空 则默认为1 表示服务说明文档
     * @param username 当前用户
     */
    @Override
    public List<FileVO> uploadFile(MultipartFile[] files, Integer source,
            String username) throws Exception {
        // 获取系统参数配置
        String hdfsPath = checkSystemConfig().getOriginFileRoot();

        List<FilePO> filePOList = new ArrayList<>();
        for (MultipartFile file : files) {
            String ext = parseFileExtension(file);
            String storageFileName = generateStorageFileName(ext);
            // TODO 此处直接上传文件。后期若有需要可先判断文件是否已存在，若存在则删除后再上传
            uploadFileToHDFS(file, hdfsPath + storageFileName);

            FilePO filePO = generateFilePO(source, storageFileName,
                    file.getOriginalFilename(), ext, username, file.getSize());
            if (filePO.getId() == null) {
                fileDAO.insert(filePO);
            } else {
                fileDAO.update(filePO);
            }
            filePOList.add(filePO);
        }

        List<FileVO> fileVOList = filePOList.stream().map(e -> new FileVO(e.getId(),
                e.getOriginFileName())).collect(Collectors.toList());

        return fileVOList;
    }

    /**
     * 获取文件
     */
    @Override
    public FilePO getFile(FileQueryVO queryVO) {
        return fileDAO.getFile(queryVO);
    }

    /**
     * 根据id获取文件
     */
    @Override
    public FilePO getFileById(Long id) throws Exception {
        FilePO filePO = fileDAO.getFileById(id);
        if (filePO == null) {
            throw new CommonServiceException(CommonConstants.EC_NOT_EXISTED_VALUE, "文件不存在");
        }
        return fileDAO.getFileById(id);
    }

    /**
     * 删除文件
     *
     * @param id 文件id
     */
    @Override
    public void delete(Long id) throws Exception {
        FilePO filePO = getFileById(id);
        if (filePO == null) {
            return;
        }
        filePO.setIsDeleted(1);
        fileDAO.update(filePO);
    }

    /**
     * 根据ids批量更新parentId
     *
     * @param ids 文件id列表
     */
    @Override
    public void batchUpdateParentIdByIds(List<Long> ids, Long parentId) {
        fileDAO.batchUpdateParentIdByIds(ids, parentId);
    }

    /**
     * 根据文件id返回文件流
     */
    @Override
    public InputStream download(Long id) throws Exception {
        FilePO filePO = getFileById(id);
        return hdfsUnrestrictedDao
                .downloadFileByStream(
                        checkSystemConfig().getOriginFileRoot() + filePO.getStorageFileName());
    }

    /**
     * 检查系统参数配置
     */
    private SystemConfigPO checkSystemConfig() throws CommonServiceException {
        SystemConfigPO systemConfigPO = systemConfigService.getSystemConfig();
        if (systemConfigPO == null) {
            throw new CommonServiceException(CommonConstants.EC_NULL_VALUE,
                    "系统参数还未配置，请在先配置再使用");
        }
        return systemConfigPO;
    }

    /**
     * 解析文件扩展名
     */
    private String parseFileExtension(MultipartFile file) {
        StringBuilder builder = new StringBuilder();
        String[] nameSplit = file.getOriginalFilename().split("\\.");
        builder.append(nameSplit[nameSplit.length - 1]);
        return builder.toString();
    }

    /**
     * 生成文件实际存储名
     */
    private String generateStorageFileName(String ext) {
        return new StringBuilder()
                .append(CommonUtils.generateUUID())
                .append(".")
                .append(ext).toString();
    }

    /**
     * 上传文件到hdfs
     */
    private void uploadFileToHDFS(MultipartFile file, String path) throws Exception {
        RespResult<Boolean> hdfsExecuteResult =
                hdfsUnrestrictedDao.uploadFileByStream("hdfs:" + path, file.getInputStream());
    }

    private FilePO generateFilePO(Integer source, String storageFileName,
            String originFileName, String extension, String userName, long fileSize) {
        FileQueryVO queryVO = new FileQueryVO();
        queryVO.setSource(source)
                .setOriginFileName(originFileName)
                .setCreator(userName);
        FilePO filePO = fileDAO.getFile(queryVO);

        if (filePO == null || filePO.getId() == null) {
            filePO = new FilePO();
            filePO.setCreator(userName);
            filePO.setCreateTime(new Date());
        }
        filePO.setSource(source)
                .setStorageFileName(storageFileName)
                .setOriginFileName(originFileName)
                .setFileSize(fileSize)
                .setFileExtension(extension)
                .setModifier(userName)
                .setModifyTime(new Date());

        return filePO;
    }

}
