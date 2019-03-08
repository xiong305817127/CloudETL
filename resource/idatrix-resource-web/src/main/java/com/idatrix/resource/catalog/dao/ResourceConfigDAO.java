package com.idatrix.resource.catalog.dao;

import com.idatrix.resource.catalog.po.ResourceConfigPO;
import com.idatrix.resource.catalog.vo.request.ResourceCatalogSearchVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * Created by Robin Wing on 2018-5-23.
 */
public interface ResourceConfigDAO {

    void insert(ResourceConfigPO resourceConfigPO);

    void deleteById(Long id);

    int updateById(ResourceConfigPO resourceConfigPO);

    ResourceConfigPO getConfigById(Long id);

    /*根据资源信息编码查询资源信息*/
    ResourceConfigPO getConfigByResourceCode(String resourceCode);

    List<ResourceConfigPO> getConfigByUser(String User);

    List<ResourceConfigPO> getAllResourceConfig();

    List<ResourceConfigPO> getByNameOrCode(@Param("name") String name,
            @Param("catalogCode") String catalogCode, @Param("seqNum") String seqNum);

    /**
     * 所有库的查询
     */
    List<ResourceConfigPO> queryByCondition(Map<String, String> condtions);

    /**
     * 根据查询条件查询已上架的资源
     */
    List<ResourceConfigPO> getPublishedResourcesByCondition(
            ResourceCatalogSearchVO catalogSearchVO);

    /**
     * 三大库之：库内容查询
     */
    public List<ResourceConfigPO> queryLibResourceByCondition(Map<String, String> condtions);

}
