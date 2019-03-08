package com.ys.idatrix.metacube.metamanage.mapper;

import com.ys.idatrix.metacube.api.beans.MetadataDTO;
import com.ys.idatrix.metacube.metamanage.domain.Metadata;
import com.ys.idatrix.metacube.metamanage.vo.request.MetadataSearchVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface MetadataMapper {

    int deleteByPrimaryKey(Long id);

    int insert(Metadata record);

    int insertSelective(Metadata record);

    Metadata selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(Metadata record);

    int updateByPrimaryKey(Metadata record);

    //不定参数查询
    List<Metadata> search(MetadataSearchVo metadataVo);

    // 根据模式查询表
    List<Metadata> searchList(MetadataSearchVo metadataVo);

    /**
     * 查询所属组织、指定数据库类型下的生效的元数据
     *
     * @param databaseTypes
     * @param deptCode
     * @return
     */
    List<Metadata> searchByDeptAndDbTypes(@Param("databaseTypes") List<Integer> databaseTypes,
                                          @Param("deptCode") String deptCode);

    // 根据主题查询元数据
    int findMetadataByThemeId(@Param("themeId") Long themeId);

    // 用于校验元数据
    int findByMetadata(Metadata metadata);

    // 根据id查询数据
    Metadata findById(@Param("id") Long id);

    /**
     * 根据存储参数唯一性校验
     *
     * @param schemaId       模式ID
     * @param name           名称
     * @param identification 其它配置
     * @return
     */
    List<Metadata> queryMetaData(@Param("schemaId") Long schemaId,
                                 @Param("name") String name,
                                 @Param("identification") String identification);

    /**
     * 根据查询条件进行数据搜索
     *
     * @param dataVO
     * @return
     */
    List<Metadata> queryMetaDataBySearchVO(MetadataSearchVo dataVO);

    /*根据ID获取HDFS全路径*/
    String getMetaDefHDFSFullDir(@Param("id") Long id);

    // 根据id逻辑删除元数据
    int delete(@Param("id") Long id);


    /**
     * 查询HDFS所有全路径地址
     *
     * @return
     */
    List<Metadata> getAllHDFSFolderInfo(@Param("user") String user,
                                        @Param("orgCode") String orgCode,
                                        @Param("pathPrefix") String pathPrefix);


    /**
     * 查询用户所操作的资源
     *
     * @param orgCode
     * @param schemaId
     * @param status
     * @param databaseTypes
     * @return
     */
    List<Metadata> getAllMetadataByUser(@Param("orgCode") String orgCode,
                                        @Param("schemaId") Long schemaId,
                                        @Param("status") Integer status,
                                        @Param("databaseTypes") List<Integer> databaseTypes);


    // 根据一系列数据来确定一条元数据信息
    MetadataDTO findByDatabaseInfo(@Param("ip") String ip,
                                   @Param("databaseType") int databaseType,
                                   @Param("serviceName") String serviceName,
                                   @Param("schemaName") String schemaName,
                                   @Param("tableName") String tableName,
                                   @Param("resourceType") int resourceType,
                                   @Param("renterId") Long renterId);

    // 根据 schemaId 获取元数据信息
    List<MetadataDTO> findListBySchemaIdAndResourceType(@Param("schemaId") Long schemaId,
                                                        @Param("resourceType") int resourceType);

    // 根据 id集合 查询
    List<Metadata> findByIdList(@Param("idList") List<Long> idList);
}