package com.idatrix.resource.datareport.service.Impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.idatrix.resource.basedata.dao.SystemConfigDAO;
import com.idatrix.resource.basedata.po.SystemConfigPO;
import com.idatrix.resource.basedata.service.ISystemConfigService;
import com.idatrix.resource.common.utils.CommonUtils;
import com.idatrix.resource.common.utils.DateTools;
import com.idatrix.resource.common.utils.ResultPager;
import com.idatrix.resource.datareport.dao.ResourceFileDAO;
import com.idatrix.resource.datareport.po.ResourceFilePO;
import com.idatrix.resource.datareport.service.IResourceFileService;
import com.idatrix.resource.datareport.vo.ResourceFileVO;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Robin Wing on 2018-6-19.
 */
@Transactional
@Service("resourceFileService")
public class ResourceFileServiceImpl implements IResourceFileService {

    private final Logger LOG= LoggerFactory.getLogger(this.getClass());

    @Autowired
    private ResourceFileDAO resourceFileDAO;

    @Autowired
    private SystemConfigDAO systemConfigDAO;

    @Autowired
    private ISystemConfigService systemConfigService;



    @Override
    public ResultPager<ResourceFileVO> queryResourceFile(Map<String, String> con,
                                                         Integer pageNum, Integer pageSize) {
        pageNum = null == pageNum ? 1 : pageNum;
        pageSize = null == pageSize ? 10 : pageSize;
        PageHelper.startPage(pageNum, pageSize);

        List<ResourceFilePO> poList = resourceFileDAO.queryResourceFile(con);
        if(poList==null || poList.size()==0){
            return null;
        }
        List<ResourceFileVO> voList = transferResourceFilePOToVO(poList);
        //用PageInfo对结果进行包装
        PageInfo<ResourceFilePO> pi = new PageInfo<ResourceFilePO>(poList);
        Long totalNum = pi.getTotal();
        ResultPager<ResourceFileVO> rp = new ResultPager<ResourceFileVO>(pi.getPageNum(),
                totalNum, voList);
        return rp;
    }

    /*文件下载*/
    @Override
    public ResourceFilePO getFileInfo(Long fileId) throws Exception {

        if (fileId == null || fileId == 0L) {
            throw new Exception("选择的文件不存在");
        }
        ResourceFilePO resourceFilePO = resourceFileDAO.getResourceFileById(fileId);
        if (resourceFilePO == null) {
            throw new Exception("上传记录中不存在需要下载的文件");
        }
        return resourceFilePO;
    }

    @Override
    public String getFileHdfsPath(Long fileId) throws Exception{

        if(fileId==null || fileId==0L){
            throw new Exception("选择的文件不存在");
        }
        ResourceFilePO resourceFilePO = resourceFileDAO.getResourceFileById(fileId);
        if(resourceFilePO==null){
            throw new Exception("上传记录中不存在需要下载的文件");
        }
        String originName = resourceFilePO.getOriginFileName();

        //获取最终文件存储路径
        String filePath = null;
//        SystemConfigPO sysConfigPO = systemConfigDAO.getLastestSysConfig();
        SystemConfigPO sysConfigPO = systemConfigService.getSystemConfigByUser(resourceFilePO.getCreator());
        if (sysConfigPO != null) {

            filePath = sysConfigPO.getFileRoot();
            if(StringUtils.isEmpty(filePath)){
                throw new RuntimeException("请配置上传文件最终存储路径");
            }
        } else {
            throw new Exception("系统参数没有配置，请先配置再上传");
        }
        String hdfsFilePath = filePath+originName;
        LOG.info("hdfs文件地址是：{}", hdfsFilePath);
        return  hdfsFilePath;
    }

    private List<ResourceFileVO> transferResourceFilePOToVO(List<ResourceFilePO> rfList){
        if(rfList==null || rfList.size()==0){
            return null;
        }

        List<ResourceFileVO> voList = new ArrayList<ResourceFileVO>();
        for(ResourceFilePO rfPO :rfList){
            ResourceFileVO rfVO = new ResourceFileVO();
            rfVO.setId(rfPO.getId());
            rfVO.setResourceId(rfPO.getResourceId());
            rfVO.setPubFileName(rfPO.getPubFileName());
            rfVO.setUpdateTime(DateTools.formatDate(rfPO.getModifyTime()));
            rfVO.setFileSize(CommonUtils.getFileSizeStr(Long.valueOf(rfPO.getFileSize())));
            rfVO.setFileType(rfPO.getFileType());
            rfVO.setFileDescription(rfPO.getFileDescription());
            if(StringUtils.isNotEmpty(rfPO.getDataBatch())) {
                rfVO.setDataBatch(rfPO.getDataBatch());
            }
            voList.add(rfVO);
        }
        return voList;
    }
}
