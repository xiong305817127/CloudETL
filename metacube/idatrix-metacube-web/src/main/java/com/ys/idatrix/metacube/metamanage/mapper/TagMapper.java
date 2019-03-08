package com.ys.idatrix.metacube.metamanage.mapper;

import com.ys.idatrix.metacube.metamanage.domain.Tag;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface TagMapper {
    int deleteByPrimaryKey(Long id);

    int insert(Tag record);

    int insertSelective(Tag record);

    Tag selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(Tag record);

    int updateByPrimaryKey(Tag record);

    // 不定参数查询
    int findByTag(Tag tag);

    // 根据用户查询标签列表
    List<Tag> findTagListByUserId(@Param("userId") Long userId);

    // 根据租户查询标签列表
    List<String> findTagListByRenterId(@Param("renterId") Long renterId);
}