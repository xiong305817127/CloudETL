package com.ys.idatrix.metacube.metamanage.mapper;

import com.ys.idatrix.metacube.metamanage.domain.EsMetadataPO;
import com.ys.idatrix.metacube.metamanage.vo.request.MetadataSearchVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @ClassName: EsMetadataMapper
 * @Description: ES 索引主表快照
 * @Author: ZhouJian
 * @Date: 2019/1/28
 */
public interface EsMetadataMapper {

    /**
     * 根据主键删除
     *
     * @param id
     * @return
     */
    int deleteByPrimaryKey(Long id);


    /**
     * 插入
     *
     * @param record
     * @return
     */
    int insert(EsMetadataPO record);


    /**
     * 选择性插入
     *
     * @param record
     * @return
     */
    int insertSelective(EsMetadataPO record);


    /**
     * 根据主键查询
     *
     * @param id
     * @return
     */
    EsMetadataPO selectByPrimaryKey(Long id);


    /**
     * 根据主键修改
     *
     * @param record
     * @return
     */
    int updateByPrimaryKey(EsMetadataPO record);


    /**
     * 根据主键选择性修改
     *
     * @param record
     * @return
     */
    int updateByPrimaryKeySelective(EsMetadataPO record);


    /**
     * 根据id逻辑删除元数据
     *
     * @param id
     * @return
     */
    int softDelete(@Param("id") Long id);


    /**
     * es 索引切换-更新版本
     *
     * @param id
     * @param version
     * @return
     */
    int switchVersion(@Param("id") Long id, @Param("version") Integer version);


    /**
     * es 索引启停
     *
     * @param id
     * @param status
     * @return
     */
    int updateIsOpen(@Param("id") Long id, @Param("isOpen") boolean status);


    /**
     * 查询
     *
     * @param metadataVo
     * @return
     */
    List<EsMetadataPO> search(MetadataSearchVo metadataVo);


    /**
     * 根据schema_id 查询 元数据信息名称，状态不等于2的
     *
     * @param schemaId
     * @return
     */
    List<EsMetadataPO> findBySchemaId(Long schemaId);


    /**
     * 不定参数查询中文名称记录数
     *
     * @param identification
     * @param schemaId
     * @param renterId
     * @return
     */
    Integer queryCntBySelectiveParam(@Param("identification") String identification,
                                     @Param("schemaId") Long schemaId, @Param("renterId") Long renterId);


    /**
     * 查询最大location
     *
     * @param id
     * @return
     */
    Integer findMaxLocation(Long id);


    /**
     * 根据模式ID查询最大version
     *
     * @param schemaId
     * @return
     */
    Integer findMaxVersion(Long schemaId);

    // 根据主题id查询元数据
    int findMetadataByThemeId(@Param("themeId") Long themeId);
}