package com.ys.idatrix.metacube.metamanage.mapper;

import com.ys.idatrix.metacube.metamanage.domain.SnapshotTableFkOracle;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface SnapshotTableFkOracleMapper {
    int deleteByPrimaryKey(Long id);

    int insert(SnapshotTableFkOracle record);

    int insertSelective(SnapshotTableFkOracle record);

    SnapshotTableFkOracle selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(SnapshotTableFkOracle record);

    int updateByPrimaryKey(SnapshotTableFkOracle record);

    // 批量新增
    void batchInsert(List<SnapshotTableFkOracle> snapshotForeignKeyList);

    List<SnapshotTableFkOracle> selectByTableIdAndVersion(@Param("tableId") Long tableId, @Param("versions") Integer versions);
}