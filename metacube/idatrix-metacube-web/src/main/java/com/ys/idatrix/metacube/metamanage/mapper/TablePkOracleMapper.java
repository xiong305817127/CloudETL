package com.ys.idatrix.metacube.metamanage.mapper;

import com.ys.idatrix.metacube.metamanage.domain.TablePkOracle;
import org.apache.ibatis.annotations.Param;

public interface TablePkOracleMapper {
    int deleteByPrimaryKey(Long id);

    int insert(TablePkOracle record);

    int insertSelective(TablePkOracle record);

    TablePkOracle selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(TablePkOracle record);

    int updateByPrimaryKey(TablePkOracle record);

    int find(TablePkOracle pk);

    // 根据表ID查询主键
    TablePkOracle findByTableId(@Param("tableId") Long tableId);

    // 根据表ID删除
    void deleteByTableId(@Param("tableId") Long tableId);
}