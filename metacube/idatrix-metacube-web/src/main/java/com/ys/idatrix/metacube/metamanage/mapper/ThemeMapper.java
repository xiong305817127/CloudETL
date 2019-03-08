package com.ys.idatrix.metacube.metamanage.mapper;

import com.ys.idatrix.metacube.metamanage.domain.Theme;
import com.ys.idatrix.metacube.metamanage.vo.request.SearchVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ThemeMapper {
    int deleteByPrimaryKey(Long id);

    int insert(Theme record);

    int insertSelective(Theme record);

    Theme selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(Theme record);

    int updateByPrimaryKey(Theme record);

    // 根据实体类查询
    List<Theme> search(SearchVO searchVo);

    // 批量删除主题
    int delete(List<Long> idList);

    // 根据 name 或 code 查询主题
    int findByNameOrCode(@Param("id") Long id, @Param("name") String name, @Param("code") String code);

    // 使用次数递增
    int progressiveIncrease(@Param("id") Long id);

    // 使用次数递减
    int decreaseProgressively(@Param("id") Long id);
}