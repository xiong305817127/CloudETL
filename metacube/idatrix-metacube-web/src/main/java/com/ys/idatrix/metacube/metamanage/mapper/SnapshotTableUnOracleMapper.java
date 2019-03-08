package com.ys.idatrix.metacube.metamanage.mapper;

import com.ys.idatrix.metacube.metamanage.domain.SnapshotTableUnOracle;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface SnapshotTableUnOracleMapper {
    int deleteByPrimaryKey(Long id);

    int insert(SnapshotTableUnOracle record);

    int insertSelective(SnapshotTableUnOracle record);

    SnapshotTableUnOracle selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(SnapshotTableUnOracle record);

    int updateByPrimaryKey(SnapshotTableUnOracle record);

    void batchInsert(List<SnapshotTableUnOracle> snapshotUniqueList);

    List<SnapshotTableUnOracle> selectByTableIdAndVersion(@Param("tableId") Long tableId, @Param("versions") Integer versions);
}