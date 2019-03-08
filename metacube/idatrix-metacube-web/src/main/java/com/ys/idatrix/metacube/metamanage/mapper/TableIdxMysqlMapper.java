package com.ys.idatrix.metacube.metamanage.mapper;

import com.ys.idatrix.metacube.metamanage.domain.TableIdxMysql;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface TableIdxMysqlMapper {
    int deleteByPrimaryKey(Long id);

    int insert(TableIdxMysql record);

    int insertSelective(TableIdxMysql record);

    TableIdxMysql selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(TableIdxMysql record);

    int updateByPrimaryKey(TableIdxMysql record);

    // 不定参数查询表索引
    int findByTableIdxMysql(TableIdxMysql index);

    // 根据 tableId 查询索引
    List<TableIdxMysql> findIndexListByTableId(@Param("tableId") Long tableId);

    // 根据 tableId 查询表索引关联的所有字段
    List<String> findIndexColumnIdsByTable(@Param("tableId") Long tableId);

    // 根据id进行逻辑删除
    int delete(@Param("id") Long id);

    // 根据 tableId 查询表所属索引最大的位置标识
    int findMaxLocationByTable(@Param("tableId") Long tableId);

    // 根据表id删除数据
    int deleteByTableId(@Param("tableId") Long tableId);

    // 查询当前表所属索引最大的位置
    int selectMaxLocationByTableId(Long tableId);
}