package com.ys.idatrix.metacube.metamanage.mapper;

import com.ys.idatrix.metacube.metamanage.domain.McSchemaPO;
import com.ys.idatrix.metacube.metamanage.vo.request.SchemaSearchVO;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface McSchemaMapper {

    int insert(McSchemaPO schemaPO);

    int update(McSchemaPO schemaPO);

    int count(@Param("dbId") Long dbId, @Param("schemaName") String schemaName);

    McSchemaPO getSchemaById(@Param("id") Long id);

    List<McSchemaPO> listByPage(SchemaSearchVO searchVO);

    McSchemaPO findById(Long schemaId);

    List<McSchemaPO> querySchemaInfo(@Param("creator") String creator,
            @Param("databaseType") Integer databaseType,
            @Param("orgCode") String orgCode);


    McSchemaPO findByDbIdAndSchemaName(@Param("dbId") Long dbId,
            @Param("schemaName") String schemaName);

    /**
     * 根据数据库id列表查找模式
     *
     * @param dbIds 数据库id列表
     */
    List<McSchemaPO> listSchemaByDatabaseIds(@Param("dbIds") List<Long> dbIds);

    /**
     * 根据模式id列表查找模式
     *
     * @param ids 数据库id列表
     * @param renterId 租户id
     * @param ip 服务器ip
     */
    List<McSchemaPO> listSchemaBySchemaIds(@Param("ids") List<Long> ids,
            @Param("renterId") Long renterId, @Param("ip") String ip,
            @Param("databaseTypes") List<Integer> databaseTypes);

    /**
     * 获取模式列表
     *
     * @param orgCode 组织编码
     * @param renterId 租户id
     * @param dbTypeList 数据库类型
     */
    List<McSchemaPO> listSchema(@Param("orgCode") String orgCode,
            @Param("renterId") Long renterId,
            @Param("dbTypeList") List<Integer> dbTypeList,
            @Param("ip") String ip);

    /**
     * 返回目录列表
     *
     * @param pathList 目录列表
     * @param renterId 租户id
     */
    List<McSchemaPO> listDirectory(@Param("pathList") List<String> pathList,
            @Param("renterId") Long renterId);
}
