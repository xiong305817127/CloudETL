package com.ys.idatrix.metacube.metamanage.mapper;

import com.ys.idatrix.metacube.metamanage.domain.EsSnapshotMetadata;
import com.ys.idatrix.metacube.metamanage.domain.SnapshotMetadata;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @ClassName: EsSnapshotMetadataMapper
 * @Description: ES 索引主表快照
 * @Author: ZhouJian
 * @Date: 2019/1/28
 */
public interface EsSnapshotMetadataMapper {

    /**
     * 删除快照
     *
     * @param id
     * @return
     */
    int deleteByPrimaryKey(Long id);

    /**
     * 添加
     *
     * @param record
     * @return
     */
    int insert(EsSnapshotMetadata record);

    /**
     * 选择性添加
     *
     * @param record
     * @return
     */
    int insertSelective(EsSnapshotMetadata record);

    /**
     * 根据主键查询
     *
     * @param id
     * @return
     */
    EsSnapshotMetadata selectByPrimaryKey(Long id);

    /**
     * 根据主键修改
     *
     * @param record
     * @return
     */
    int updateByPrimaryKey(EsSnapshotMetadata record);

    /**
     * 根据主键不定字段修改
     *
     * @param record
     * @return
     */
    int updateByPrimaryKeySelective(EsSnapshotMetadata record);


    /**
     * 查询快照列表接口
     *
     * @param metaId
     * @return
     */
    List<EsSnapshotMetadata> getSnapshotMetadataByMetaId(Long metaId);

    /**
     * 根据 元数据id 和 版本号 查询一条元数据信息
     *
     * @param metadataId
     * @param version
     * @return
     */
    EsSnapshotMetadata selectByMetaIdAndVersion(@Param("metadataId") Long metadataId, @Param("version") Integer version);

}