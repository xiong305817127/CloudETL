package com.ys.idatrix.metacube.metamanage.mapper;

import com.ys.idatrix.metacube.metamanage.domain.TableFkOracle;

import java.util.List;

public interface TableFkOracleMapper {
    int deleteByPrimaryKey(Long id);

    int insert(TableFkOracle record);

    int insertSelective(TableFkOracle record);

    TableFkOracle selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(TableFkOracle record);

    int updateByPrimaryKey(TableFkOracle record);

    // 不定参数查询
    int find(TableFkOracle foreignKey);

    // 查询表下外键最大的位置
    int selectMaxLocationByTableId(Long tableId);

    // 根据表id查询外键列表
    List<TableFkOracle> findByTableId(Long tableId);

    // 逻辑删除
    void delete(Long id);

    // 根据表ID删除
    void deleteByTableId(Long tableId);
}