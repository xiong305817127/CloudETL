package com.ys.idatrix.metacube.metamanage.mapper;

import com.ys.idatrix.metacube.metamanage.domain.SnapshotTableSetOracle;
import org.apache.ibatis.annotations.Param;

public interface SnapshotTableSetOracleMapper {
    int deleteByPrimaryKey(Long id);

    int insert(SnapshotTableSetOracle record);

    int insertSelective(SnapshotTableSetOracle record);

    SnapshotTableSetOracle selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(SnapshotTableSetOracle record);

    int updateByPrimaryKey(SnapshotTableSetOracle record);

    SnapshotTableSetOracle selectByTableIdAndVersion(@Param("tableId") Long tableId, @Param("versions") Integer versions);
}