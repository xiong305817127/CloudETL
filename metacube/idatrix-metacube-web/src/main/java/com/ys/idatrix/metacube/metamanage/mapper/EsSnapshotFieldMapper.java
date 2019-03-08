package com.ys.idatrix.metacube.metamanage.mapper;

import com.ys.idatrix.metacube.metamanage.domain.EsSnapshotFieldPO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @ClassName: EsSnapshotFieldMapper
 * @Description: ES 索引字段表快照
 * @Author: ZhouJian
 * @Date: 2019/1/28
 */
public interface EsSnapshotFieldMapper {

    /**
     * 根据主键查询
     *
     * @param id
     * @return
     */
    EsSnapshotFieldPO selectByPK(Long id);

    /**
     * 根据主键删除
     *
     * @param id
     * @return
     */
    int deleteByPK(Long id);

    /**
     * 新增
     *
     * @param esSnapshotFieldPO
     * @return
     */
    int insert(EsSnapshotFieldPO esSnapshotFieldPO);


    /**
     * 新增 - 不定参数
     *
     * @param esSnapshotFieldPO
     * @return
     */
    int insertSelective(EsSnapshotFieldPO esSnapshotFieldPO);


    /**
     * 批量新增
     *
     * @param esSnapshotFieldPOList
     * @return
     */
    int batchInsert(List<EsSnapshotFieldPO> esSnapshotFieldPOList);


    /**
     * 根据索引表主键及版本查询快照
     *
     * @param metadataId
     * @param version
     * @return
     */
    List<EsSnapshotFieldPO> selectByMetaIdAndVersion(@Param("metadataId") Long metadataId, @Param("version") Integer version);

}
