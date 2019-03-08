package com.ys.idatrix.metacube.metamanage.mapper;

import com.ys.idatrix.metacube.metamanage.domain.SnapshotMetadata;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface SnapshotMetadataMapper {
    int deleteByPrimaryKey(Long id);

    int insert(SnapshotMetadata record);

    int insertSelective(SnapshotMetadata record);

    SnapshotMetadata selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(SnapshotMetadata record);

    int updateByPrimaryKey(SnapshotMetadata record);

    /*查询快照列表接口*/
    List<SnapshotMetadata> getSnapshotMetadataByMetaId(Long metaId);

    // 根据 元数据id 和 版本号 查询一条元数据信息
    SnapshotMetadata selectByTableIdAndVersion(@Param("metadataId") Long metadataId, @Param("version") Integer version);
}