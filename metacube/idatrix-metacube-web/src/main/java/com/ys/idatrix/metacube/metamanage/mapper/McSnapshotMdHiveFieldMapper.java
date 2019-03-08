package com.ys.idatrix.metacube.metamanage.mapper;

import com.ys.idatrix.metacube.metamanage.domain.McSnapshotMdHiveFieldPO;

public interface McSnapshotMdHiveFieldMapper {

    int deleteByPrimaryKey(Long id);

    int insert(McSnapshotMdHiveFieldPO record);

    int insertSelective(McSnapshotMdHiveFieldPO record);

    McSnapshotMdHiveFieldPO selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(McSnapshotMdHiveFieldPO record);

    int updateByPrimaryKey(McSnapshotMdHiveFieldPO record);
}