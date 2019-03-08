package com.ys.idatrix.metacube.metamanage.mapper;

import com.ys.idatrix.metacube.metamanage.domain.TableChOracle;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface TableChOracleMapper {
    int deleteByPrimaryKey(Long id);

    int insert(TableChOracle record);

    int insertSelective(TableChOracle record);

    TableChOracle selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(TableChOracle record);

    int updateByPrimaryKey(TableChOracle record);

    // 不定参数查询
    int find(TableChOracle check);

    // 查询当前表检查约束最大的位置
    int selectMaxLocationByTableId(Long tableId);

    // 根据表id查询检查约束列表
    List<TableChOracle> findByTableId(Long tableId);

    // 逻辑删除
    void delete(Long id);

    // 根据表ID删除
    void deleteByTableId(@Param("tableId") Long tableId);
}