package com.ys.idatrix.metacube.sysmanage.service;

import com.ys.idatrix.metacube.api.beans.PageResultBean;
import com.ys.idatrix.metacube.metamanage.domain.Theme;
import com.ys.idatrix.metacube.metamanage.vo.request.SearchVO;

import java.util.List;

/**
 * Created by Administrator on 2019/1/15.
 */
public interface ThemeService {

    // 查询
    PageResultBean<Theme> search(SearchVO searchVo);

    // 查询当前租户下的主题列表
    List<Theme> findThemeList();

    // 新增
    int add(Theme theme);

    // 修改
    void update(Theme theme);

    // 根据ids删除主题
    int delete(String ids);

    // 递增
    int increaseProgressively(Long themeId);

    // 递减
    int decreaseProgressively(Long themeId);
}
