package com.ys.idatrix.metacube.metamanage.mapper;

import com.ys.idatrix.metacube.metamanage.domain.ViewDetail;
import org.apache.ibatis.annotations.Param;

public interface ViewDetailMapper {
    int deleteByPrimaryKey(Long id);

    int insert(ViewDetail record);

    int insertSelective(ViewDetail record);

    ViewDetail selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(ViewDetail record);

    int updateByPrimaryKey(ViewDetail record);

    // 根据视图 id 删除
    void deleteByViewId(@Param("viewId") Long viewId);

    // 根据视图 id 获取视图详情
    ViewDetail findByViewId(@Param("viewId") Long viewId);
}