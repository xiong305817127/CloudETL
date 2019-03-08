package com.ys.idatrix.metacube.metamanage.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.ys.idatrix.metacube.api.beans.PageResultBean;
import com.ys.idatrix.metacube.common.exception.MetaDataException;
import com.ys.idatrix.metacube.common.utils.UserUtils;
import com.ys.idatrix.metacube.metamanage.domain.Theme;
import com.ys.idatrix.metacube.metamanage.mapper.ThemeMapper;
import com.ys.idatrix.metacube.metamanage.service.ThemeService;
import com.ys.idatrix.metacube.metamanage.vo.request.SearchVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Administrator on 2019/1/15.
 */
@Slf4j
@Transactional
@Service
public class ThemeServiceImpl implements ThemeService {

    @Autowired
    private ThemeMapper themeMapper;

    @Override
    public PageResultBean<Theme> search(SearchVO searchVo) {
        searchVo.setRenterId(UserUtils.getRenterId());
        PageHelper.startPage(searchVo.getPageNum(), searchVo.getPageSize());// 分页
        List<Theme> list = themeMapper.search(searchVo); // 查询
        PageInfo<Theme> pageInfo = new PageInfo<>(list);
        // 封装参数
        PageResultBean<Theme> result = PageResultBean.builder(pageInfo.getTotal(), list, searchVo.getPageNum(), searchVo.getPageSize());
        return result;
    }

    @Override
    public List<Theme> findThemeList() {
        SearchVO searchVo = new SearchVO();
        searchVo.setRenterId(UserUtils.getRenterId());
        List<Theme> list = themeMapper.search(searchVo);
        return list;
    }

    @Override
    public int add(Theme theme) {
        // 参数校验
        validated(null, theme.getName(), theme.getThemeCode());
        // 补全参数
        theme.setRenterId(UserUtils.getRenterId());
        theme.setUseCount(0);
        String username = UserUtils.getUserName();
        theme.setCreator(username);
        theme.setModifier(username);
        Date now = new Date();
        theme.setCreateTime(now);
        theme.setModifyTime(now);
        theme.setIsDeleted(false);
        // insert
        int count = themeMapper.insertSelective(theme);
        return count;
    }

    @Override
    public void update(Theme theme) {
        // 参数校验
        validated(theme.getId(), theme.getName(), theme.getThemeCode());
        // 补全参数
        String username = UserUtils.getUserName();
        theme.setModifier(username);
        theme.setModifyTime(new Date());
        // update
        themeMapper.updateByPrimaryKeySelective(theme);
    }

    public void validated(Long id, String name, String code) {
        if (themeMapper.findByNameOrCode(id, name, null) > 0) {
            throw new MetaDataException("500", "当前主题名已被占用");
        }
        if (themeMapper.findByNameOrCode(id, null, code) > 0) {
            throw new MetaDataException("500", "当前主题代码已被占用");
        }
    }

    @Override
    public int delete(String ids) {
        String[] idArr = ids.split(",");
        List<Long> idList = new ArrayList<>();
        // 判断元数据是否使用了这些主题
        for (String id : idArr) {
            Long themeId = Long.parseLong(id);
            Theme theme = themeMapper.selectByPrimaryKey(themeId);
            if (theme.getUseCount() > 0) {
                throw new MetaDataException("500", "当前所选主题中，已有被使用的主题，无法删除，请先修改相关元数据的主题。");
            }
            idList.add(themeId);
        }
        // delete
        int count = themeMapper.delete(idList);
        return count;
    }

    @Override
    public int increaseProgressively(Long themeId) {
        return themeMapper.progressiveIncrease(themeId);
    }

    @Override
    public int decreaseProgressively(Long themeId) {
        return themeMapper.decreaseProgressively(themeId);
    }
}
