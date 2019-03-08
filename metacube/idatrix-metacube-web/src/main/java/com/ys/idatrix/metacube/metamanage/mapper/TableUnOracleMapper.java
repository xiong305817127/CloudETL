package com.ys.idatrix.metacube.metamanage.mapper;

import com.ys.idatrix.metacube.metamanage.domain.TableUnOracle;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface TableUnOracleMapper {
    int deleteByPrimaryKey(Long id);

    int insert(TableUnOracle record);

    int insertSelective(TableUnOracle record);

    TableUnOracle selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(TableUnOracle record);

    int updateByPrimaryKey(TableUnOracle record);

    // 不定参数查询
    int find(TableUnOracle unique);

    // 查询当前表最大的位置
    int selectMaxLocationByTableId(@Param("tableId") Long tableId);

    // 根据表id查询唯一约束列表
    List<TableUnOracle> findByTableId(@Param("tableId") Long tableId);
    
    // 根据表id和约束名称查询唯一约束
    TableUnOracle findByTableIdAndName(@Param("tableId") Long tableId,@Param("name") String name);

    // 逻辑删除
    void delete(Long id);

    // 根据表ID删除
    void deleteByTableId(@Param("tableId") Long tableId);
}