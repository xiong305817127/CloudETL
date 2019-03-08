package com.ys.idatrix.metacube.metamanage.mapper;

import com.ys.idatrix.metacube.metamanage.domain.TableIdxOracle;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface TableIdxOracleMapper {
    int deleteByPrimaryKey(Long id);

    int insert(TableIdxOracle record);

    int insertSelective(TableIdxOracle record);

    TableIdxOracle selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(TableIdxOracle record);

    int updateByPrimaryKey(TableIdxOracle record);

    // 不定参数查询
    int find(TableIdxOracle index);

    // 查询当前表对应最大的索引位置
    int selectMaxLocationByTableId(Long tableId);

    // 根据表id查询索引列表
    List<TableIdxOracle> findByTableId(@Param("tableId") Long tableId);

    // 逻辑删除索引
    void delete(Long id);

    // 根据表ID删除
    void deleteByTableId(@Param("tableId") Long tableId);
}