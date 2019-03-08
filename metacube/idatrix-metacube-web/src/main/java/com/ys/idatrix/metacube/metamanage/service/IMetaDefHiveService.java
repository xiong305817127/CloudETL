package com.ys.idatrix.metacube.metamanage.service;

import com.ys.idatrix.metacube.api.beans.PageResultBean;
import com.ys.idatrix.metacube.metamanage.domain.Metadata;
import com.ys.idatrix.metacube.metamanage.vo.request.MetaDefHiveVO;
import com.ys.idatrix.metacube.metamanage.vo.request.MetadataSearchVo;
import com.ys.idatrix.metacube.metamanage.vo.response.MetaDefOverviewVO;

/**
 * Created by Administrator on 2019/1/25.
 */
public interface IMetaDefHiveService {

    /**
     * Hive定义查询
     * @param searchVO
     * @return
     */
    PageResultBean<MetaDefOverviewVO> hiveQueryOverview(MetadataSearchVo searchVO);

    /**
     * Hive保存草稿状态
     * @param rentId
     * @param user
     * @param baseVO
     * @return
     */
    Metadata saveDraft(Long rentId, String user, MetaDefHiveVO baseVO);

    /**
     * Hive保存生效状态
     * @param rentId
     * @param user
     * @param baseVO
     * @return
     */
    Metadata saveExec(Long rentId, String user, MetaDefHiveVO baseVO);

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
    MetaDefHiveVO getDetail(Long rentId, String user, Long id);
}


