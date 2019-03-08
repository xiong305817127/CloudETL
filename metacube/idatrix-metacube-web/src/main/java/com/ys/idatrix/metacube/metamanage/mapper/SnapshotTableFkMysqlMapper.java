package com.ys.idatrix.metacube.metamanage.mapper;

import com.ys.idatrix.metacube.metamanage.domain.SnapshotTableFkMysql;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface SnapshotTableFkMysqlMapper {
    int deleteByPrimaryKey(Long id);

    int insert(SnapshotTableFkMysql record);

    int insertSelective(SnapshotTableFkMysql record);

    SnapshotTableFkMysql selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(SnapshotTableFkMysql record);

    int updateByPrimaryKey(SnapshotTableFkMysql record);

    void batchInsert(List<SnapshotTableFkMysql> snapshotFKList);

    List<SnapshotTableFkMysql> selectListByTableIdAndVersion(@Param("tableId") Long tableId, @Param("version") Integer version);
}