package com.ys.idatrix.metacube.metamanage.service;

import com.ys.idatrix.metacube.api.beans.PageResultBean;
import com.ys.idatrix.metacube.metamanage.domain.Metadata;
import com.ys.idatrix.metacube.metamanage.vo.request.MetaDefHDFSVO;
import com.ys.idatrix.metacube.metamanage.vo.request.MetadataSearchVo;
import com.ys.idatrix.metacube.metamanage.vo.response.MetaDefOverviewVO;

import java.util.List;

/**
 * 元数据定义-HDFS
 */
public interface IMetaDefHDFSService {

    /**
     * HDFS定义查询
     * @param searchVO
     * @return
     */
    PageResultBean<MetaDefOverviewVO> hdfsQueryOverview(MetadataSearchVo searchVO);

    /**
     * 保存草稿状态
     * @param rentId
     * @param user
     * @param baseVO
     * @return
     */
    Metadata saveDraft(Long rentId, String user, MetaDefHDFSVO baseVO);

    /**
     * 保存生效状态
     * @param rentId
     * @param user
     * @param baseVO
     * @return
     */
    Metadata saveExec(Long rentId, String user, MetaDefHDFSVO baseVO);

    /**
     * HDFS定义删除
     * @param rentId
     * @param user
     * @param id
     * @return
     */
    Long delete(Long rentId, String user, Long id);



    /**
     * HDFS获取详情
     * @param rentId
     * @param user
     * @param id
     * @return
     */
    Metadata getDetail(Long rentId, String user, Long id);


    /**
     * 根据租户或者搜索关键字查询所有列表
     * @param rentId
     * @param searchKey
     * @return
     */
    List<Metadata> getAllDirByRentId(Long rentId, String searchKey);

}
