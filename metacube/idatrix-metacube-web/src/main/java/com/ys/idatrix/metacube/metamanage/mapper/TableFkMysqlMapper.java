package com.ys.idatrix.metacube.metamanage.mapper;

import com.ys.idatrix.metacube.metamanage.domain.TableFkMysql;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface TableFkMysqlMapper {
    int deleteByPrimaryKey(Long id);

    int insert(TableFkMysql record);

    int insertSelective(TableFkMysql record);

    TableFkMysql selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(TableFkMysql record);

    int updateByPrimaryKey(TableFkMysql record);

    // 不固定参数查询
    int findByTableFkMysql(TableFkMysql tableFkMysql);

    // 查询表外键信息
    List<TableFkMysql> findListByTableId(@Param("tableId") Long tableId);

    // 逻辑删除
    int delete(@Param("id") Long id);

    // 根据tableId删除表
    int deleteByTableId(@Param("tableId") Long tableId);

    // 查询当前表所属索引最大的位置
    int selectMaxLocationByTableId(Long tableId);
}