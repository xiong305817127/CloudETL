package com.ys.idatrix.metacube.metamanage.mapper;

import com.ys.idatrix.metacube.metamanage.domain.EsFieldPO;

import java.util.List;

/**
 * @ClassName: EsFieldMapper
 * @Description: ES 索引字段表
 * @Author: ZhouJian
 * @Date: 2019/1/23
 */
public interface EsFieldMapper {

    /**
     * 根据主键查询
     *
     * @param id
     * @return
     */
    EsFieldPO selectByPK(Long id);


    /**
     * 新增
     *
     * @param esFieldPO
     * @return
     */
    int insert(EsFieldPO esFieldPO);


    /**
     * 批量添加
     *
     * @param esFieldPOList
     * @return
     */
    int batchInsert(List<EsFieldPO> esFieldPOList);


    /**
     * 新增 - 不定参数
     *
     * @param esFieldPO
     * @return
     */
    int insertSelective(EsFieldPO esFieldPO);


    /**
     * 根据主键删除
     *
     * @param id
     * @return
     */
    int deleteByPK(Long id);


    /**
     * 根据索引id（外键）删除记录
     *
     * @param id
     * @return
     */
    int deleteByIndexId(Long id);


    /**
     * 根据主键，不定值修改
     *
     * @param esFieldPO
     * @return
     */
    int updateByPrimaryKeySelective(EsFieldPO esFieldPO);


    /**
     * 根据索引id查询所有字段
     *
     * @param indexId
     * @return
     */
    List<EsFieldPO> queryFieldsByIndexId(Long indexId);

}
