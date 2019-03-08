package com.ys.idatrix.metacube.metamanage.service;

import com.ys.idatrix.metacube.api.beans.PageResultBean;
import com.ys.idatrix.metacube.metamanage.domain.EsMetadataPO;
import com.ys.idatrix.metacube.metamanage.vo.request.EsIndexVO;
import com.ys.idatrix.metacube.metamanage.vo.request.MetadataSearchVo;

import java.util.List;
import java.util.Map;

/**
 * @ClassName: EsIndexService
 * @Description: ES 索引处理服务
 * @Author: ZhouJian
 * @Date: 2019/1/23
 */
public interface EsIndexService {


    /**
     * 分页查询索引
     *
     * @param searchVo
     * @return
     */
    PageResultBean<EsMetadataPO> search(MetadataSearchVo searchVo);


    /**
     * 根据索引标识id 查询索引详细（基本信息+类型、字段信息）
     *
     * @param metaId
     * @return
     */
    EsIndexVO queryEsDetail(Long metaId);


    /**
     * 校验是否存在索引
     *
     * @param schemaId
     * @param schemaName
     * @param isDrafted
     * @return
     */
    boolean checkExistsIndex(Long schemaId, String schemaName, boolean isDrafted);


    /**
     * 保存索引
     * ①保存并生效
     * ②保存操作(草稿)
     *
     * @param esIndexVO
     * @param isDrafted
     * @return
     */
    boolean saveOrCreatedIndex(EsIndexVO esIndexVO, boolean isDrafted);


    /**
     * 修改
     *
     * @param esIndexVO
     * @return
     */
    boolean updateIndex(EsIndexVO esIndexVO);


    /**
     * 删除
     *
     * @param ids
     * @return
     */
    boolean softDeleteIndex(List<Long> ids);


    /**
     * 启停
     *
     * @param id
     * @param isOpen
     * @return
     */
    boolean openOrStartIndex(Long id, boolean isOpen);


    /**
     * 查询版本
     *
     * @param id
     * @return
     */
    List<Map<Long, Integer>> queryVersionsByMetaId(Long id);


    /**
     * 切换
     *
     * @param id
     * @param targetVersion
     * @return
     */
    boolean switchIndex(Long id, int targetVersion);

    /**
     * 根据ID查询ES的基本信息
     *
     * @param id
     * @return
     */
    EsMetadataPO findById(Long id);


}
