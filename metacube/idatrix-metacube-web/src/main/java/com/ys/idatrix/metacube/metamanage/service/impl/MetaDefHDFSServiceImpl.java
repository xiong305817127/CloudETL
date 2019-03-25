package com.ys.idatrix.metacube.metamanage.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.ys.idatrix.db.api.common.RespResult;
import com.ys.idatrix.db.api.hdfs.service.HdfsUnrestrictedService;
import com.ys.idatrix.graph.service.api.def.DatabaseType;
import com.ys.idatrix.metacube.api.beans.PageResultBean;
import com.ys.idatrix.metacube.common.enums.TableColumnStatusEnum;
import com.ys.idatrix.metacube.common.exception.MetaDataException;
import com.ys.idatrix.metacube.metamanage.domain.Metadata;
import com.ys.idatrix.metacube.metamanage.domain.SnapshotMetadata;
import com.ys.idatrix.metacube.metamanage.mapper.MetadataMapper;
import com.ys.idatrix.metacube.metamanage.mapper.SnapshotMetadataMapper;
import com.ys.idatrix.metacube.metamanage.service.IMetaDefBaseService;
import com.ys.idatrix.metacube.metamanage.service.IMetaDefHDFSService;
import com.ys.idatrix.metacube.metamanage.vo.request.MetaDefHDFSVO;
import com.ys.idatrix.metacube.metamanage.vo.request.MetadataSearchVo;
import com.ys.idatrix.metacube.metamanage.vo.response.MetaDefOverviewVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static com.ys.idatrix.metacube.api.beans.DatabaseTypeEnum.HDFS;
import static com.ys.idatrix.metacube.common.enums.DataStatusEnum.*;

/**
 * 元数据定义-HDFS
 * @author robin
 *
 */
@Transactional
@Slf4j
@Service("metaDefHDFSService")
public class MetaDefHDFSServiceImpl implements IMetaDefHDFSService  {

    @Autowired
    private IMetaDefBaseService metaDefBaseService;

    @Autowired
    private MetadataMapper metadataMapper;

    @Autowired
    private SnapshotMetadataMapper snapshotMetadataMapper;

    @Autowired
    private HdfsUnrestrictedService hdfsUnrestrictedService;

    /**
     * HDFS定义查询
     * @param searchVO
     * @return
     */
    @Override
    public PageResultBean<MetaDefOverviewVO> hdfsQueryOverview(MetadataSearchVo searchVO) {

        int pageNum = searchVO.getPageNum();
        int pageSize = searchVO.getPageSize();

        PageHelper.startPage(pageNum, pageSize);
        List<Metadata> dataList = metadataMapper.queryMetaDataBySearchVO(searchVO);
        List<MetaDefOverviewVO> rcVOList = transferHDFSMetaData(dataList);

        //用PageInfo对结果进行包装
        PageInfo<Metadata> pi = new PageInfo<Metadata>(dataList);
        Long totalNums = pi.getTotal();
        PageResultBean<MetaDefOverviewVO> rp = new PageResultBean<MetaDefOverviewVO>(pi.getPageNum(), totalNums, rcVOList);
        return rp;
    }

    private List<MetaDefOverviewVO> transferHDFSMetaData(List<Metadata> dataList){
        List<MetaDefOverviewVO> overviewList = new ArrayList<>();
        if(CollectionUtils.isEmpty(dataList)){
            return overviewList;
        }
        for(Metadata data:dataList){
            MetaDefOverviewVO vo = new MetaDefOverviewVO();
            BeanUtils.copyProperties(data, vo);
            vo.setStatus(data.getPublicStatus());
            overviewList.add(vo);
        }
        return overviewList;
    }

    /**
     * 保存草稿状态
     *
     * @param rentId
     * @param user
     * @param baseVO
     * @return
     */
    @Override
    public Metadata saveDraft(Long rentId, String user, MetaDefHDFSVO baseVO)throws MetaDataException {


        if(!verifyConfigParam(rentId, baseVO)){
            return null;
        }
        baseVO.setStatus(DRAFT.getValue());
        Metadata data =saveMetaData(rentId, user, baseVO,false);
        return data;
    }

    private boolean verifyConfigParam(Long rentId, MetaDefHDFSVO baseVO){

        //数据检验:1.同一个节点下面不能名字相同的配置，不同节点下名字相同没关系。2.文件路径名称必须唯一。

        if(StringUtils.isBlank(baseVO.getIdentification()) ||
                StringUtils.isBlank(baseVO.getName()) ||
                  baseVO.getSchemaId()==null){
            throw new MetaDataException("缺少必填参数");
        }

        //HDFS需要在更目录自自动增加路径符号"/"
        String name = baseVO.getName();
        if(!name.startsWith("/")){
            baseVO.setName(new StringBuilder("/").append(name).toString());
        }

        Long id = baseVO.getId();
        if(id==null||id.equals(0L)){
            List<Metadata> sameNameDataList = metadataMapper.queryMetaData(baseVO.getSchemaId(), baseVO.getName(), null);
            List<Metadata> existList = sameNameDataList.stream().filter(p->p.getStatus()!=2).collect(Collectors.toList());
            if(CollectionUtils.isNotEmpty(existList)){
                throw new MetaDataException("同一模式下存在相同目录名称");
            }
            List<Metadata> sameDictList = metadataMapper.queryMetaData(baseVO.getSchemaId(), null, baseVO.getIdentification());
            List<Metadata> existSameDictList =sameDictList.stream().filter(p->p.getStatus()!=2).collect(Collectors.toList());
            if(CollectionUtils.isNotEmpty(existSameDictList)){
                throw new MetaDataException("配置子目录路径已经存在，请修改");
            }
        }else{
            List<Metadata> sameNameDataList = metadataMapper.queryMetaData(baseVO.getSchemaId(), baseVO.getName(), null);
            List<Metadata> existSameNameDataList =sameNameDataList.stream().filter(p->p.getStatus()!=2).collect(Collectors.toList());
            if(CollectionUtils.isNotEmpty(existSameNameDataList)){

                if(sameNameDataList.size()>1){
                    throw new MetaDataException("同一模式下存在相同目录名称");
                }else{
                    Metadata sameMetaData = sameNameDataList.get(0);
                    if(!sameMetaData.getId().equals(id)){
                        throw new MetaDataException("同一模式下存在相同目录名称");
                    }
                }
            }

            List<Metadata> sameDictList = metadataMapper.queryMetaData(baseVO.getSchemaId(), null, baseVO.getIdentification());
            List<Metadata> existSameDictList =sameDictList.stream().filter(p->p.getStatus()!=2).collect(Collectors.toList());
            if(CollectionUtils.isNotEmpty(existSameDictList)){
                if(sameDictList.size()>1){
                    throw new MetaDataException("配置子目录路径已经存在，请修改");
                }else{
                    Metadata sameMetaData = existSameDictList.get(0);
                    if(!sameMetaData.getId().equals(id)){
                        throw new MetaDataException("配置子目录路径已经存在，请修改");
                    }
                }
            }
        }
        return true;
    }


    private Metadata saveMetaData(Long rentId, String user, MetaDefHDFSVO baseVO, Boolean execFlag){
        Metadata data = null;
        Long metaId = baseVO.getId();
        int version = baseVO.getVersion();
        if(metaId==null || metaId.equals(0L)){
            data = new Metadata();
            BeanUtils.copyProperties(baseVO, data);
            data.setRenterId(rentId);
            data.setCreator(user);
            data.setCreateTime(new Date());
            version = 1;

        }else{
            data = metadataMapper.selectByPrimaryKey(metaId);
            BeanUtils.copyProperties(baseVO, data);
            version = data.getVersion()+1;
        }
        data.setDatabaseType(HDFS.getCode());

        int status = DRAFT.getValue();
        if(execFlag){
            status = VALID.getValue();
        }
        data.setStatus(status);

        data.setVersion(version);
        data.setModifyTime(new Date());
        data.setModifier(user);
        if(metaId==null || metaId.equals(0L)){
            metadataMapper.insertSelective(data);
        }else{
            metadataMapper.updateByPrimaryKeySelective(data);
        }
        return metadataMapper.selectByPrimaryKey(data.getId());
    }



    /**
     * 保存生效状态
     *
     * @param rentId
     * @param user
     * @param baseVO
     * @return
     */
    @Override
    public Metadata saveExec(Long rentId, String user, MetaDefHDFSVO baseVO) {

        if(!verifyConfigParam(rentId, baseVO)){
            return null;
        }

        //开始调用HDFS创建命令:屏蔽dubbo影响，后面使用
        String errorMsg=null;
        Metadata lastData = getLastData(baseVO.getId());
        if(lastData==null || !StringUtils.equals(lastData.getIdentification(), baseVO.getIdentification())) {
            try {
                String fullPath = baseVO.getRootPath() + baseVO.getName();
                if (StringUtils.isNotBlank(fullPath)) {
                    RespResult<Boolean> hr = hdfsUnrestrictedService.createDir(user, fullPath);
                    if (hr.isSuccess()) {
                        log.info("成功创建HDFS目录:{}", fullPath);
                    } else {
                        errorMsg = hr.getMsg();
                        log.info("创建HDFS目录:{},失败:{}", fullPath, hr.getMsg());
                    }
                }
            } catch (Exception e) {
                errorMsg = "操作失败:" + e.getMessage();
                e.printStackTrace();
                throw new MetaDataException("创建HDFS失败 " + errorMsg);
            }
        }

        //入库数据库
        baseVO.setStatus(VALID.getValue());
        Metadata data = saveMetaData(rentId, user, baseVO, true);

        //进行数据版本快照
        SnapshotMetadata snop = new SnapshotMetadata();
        BeanUtils.copyProperties(data, snop);
        snop.setMetaId(data.getId());
        String snopDescription = getSnopDetails(data);
        snop.setDetails(snopDescription);
        snop.setId(null);
        snapshotMetadataMapper.insertSelective(snop);
        metaDefBaseService.updateMetadataChangeInfoToGraph(DatabaseType.HDFS, data);
        return data;
    }

    /**
     * 获取元数据最近的一次快照
     * @param id
     * @return
     */
    private Metadata getLastData(Long id){

        if(id==null||id.equals(0L)){
            return null;
        }
        List<SnapshotMetadata> metaList = snapshotMetadataMapper.getSnapshotMetadataByMetaId(id);
        if(CollectionUtils.isEmpty(metaList)){
            return null;
        }
        SnapshotMetadata lastSnapshot = metaList.get(0);
        Metadata lastMetaData = new Metadata();
        BeanUtils.copyProperties(lastSnapshot, lastMetaData);
        lastMetaData.setId(lastSnapshot.getMetaId());
        return lastMetaData;
    }

    /**
     * 获取元数据变更描述
     * @param data
     * @return
     */
    private String getSnopDetails(Metadata data){
        StringBuilder detail = new StringBuilder();

        List<SnapshotMetadata> metaList = snapshotMetadataMapper.getSnapshotMetadataByMetaId(data.getId());
        if(CollectionUtils.isEmpty(metaList)){
            detail.append("新增数据");
            return detail.toString();
        }
        SnapshotMetadata lastSnapshot = metaList.get(0);
        Metadata lastMetaData = new Metadata();
        BeanUtils.copyProperties(lastSnapshot, lastMetaData);
        if(!StringUtils.equalsAnyIgnoreCase(lastMetaData.getName(), data.getName())){
            detail.append(",修改目录名称");
        }
        if(!StringUtils.equalsAnyIgnoreCase(lastMetaData.getIdentification(), data.getIdentification())){
            detail.append(",修改子目录");
        }
        if(lastMetaData.getPublicStatus()==null || !lastMetaData.getPublicStatus().equals(data.getPublicStatus())){
            detail.append(",公开登记发生变更");
        }
        if(lastMetaData.getThemeId()==null || !lastMetaData.getThemeId().equals(data.getThemeId())){
            detail.append(",主题发生变更");
        }
        if(!StringUtils.equalsAnyIgnoreCase(lastMetaData.getTags(), data.getTags())){
            detail.append(",标签发生变更");
        }
        if(!StringUtils.equalsAnyIgnoreCase(lastMetaData.getRemark(), data.getRemark())){
            detail.append(",备注发生变更");
        }

        log.info("HDFS版本变更生成描述：{}",detail.toString());
        return detail.toString();
    }

    /**
     * HDFS定义删除
     *
     * @param rentId
     * @param user
     * @param id
     * @return
     */
    @Override
    public Long delete(Long rentId, String user, Long id)throws MetaDataException{

        Metadata data = metadataMapper.selectByPrimaryKey(id);
        if(data==null){
            throw new MetaDataException("存储系统中没有该记录");
        }
        if(data.getStatus().equals(VALID.getValue())){
            String fullDirPath = metadataMapper.getMetaDefHDFSFullDir(id);
            if(StringUtils.isEmpty(fullDirPath)){
                throw new MetaDataException("删除HDFS失败 文件路径不存在");
            }

//            HdfsExecuteResult result = hdfsUnrestrictedService.deleteDir(Lists.newArrayList(fullDirPath), false);
//            if(!result.isSuccess()){
//                String hasDataErrorMsg = "存在子文件";
//                throw new MetaDataException("删除HDFS失败 "+result.getMessage());
//            }
        }else if(data.getStatus().equals(TableColumnStatusEnum.DELETE.getValue())){
            throw new MetaDataException("删除HDFS失败 该记录已经被删除");
        }

        data.setModifier(user);
        data.setModifyTime(new Date());
        data.setStatus(DELETE.getValue());
        metadataMapper.updateByPrimaryKeySelective(data);
        metaDefBaseService.updateMetadataDeleteInfoToGraph(DatabaseType.HDFS, id);
        return id;
    }

    /**
     * HDFS获取详情
     *
     * @param rentId
     * @param user
     * @param id
     * @return
     */
    @Override
    public Metadata getDetail(Long rentId, String user, Long id) {

        Metadata data = metadataMapper.selectByPrimaryKey(id);
        if(data==null){
            throw new MetaDataException("存储系统中没有该记录");
        }
        return data;
    }

    /**
     * 根据租户或者搜索关键字查询所有列表
     *
     * @param rentId
     * @param searchKey
     * @return
     */
    @Override
    public List<Metadata> getAllDirByRentId(Long rentId, String searchKey) {

        List<Metadata> orgHdfsList = metadataMapper.getAllHDFSFolderInfo(null, null, null, rentId);
        if (CollectionUtils.isNotEmpty(orgHdfsList)) {
            if (StringUtils.isNotEmpty(searchKey)) {
                return orgHdfsList.stream().filter(p -> StringUtils.containsIgnoreCase(p.getIdentification(), searchKey)).collect(Collectors.toList());
            } else {
                return orgHdfsList;
            }
        }
        return null;
    }
}
