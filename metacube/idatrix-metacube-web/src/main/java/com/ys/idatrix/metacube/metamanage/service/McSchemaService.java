package com.ys.idatrix.metacube.metamanage.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.ys.idatrix.metacube.api.beans.PageResultBean;
import com.ys.idatrix.metacube.metamanage.domain.McSchemaPO;
import com.ys.idatrix.metacube.metamanage.mapper.McSchemaMapper;
import com.ys.idatrix.metacube.metamanage.vo.request.SchemaSearchVO;
import java.util.List;

/**
 * 模式接口抽象 提供了模式操作的一些默认实现
 */
public interface McSchemaService {

    /**
     * 获取McSchemaMapper数据访问接口，实现类需注入依赖
     */
    McSchemaMapper getSchemaMapper();

    /**
     * 新建模式 新建需要生成物理模式（表、视图、hdfs目录、es索引等）
     */
    McSchemaPO create(McSchemaPO schemaPO);

    /**
     * 注册模式 只在模式表新增记录
     */
    McSchemaPO register(McSchemaPO schemaPO);

    /**
     * 模式表中插入一条记录
     */
    default McSchemaPO insert(McSchemaPO schemaPO) {
        checkUniqueness(schemaPO);
        getSchemaMapper().insert(schemaPO);
        return schemaPO;
    }

    /**
     * 修改模式信息 模式名称不能修改
     */
    default McSchemaPO update(McSchemaPO schemaPO) {
        getSchemaMapper().update(schemaPO);
        return schemaPO;
    }

    /**
     * 模式列表
     */
    default PageResultBean<List<McSchemaPO>> listByPage(SchemaSearchVO searchVO) {
        PageHelper.startPage(searchVO.getPageNum(), searchVO.getPageSize());
        List<McSchemaPO> schemaPOList = getSchemaMapper().listByPage(searchVO);
        PageInfo<McSchemaPO> info = new PageInfo<>(schemaPOList);
        return PageResultBean.of(searchVO.getPageNum(), info.getTotal(), schemaPOList);
    }

    /**
     * 根据数据库id列表返回模式列表
     *
     * @param dbIds 数据库id列表
     */
    default List<McSchemaPO> listSchemaByDatabaseIds(List<Long> dbIds) {
        return getSchemaMapper().listSchemaByDatabaseIds(dbIds);
    }

    /**
     * 根据模式id列表返回模式列表
     *
     * @param schemaIds 数据库id列表
     */
    default List<McSchemaPO> listSchemaBySchemaIds(List<Long> schemaIds) {
        return getSchemaMapper().listSchemaBySchemaIds(schemaIds);
    }

    default List<McSchemaPO> listSchema(String orgCode,
            Long renterId, List<Integer> dbTypeList) {
        return getSchemaMapper().listSchema(orgCode, renterId, dbTypeList);
    }

    /**
     * 模式详情
     *
     * @param id 模式id
     */
    default McSchemaPO getSchemaById(Long id) {
        return getSchemaMapper().getSchemaById(id);
    }

    /**
     * 删除模式 逻辑删除
     */
    default McSchemaPO delete(McSchemaPO schemaPO) {
        schemaPO.setIsDeleted(1);
        update(schemaPO);
        return schemaPO;
    }

    /**
     * 检查模式是否已存在
     *
     * @return 已存在返回true 否则返回false
     */
    default boolean exists(McSchemaPO schemaPO) {
        return getSchemaMapper().count(schemaPO.getDbId(), schemaPO.getName()) > 0;
    }

    /**
     * 检查唯一性
     */
    default void checkUniqueness(McSchemaPO schemaPO) {
        if (exists(schemaPO)) {
            throw new IllegalArgumentException("模式已存在");
        }
    }

    default McSchemaPO findById(Long schemaId) {
        return getSchemaMapper().findById(schemaId);
    }
}
