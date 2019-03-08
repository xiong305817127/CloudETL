package com.ys.idatrix.metacube.metamanage.mapper;

import com.ys.idatrix.metacube.metamanage.domain.SnapshotTableIdxOracle;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface SnapshotTableIdxOracleMapper {
    int deleteByPrimaryKey(Long id);

    int insert(SnapshotTableIdxOracle record);

    int insertSelective(SnapshotTableIdxOracle record);

    SnapshotTableIdxOracle selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(SnapshotTableIdxOracle record);

    int updateByPrimaryKey(SnapshotTableIdxOracle record);

    // 批量新增
    void batchInsert(List<SnapshotTableIdxOracle> snapshotIndexList);

    List<SnapshotTableIdxOracle> selectByTableIdAndVersion(@Param("tableId") Long tableId, @Param("versions") Integer versions);
}