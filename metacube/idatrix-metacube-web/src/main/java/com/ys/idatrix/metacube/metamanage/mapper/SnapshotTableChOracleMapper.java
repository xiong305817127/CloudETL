package com.ys.idatrix.metacube.metamanage.mapper;

import com.ys.idatrix.metacube.metamanage.domain.SnapshotTableChOracle;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface SnapshotTableChOracleMapper {
    int deleteByPrimaryKey(Long id);

    int insert(SnapshotTableChOracle record);

    int insertSelective(SnapshotTableChOracle record);

    SnapshotTableChOracle selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(SnapshotTableChOracle record);

    int updateByPrimaryKey(SnapshotTableChOracle record);

    // 批量新增
    void batchInsert(List<SnapshotTableChOracle> snapshotCheckList);

    List<SnapshotTableChOracle> selectByTableIdAndVersion(@Param("tableId") Long tableId, @Param("versions") Integer version);
}