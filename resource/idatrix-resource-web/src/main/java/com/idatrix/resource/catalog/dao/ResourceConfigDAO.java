package com.idatrix.resource.catalog.dao;

import com.idatrix.resource.catalog.po.ResourceConfigPO;
import com.idatrix.resource.catalog.vo.request.ResourceCatalogSearchVO;
import com.idatrix.resource.portal.po.ResourceQueryPO;
import com.idatrix.resource.portal.vo.ResourceQueryRequestVO;
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
    ResourceConfigPO getConfigByResourceCodeAndRentId(@Param("rentId")Long rentId,
                                                      @Param("resourceCode") String resourceCode);

    List<ResourceConfigPO> getConfigByUser(String User);

    List<ResourceConfigPO> getAllResourceConfigByRentId(Long rentId);

    List<ResourceConfigPO> getByNameOrCodeAndRentId( @Param("rentId") Long rentID,@Param("name") String name,
            @Param("catalogCode") String catalogCode, @Param("seqNum") String seqNum);

    /*所有库的查询*/
    List<ResourceConfigPO> queryByCondition(Map<String, String> condtions);

    /*根据查询条件查询已上架的资源*/
    List<ResourceConfigPO> getPublishedResourcesByCondition(ResourceCatalogSearchVO catalogSearchVO);

    /*三大库之：库内容查询*/
    List<ResourceConfigPO> queryLibResourceByCondition(Map<String, String> condtions);

    /*获取所有有编辑过资源的租户ID 列表*/
    List<Long> getResourceRentList();


    /*门户里面进行查询*/
    List<ResourceQueryPO> queryPortalResourceByCondition(ResourceQueryRequestVO requestVO);

    /*获取所有资源列表*/
    List<ResourceConfigPO> getResourceIdList(@Param("status")String status);


    /**
     * added by zhoujian on 20190102
     * 查询租户下所有状态=pub_success的资源
     *
     * @param rentId
     * @return
     */
    List<ResourceConfigPO> getAllPublishedResourceByRentId(Long rentId);


    /**
     * added by zhoujian on 20190107
     * 查询租户下所有状态=pub_success的资源
     *
     * @return
     */
    List<ResourceConfigPO> getAllPublishedResource();


    /**
     * added by zhoujian on 20190107
     * 查询获取已发布资源的所有租户信息
     *
     * @return
     */
    List<Long> getRentersByPublishedResource();

}
