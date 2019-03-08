package com.ys.idatrix.metacube.metamanage.mapper;

import com.ys.idatrix.metacube.metamanage.domain.TableColumn;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface TableColumnMapper {
    int deleteByPrimaryKey(Long id);

    int insert(TableColumn record);

    int insertSelective(TableColumn record);

    TableColumn selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(TableColumn record);

    int updateByPrimaryKey(TableColumn record);

    // 不确定参数查询表字段
    int findByTableColumn(TableColumn tableColumn);

    // 根据表id查询表字段
    List<TableColumn> findTableColumnListByTableId(@Param("tableId") Long tableId);

    int delete(@Param("id") Long id);

    int deleteByTableId(@Param("tableId") Long tableId);

    // 查询当前表所属字段最大的位置
    int selectMaxLocationByTableId(@Param("tableId") Long tableId);
    
    //通过表ID和字段名,查询字段信息
    TableColumn selectByTableAndName(@Param("tableId") Long tableId , @Param("columnName") String columnName);

    // 根据id列表查询字段
    List<TableColumn> getTableColumnListByIdList(@Param("idList") List<String> idList);
}