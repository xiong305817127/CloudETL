package com.idatrix.resource.basedata.service;

import com.idatrix.resource.basedata.vo.DictionaryVO;

import java.util.List;

/**
 * 数据字段服务：包含资源格式分类，资源格式，共享方式
 */
public interface IDictionaryService {

    /**
     *  增加字典记录
     * @param rentId 租户信息
     * @param user  用户名称
     * @param type 类型：CLASSIFY_DICT、TYPE_DICT、SHARE_DICT
     * @param dictionaryVOList  字典详细数据
     */
    void addDictionary(Long rentId, String user, String type, List<DictionaryVO> dictionaryVOList) throws Exception;

    void deleteDictionaryById(Long id, String deleteStatus) throws Exception;

    void deleteDictionaryById(Long[] ids, String deleteStatus) throws Exception;

    List<DictionaryVO> getAllDictionaryInfo(Long rentId, String type);

    List<DictionaryVO> getAllDictionaryInfoByParendId(Long rentId, Long id);

    List<DictionaryVO> getResourceAllDictionaryInfo(Long rentId,String user, String type);

    List<DictionaryVO> getResourceTypeDict(Long rentId,String user);

    List<DictionaryVO> getResourceShareDict(Long rentId,String user);

    void increaseDictUseCount(Long rentId, String type, String code);

    void decreaseDictUseCount(Long rentId, String type, String code);
}
