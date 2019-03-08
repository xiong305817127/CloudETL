package com.ys.idatrix.metacube.metamanage.mapper;

import com.ys.idatrix.metacube.metamanage.domain.SnapshotViewDetail;
import org.apache.ibatis.annotations.Param;

public interface SnapshotViewDetailMapper {
    int deleteByPrimaryKey(Long id);

    int insert(SnapshotViewDetail record);

    int insertSelective(SnapshotViewDetail record);

    SnapshotViewDetail selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(SnapshotViewDetail record);

    int updateByPrimaryKey(SnapshotViewDetail record);

    SnapshotViewDetail findByViewIdAndVersion(@Param("viewId") Long viewId, @Param("version") Integer version);
}