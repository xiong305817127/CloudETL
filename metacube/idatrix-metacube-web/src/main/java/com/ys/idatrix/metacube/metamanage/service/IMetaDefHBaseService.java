package com.ys.idatrix.metacube.metamanage.service;

import com.ys.idatrix.metacube.api.beans.PageResultBean;
import com.ys.idatrix.metacube.metamanage.domain.Metadata;
import com.ys.idatrix.metacube.metamanage.vo.request.MetaDefHbaseVO;
import com.ys.idatrix.metacube.metamanage.vo.request.MetadataSearchVo;
import com.ys.idatrix.metacube.metamanage.vo.response.MetaDefOverviewVO;

/**
 * 元数据定义-Hbase实现
 */
public interface IMetaDefHBaseService {


    /**
     * HBase定义查询
     * @param searchVO
     * @return
     */
    PageResultBean<MetaDefOverviewVO> hbaseQueryOverview(MetadataSearchVo searchVO);

    /**
     * HBase保存草稿状态
     * @param rentId
     * @param user
     * @param baseVO
     * @return
     */
    Metadata saveDraft(Long rentId, String user, MetaDefHbaseVO baseVO);

    /**
     * Hive保存生效状态
     * @param rentId
     * @param user
     * @param baseVO
     * @return
     */
    Metadata saveExec(Long rentId, String user, MetaDefHbaseVO baseVO);

    /**
     * Hive删除
     * @param rentId
     * @param user
     * @param id
     * @return
     */
    Long delete(Long rentId, String user, Long id);

    /**
     * Hive获取详情
     * @param rentId
     * @param user
     * @param id
     * @return
     */
    MetaDefHbaseVO getDetail(Long rentId, String user, Long id);
}
