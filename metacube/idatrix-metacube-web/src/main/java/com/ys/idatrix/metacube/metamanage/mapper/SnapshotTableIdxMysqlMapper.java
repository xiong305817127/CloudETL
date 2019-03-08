package com.ys.idatrix.metacube.metamanage.mapper;

import com.ys.idatrix.metacube.metamanage.domain.SnapshotTableIdxMysql;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface SnapshotTableIdxMysqlMapper {
    int deleteByPrimaryKey(Long id);

    int insert(SnapshotTableIdxMysql record);

    int insertSelective(SnapshotTableIdxMysql record);

    SnapshotTableIdxMysql selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(SnapshotTableIdxMysql record);

    int updateByPrimaryKey(SnapshotTableIdxMysql record);

    void batchInsert(List<SnapshotTableIdxMysql> snapshotIdxList);

    // 根据 tableId 和版本号查询某个版本中表所属的索引信息
    List<SnapshotTableIdxMysql> selectListByTableIdAndVersion(@Param("tableId") Long tableId,
                                                              @Param("version") Integer version);
}