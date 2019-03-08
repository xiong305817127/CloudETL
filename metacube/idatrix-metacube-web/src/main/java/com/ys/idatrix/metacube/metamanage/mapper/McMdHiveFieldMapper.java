package com.ys.idatrix.metacube.metamanage.mapper;

import com.ys.idatrix.metacube.metamanage.domain.McMdHiveFieldPO;

public interface McMdHiveFieldMapper {
    int deleteByPrimaryKey(Long id);

    int insert(McMdHiveFieldPO record);

    int insertSelective(McMdHiveFieldPO record);

    McMdHiveFieldPO selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(McMdHiveFieldPO record);

    int updateByPrimaryKey(McMdHiveFieldPO record);
}