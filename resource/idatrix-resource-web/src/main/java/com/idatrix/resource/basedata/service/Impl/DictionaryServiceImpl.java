package com.idatrix.resource.basedata.service.Impl;

import com.idatrix.resource.basedata.dao.DictionaryDAO;
import com.idatrix.resource.basedata.exception.DictionaryDeleteException;
import com.idatrix.resource.basedata.po.DictionaryPO;
import com.idatrix.resource.basedata.service.IDictionaryService;
import com.idatrix.resource.basedata.vo.DictionaryVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.idatrix.resource.basedata.common.BasedataConstant.CLASSIFY_DICT;
import static com.idatrix.resource.basedata.common.BasedataConstant.SHARE_DICT;
import static com.idatrix.resource.basedata.common.BasedataConstant.TYPE_DICT;

/**
 * 数据字段服务：包含资源格式分类，资源格式，共享方式
 */

@Slf4j
@Transactional
@Service("dictionaryService")
public class DictionaryServiceImpl implements IDictionaryService {

    @Autowired
    private DictionaryDAO dictionaryDAO;


    /**
     *  增加数据字典内容，页面删除行需要调用 后台删除接口
     * @param rentId 租户信息
     * @param user  用户名称
     * @param type 类型：CLASSIFY_DICT、TYPE_DICT、SHARE_DICT
     * @param dictionaryVOList  字典详细数据
     * @throws Exception
     */
    @Override
    public void addDictionary(Long rentId, String user, String type, List<DictionaryVO> dictionaryVOList)throws Exception {

        for(DictionaryVO dVo: dictionaryVOList){
            if(!StringUtils.equalsAnyIgnoreCase(type, dVo.getType())){
                continue;
            }
            if(!verifyParam(rentId, type, dVo)){
                throw new Exception("存在相同的字典记录，字典名称 "+dVo.getName()+
                " 字典编码 "+dVo.getCode());
            }
            if(!verifyPermissionForChangeDictCode(rentId, type, dVo)){
                throw new Exception("该字典编码不能修改，只可以根据实际情况修改成不重复名称");
            }
            Long parentId = dVo.getTypeParentId();
            if(BooleanUtils.isTrue(dVo.getUseFlag())){
                continue;
            }else if(parentId!=null && StringUtils.equalsIgnoreCase(type, TYPE_DICT) &&parentId.equals(0L)){
                continue;
            }else if(parentId!=null && !parentId.equals(0L) ){
                continue;
            }
            saveDict(rentId, user, type, dVo);
        }
    }

    /**
     * 对于资源分类、资源共享方式中编码采用数字，前端和后台里面有使用逻辑关系，不能随意变更编码
     * @param rentId
     * @param type
     * @param dictionaryVO
     * @return
     */
    private Boolean verifyPermissionForChangeDictCode(Long rentId, String type, DictionaryVO dictionaryVO){

        if(StringUtils.equalsAnyIgnoreCase(type, CLASSIFY_DICT) ||
                StringUtils.equalsAnyIgnoreCase(type, SHARE_DICT)){
            if(dictionaryVO.getId()>0L){
                List<DictionaryPO> poList = dictionaryDAO.getSameCodeOrNameDict(rentId, type, dictionaryVO.getName(),
                        dictionaryVO.getCode(), dictionaryVO.getTypeParentId());
                if(poList!=null&&poList.size()==1&&poList.get(0).getId().equals(dictionaryVO.getId())){
                    return true;
                }
            }
        }
        return false;

    }


    /**
     * 校验数据是否符合要求
     * @param vo
     * @return
     * @throws Exception
     */
    private Boolean verifyParam(Long rentId, String type, DictionaryVO vo)throws Exception{


        if(vo.getId()!=null&&vo.getId()>0L){
            if(vo.getUseFlag()){
                return true;
            }else{
                List<DictionaryPO> poList = dictionaryDAO.getSameDict(rentId, type, vo.getName(),vo.getCode(),vo.getTypeParentId());
                if(poList==null || poList.size()==0 || poList.size()==1) {
                    return true;
                }
            }
        }else{
            List<DictionaryPO> poList = dictionaryDAO.getSameDict(rentId, type, vo.getName(),vo.getCode(),vo.getTypeParentId());
            if(poList==null || poList.size()==0) {
                return true;
            }
        }
        return false;
    }

    private void verifyBeforeSave(DictionaryPO dPO){
        if(StringUtils.equals(dPO.getType(), TYPE_DICT)){
            if(dPO.getTypeParentId()==null || dPO.getTypeParentId()==0L){
                throw new RuntimeException("资源格式 "+dPO.getName()+" 字典没有配置 parent Id");
            }
        }
    }

    private void saveDict(Long rentId, String user, String type,DictionaryVO dVo){
        DictionaryPO dictPo = null;
        if(dVo.getId()==null || dVo.getId().equals(0L)){
            dictPo = new DictionaryPO(rentId, user, type, dVo);
            dictionaryDAO.insert(dictPo);
        }else{
            dictPo = dictionaryDAO.getById(dVo.getId());
            dictPo.setCode(dVo.getCode());
            dictPo.setName(dVo.getName());
            dictPo.setModifier(user);
            dictPo.setModifyTime(new Date());
            dictionaryDAO.updateById(dictPo);
        }
    }

    @Override
    public void deleteDictionaryById(Long[] ids, String deleteStatus) throws Exception {
        for(Long id :ids){
            deleteDictionaryById(id, deleteStatus);
        }
    }

    @Override
    public void deleteDictionaryById(Long id, String deleteStatus) throws Exception{
        DictionaryPO dictionaryPO = dictionaryDAO.getById(id);
        if(dictionaryPO==null){
            throw new Exception("不存在对应字典内容");
        }

        if(StringUtils.equalsIgnoreCase(dictionaryPO.getType(), CLASSIFY_DICT)){
            if(dictionaryPO.getUseCount()>0L){
                throw new Exception("该分类下资源格式已被使用，无法删除，请先修改相关资源的格式。");
            }
            List<DictionaryPO> dictList = dictionaryDAO.getByParentTypeId(id);
            if(dictList==null || dictList.size()==0){
                dictionaryDAO.deleteById(id);
            }else{
                if(StringUtils.equalsIgnoreCase("force", deleteStatus)){
                    for(DictionaryPO dPo:dictList){
                        if(dPo.getUseCount()>0L){
                            throw new Exception("该分类下资源格式已被使用，无法删除，请先修改相关资源的格式。");
                        }
                    }
                    dictionaryDAO.deleteByParentTypeId(id);
                }else{
                    throw new DictionaryDeleteException(2100, "是否删除该分类下的所有资源格式。");
                }
            }
            dictionaryDAO.deleteById(id);
        }
    }

    @Override
    public List<DictionaryVO> getAllDictionaryInfo(Long rentId, String type) {

        List<DictionaryPO> dictList = new ArrayList<>();
        if(StringUtils.equalsIgnoreCase(type, TYPE_DICT)){
            List<DictionaryPO> tmpList = dictionaryDAO.getByRentIdAndType(rentId, CLASSIFY_DICT);
            for(DictionaryPO po:tmpList){
                dictList.add(po);
                List<DictionaryPO> resultList = dictionaryDAO.getByParentTypeId(po.getId());
                if(resultList!=null && resultList.size()>0){
                    dictList.addAll(resultList);
                }
            }
        }else{
            dictList = dictionaryDAO.getByRentIdAndType(rentId, type);
        }
        return transferDictPOToVO(dictList);
    }

    @Override
    public List<DictionaryVO> getAllDictionaryInfoByParendId(Long rentId, Long id) {

        List<DictionaryPO> tmpList = dictionaryDAO.getByParentTypeId(id);
        return transferDictPOToVO(tmpList);
    }

    private void initDictionary(Long rentId, String user){

        DictionaryPO filePO = new DictionaryPO(rentId, user, CLASSIFY_DICT, "电子文件", "1", 0L);
        dictionaryDAO.insert(filePO);
        Long fileParentId = filePO.getId();
        dictionaryDAO.insert(new DictionaryPO(rentId, user, TYPE_DICT, "OFD", "OFD", fileParentId));
        dictionaryDAO.insert(new DictionaryPO(rentId, user, TYPE_DICT, "wps", "wps", fileParentId));
        dictionaryDAO.insert(new DictionaryPO(rentId, user, TYPE_DICT, "xml", "xml", fileParentId));
        dictionaryDAO.insert(new DictionaryPO(rentId, user, TYPE_DICT, "txt", "txt", fileParentId));
        dictionaryDAO.insert(new DictionaryPO(rentId, user, TYPE_DICT, "doc", "doc", fileParentId));
        dictionaryDAO.insert(new DictionaryPO(rentId, user, TYPE_DICT, "docx", "docx", fileParentId));
        dictionaryDAO.insert(new DictionaryPO(rentId, user, TYPE_DICT, "html", "html", fileParentId));
        dictionaryDAO.insert(new DictionaryPO(rentId, user, TYPE_DICT, "pdf", "pdf", fileParentId));
        dictionaryDAO.insert(new DictionaryPO(rentId, user, TYPE_DICT, "ppt", "ppt", fileParentId));
        dictionaryDAO.insert(new DictionaryPO(rentId, user, TYPE_DICT, "pptx", "pptx", fileParentId));

        DictionaryPO formPO = new DictionaryPO(rentId, user, CLASSIFY_DICT, "电子表格", "2", 0L);
        dictionaryDAO.insert(formPO);
        Long formParentId = formPO.getId();
        dictionaryDAO.insert(new DictionaryPO(rentId, user, TYPE_DICT, "et", "et", formParentId));
        dictionaryDAO.insert(new DictionaryPO(rentId, user, TYPE_DICT, "xls", "xls", formParentId));
        dictionaryDAO.insert(new DictionaryPO(rentId, user, TYPE_DICT, "xlsx", "xlsx", formParentId));

        DictionaryPO dbPO = new DictionaryPO(rentId, user, CLASSIFY_DICT, "数据库", "3", 0L);
        dictionaryDAO.insert(dbPO);
        Long dbParentId = dbPO.getId();
        dictionaryDAO.insert(new DictionaryPO(rentId, user, TYPE_DICT, "MySQL", "MySQL", dbParentId));
        dictionaryDAO.insert(new DictionaryPO(rentId, user, TYPE_DICT, "Oracle", "Oracle", dbParentId));
//        dictionaryDAO.insert(new DictionaryPO(rentId, user, TYPE_DICT, "DM", "DM", dbParentId));
//        dictionaryDAO.insert(new DictionaryPO(rentId, user, TYPE_DICT, "PostgreSQL", "PostgreSQL", dbParentId));

        DictionaryPO imagePO = new DictionaryPO(rentId, user, CLASSIFY_DICT, "图形图像", "4", 0L);
        dictionaryDAO.insert(imagePO);
        Long imageParentId = imagePO.getId();
        dictionaryDAO.insert(new DictionaryPO(rentId, user, TYPE_DICT, "jpg", "jpg", imageParentId));
        dictionaryDAO.insert(new DictionaryPO(rentId, user, TYPE_DICT, "gif", "gif", imageParentId));
        dictionaryDAO.insert(new DictionaryPO(rentId, user, TYPE_DICT, "bmp", "bmp", imageParentId));

        DictionaryPO streamPO = new DictionaryPO(rentId, user, CLASSIFY_DICT, "流媒体", "5", 0L);
        dictionaryDAO.insert(streamPO);
        Long streamParentId = streamPO.getId();
        dictionaryDAO.insert(new DictionaryPO(rentId, user, TYPE_DICT, "swf", "swf", streamParentId));
        dictionaryDAO.insert(new DictionaryPO(rentId, user, TYPE_DICT, "rm", "rm", streamParentId));
        dictionaryDAO.insert(new DictionaryPO(rentId, user, TYPE_DICT, "mpg", "mpg", streamParentId));

        dictionaryDAO.insert(new DictionaryPO(rentId, user, CLASSIFY_DICT, "自描述格式", "6", 0L));
        dictionaryDAO.insert(new DictionaryPO(rentId, user, CLASSIFY_DICT, "服务接口", "7", 0L));

        //1数据库，2文件下载，3webservice服务
        dictionaryDAO.insert(new DictionaryPO(rentId, user, SHARE_DICT, "数据库", "1", 0L));
        dictionaryDAO.insert(new DictionaryPO(rentId, user, SHARE_DICT, "文件下载", "2", 0L));
        dictionaryDAO.insert(new DictionaryPO(rentId, user, SHARE_DICT, "服务方式", "3", 0L));

    }

    @Override
    public List<DictionaryVO> getResourceAllDictionaryInfo(Long rentId, String user, String type) {
        List<DictionaryPO> dictList = dictionaryDAO.getByRentIdAndType(rentId, type);
        if(dictList==null || dictList.size()==0){
            initDictionary(rentId, user);
            dictList = dictionaryDAO.getByRentIdAndType(rentId, type);
        }
        return transferDictPOToVO(dictList);
    }

    @Override
    public List<DictionaryVO> getResourceTypeDict(Long rentId, String user) {
        List<DictionaryVO> dictList = getAllDictionaryInfo(rentId, TYPE_DICT);
        if(dictList==null||dictList.size()==0){
            initDictionary(rentId, user);
            dictList = getAllDictionaryInfo(rentId, TYPE_DICT);
        }
        List<DictionaryVO> targetList = new ArrayList<>();
        for(DictionaryVO vo: dictList){
            if(vo.getTypeParentId().equals(0L)){
                List<DictionaryVO> childrenList = new ArrayList<>();
                for(DictionaryVO child:dictList){
                    if(child.getTypeParentId().equals(vo.getId())){
                        childrenList.add(child);
                    }
                }
                vo.setChildrenList(childrenList);
                targetList.add(vo);
            }
        }
        return targetList;
    }

    @Override
    public List<DictionaryVO> getResourceShareDict(Long rentId, String user) {
        List<DictionaryVO> dictList = getAllDictionaryInfo(rentId, SHARE_DICT);
        if(dictList==null||dictList.size()==0){
            initDictionary(rentId, user);
            dictList = getAllDictionaryInfo(rentId, SHARE_DICT);
        }
        return dictList;
    }

    @Override
    public void increaseDictUseCount(Long rentId, String type, String code) {
        DictionaryPO dPO = dictionaryDAO.getByTypeAndCode(rentId, type, code);
        if(dPO==null){
            log.error("字典出现异常-增加计数：租户 {}，类型{}，编码{}", rentId, type, code);
            return;
        }
        Long lastCount =dPO.getUseCount()+1;
        dPO.setUseCount(lastCount);
        dPO.setModifyTime(new Date());
        dictionaryDAO.updateById(dPO);

    }

    @Override
    public void decreaseDictUseCount(Long rentId, String type, String code) {
        DictionaryPO dPO = dictionaryDAO.getByTypeAndCode(rentId, type, code);
        if(dPO==null){
            log.error("字典出现异常-减少计数：租户 {}，类型{}，编码{}", rentId, type, code);
            return;
        }
        Long lastCount =dPO.getUseCount();
        if(lastCount>0){
            lastCount = lastCount-1;
        }
        dPO.setUseCount(lastCount);
        dPO.setModifyTime(new Date());
        dictionaryDAO.updateById(dPO);
    }


    private List<DictionaryVO> transferDictPOToVO(List<DictionaryPO> poList){
        if(poList==null || poList.size()==0){
            return null;
        }
        List<DictionaryVO> dictVoList = new ArrayList<>();
        for(DictionaryPO po:poList){
            dictVoList.add(new DictionaryVO(po));
        }
        return dictVoList;
    }
}
