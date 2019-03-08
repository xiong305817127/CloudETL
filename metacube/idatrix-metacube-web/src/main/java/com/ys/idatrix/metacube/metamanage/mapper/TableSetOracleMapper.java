package com.ys.idatrix.metacube.metamanage.mapper;

import com.ys.idatrix.metacube.metamanage.domain.TableSetOracle;
import org.apache.ibatis.annotations.Param;

public interface TableSetOracleMapper {
    int deleteByPrimaryKey(Long id);

    int insert(TableSetOracle record);

    int insertSelective(TableSetOracle record);

    TableSetOracle selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(TableSetOracle record);

    int updateByPrimaryKey(TableSetOracle record);

    // 根据表id查询表设置信息
    TableSetOracle findByTableId(Long tableId);

    // 根据表ID删除
    void deleteByTableId(@Param("tableId") Long tableId);
}