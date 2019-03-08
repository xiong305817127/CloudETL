package com.idatrix.resource.catalog.dao;

import com.idatrix.resource.catalog.po.DeptLimitedPO;

import java.util.List;

/**
 *  资源-部门限定表
 *  资源可哪些部门订阅，无条件共享及不共享不需要关联。每条关系一条数据。
 */

public interface DeptLimitedDAO {

    public void insert(DeptLimitedPO deptLimitedPO);

    public void deleteById(Long id);

    public void deleteByResourceId(Long resourceId);

    /*按照数据资源查询能够开放哪些部门*/
    public List<DeptLimitedPO> getByResourceId(Long resoureId);

    /*按照部门id后查询开放了哪些资源*/
    public List<DeptLimitedPO> getByDeptId(Long deptId);

    public int updateById(DeptLimitedPO rsPO);

    public Long[] getDeptArrayByResource(Long resourceId);
}
