package com.ys.idatrix.metacube.metamanage.mapper;

import com.ys.idatrix.metacube.metamanage.domain.SnapshotTablePkOracle;
import org.apache.ibatis.annotations.Param;

public interface SnapshotTablePkOracleMapper {
    int deleteByPrimaryKey(Long id);

    int insert(SnapshotTablePkOracle record);

    int insertSelective(SnapshotTablePkOracle record);

    SnapshotTablePkOracle selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(SnapshotTablePkOracle record);

    int updateByPrimaryKey(SnapshotTablePkOracle record);

    // 根据版本号和表id确定一条数据
    SnapshotTablePkOracle selectByTableIdAndVersion(@Param("tableId") Long tableId, @Param("versions") Integer versions);
}