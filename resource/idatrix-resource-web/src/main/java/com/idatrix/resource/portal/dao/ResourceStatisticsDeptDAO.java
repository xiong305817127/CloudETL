package com.idatrix.resource.portal.dao;

import com.idatrix.resource.portal.po.ResourceStatisticsDeptPO;
import org.apache.ibatis.annotations.Param;

public interface ResourceStatisticsDeptDAO {
    int deleteByPrimaryKey(Long id);

    int insert(ResourceStatisticsDeptPO record);

    int insertSelective(ResourceStatisticsDeptPO record);

    ResourceStatisticsDeptPO selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(ResourceStatisticsDeptPO record);

    int updateByPrimaryKey(ResourceStatisticsDeptPO record);


    /*****************************手动增加DAO处理**********************************/
    ResourceStatisticsDeptPO getStatisticsDept(@Param("rentId")Long rentId,  @Param("deptId")Long deptId,
                                               @Param("resourceId")Long resourceId);
}