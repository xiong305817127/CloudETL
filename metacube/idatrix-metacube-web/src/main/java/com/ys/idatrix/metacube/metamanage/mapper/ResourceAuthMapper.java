package com.ys.idatrix.metacube.metamanage.mapper;

import com.ys.idatrix.metacube.metamanage.domain.ResourceAuth;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ResourceAuthMapper {

    int deleteByPrimaryKey(Long id);

    int insert(ResourceAuth record);

    int insertSelective(ResourceAuth record);

    ResourceAuth selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(ResourceAuth record);

    int updateByPrimaryKey(ResourceAuth record);

    List<ResourceAuth> findAll();

    List<ResourceAuth> findByAuthType(@Param("authType") Integer authType);

    List<ResourceAuth> findByAuthNameAndAuthTypes(@Param("authName") String authName, @Param("authTypes") List<Integer> authTypes);

    /**
     * 获取所有权限值的和
     * @return
     */
    List<Integer> getAllAuthValue();
}