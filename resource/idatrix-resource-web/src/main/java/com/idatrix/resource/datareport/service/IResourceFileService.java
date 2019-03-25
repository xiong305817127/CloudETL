package com.idatrix.resource.datareport.service;

import com.idatrix.resource.common.utils.ResultPager;
import com.idatrix.resource.datareport.po.ResourceFilePO;
import com.idatrix.resource.datareport.vo.ResourceFileVO;

import java.util.List;
import java.util.Map;

/**
 * Created by Robin Wing on 2018-6-19.
 */
public interface IResourceFileService {

    ResultPager<ResourceFileVO> queryResourceFile(String user, Map<String, String> conditions,  Integer pageNum,
                                                    Integer pageSize);

    /**
     * 根据资源ID搜索文件名称
     * @param con
     * @return
     */
    List<ResourceFileVO> getResourceFileByResourceId(Map<String, String> con);


    /*根据fileId 下载具体文件*/
    ResourceFilePO getFileInfo(Long fileId) throws Exception;

    /*获取文件下载名称*/
    String getFileHdfsPath(Long fileId) throws Exception;
}
