package com.ys.idatrix.metacube.metamanage.mapper;

import com.ys.idatrix.metacube.metamanage.domain.SnapshotTableColumn;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface SnapshotTableColumnMapper {
    int deleteByPrimaryKey(Long id);

    int insert(SnapshotTableColumn record);

    int insertSelective(SnapshotTableColumn record);

    SnapshotTableColumn selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(SnapshotTableColumn record);

    int updateByPrimaryKey(SnapshotTableColumn record);

    void batchInsert(List<SnapshotTableColumn> snapshotColList);

    // 根据 tableId 和版本号查询某个版本中表所属的字段信息
    List<SnapshotTableColumn> selectListByTableIdAndVersion(@Param("tableId") Long tableId, @Param("version") Integer version);

    // 根据字段id和版本确定一条数据
    SnapshotTableColumn findByColumnIdAndVersion(@Param("columnId") Long columnId, @Param("version") Integer version);
}