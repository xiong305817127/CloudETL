package com.idatrix.resource.basedata.dao;

import com.idatrix.resource.basedata.po.DictionaryPO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Created by Administrator on 2018/12/13.
 */
public interface DictionaryDAO {

    List<DictionaryPO> getByRentIdAndType(@Param("rentId")Long rentId,
                                          @Param("type")String type);

    List<DictionaryPO> getSameDict(
            @Param("rentId") Long rentId, @Param("type") String type,
                                   @Param("name") String name,
                                   @Param("code") String code,
                                   @Param("typeParentId")Long typeParentId);


    List<DictionaryPO> getSameCodeOrNameDict(
            @Param("rentId") Long rentId, @Param("type") String type,
            @Param("name") String name,
            @Param("code") String code,
            @Param("typeParentId")Long typeParentId);

    List<DictionaryPO> getByParentTypeId(Long parentTypeId);

    DictionaryPO getById(Long id);

    DictionaryPO getByTypeAndCode(@Param("rentId")Long rentId,
                                  @Param("type")String type,
                                  @Param("code") String code);


    Long insert(DictionaryPO dictionaryPO);

    void deleteById(Long id);

    void deleteByParentTypeId(Long parentTypeId);

    void updateById(DictionaryPO dictionaryPO);
}
