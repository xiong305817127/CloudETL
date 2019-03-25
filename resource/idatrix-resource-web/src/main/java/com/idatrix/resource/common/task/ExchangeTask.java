package com.idatrix.resource.common.task;


import com.idatrix.resource.catalog.dao.ResourceColumnDAO;
import com.idatrix.resource.catalog.dao.ResourceConfigDAO;
import com.idatrix.resource.catalog.po.ResourceColumnPO;
import com.idatrix.resource.catalog.po.ResourceConfigPO;
import com.idatrix.resource.catalog.vo.ResourceColumnVO;
import com.idatrix.resource.common.utils.CommonConstants;
import com.idatrix.resource.common.utils.DateTools;
import com.idatrix.resource.subscribe.dao.SubscribeDAO;
import com.idatrix.resource.subscribe.dao.SubscribeDbioDAO;
import com.idatrix.resource.subscribe.po.SubscribeDbioPO;
import com.idatrix.resource.subscribe.po.SubscribePO;
import com.idatrix.resource.taskmanage.dao.SubTaskDAO;
import com.idatrix.resource.taskmanage.po.SubTaskPO;
import com.ys.idatrix.cloudetl.subscribe.api.dto.CreateJobDto;
import com.ys.idatrix.cloudetl.subscribe.api.dto.QueryJobDto;
import com.ys.idatrix.cloudetl.subscribe.api.dto.SubscribeResultDto;
import com.ys.idatrix.cloudetl.subscribe.api.dto.parts.OutputFieldsDto;
import com.ys.idatrix.cloudetl.subscribe.api.dto.parts.SearchFieldsDto;
import com.ys.idatrix.cloudetl.subscribe.api.dto.step.InsertUpdateDto;
import com.ys.idatrix.cloudetl.subscribe.api.dto.step.TableInputDto;
import com.ys.idatrix.cloudetl.subscribe.api.dto.step.TimerDto;
import com.ys.idatrix.cloudetl.subscribe.api.service.SubscribeService;
import com.ys.idatrix.metacube.api.beans.MetadataDTO;
import com.ys.idatrix.metacube.api.beans.ResultBean;
import com.ys.idatrix.metacube.api.beans.dataswap.MetadataField;
import com.ys.idatrix.metacube.api.beans.dataswap.QueryMetadataFieldsResult;
import com.ys.idatrix.metacube.api.service.MetadataToDataSwapService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.idatrix.resource.subscribe.utils.SubscribeConstants.*;

/**
 *  交换任务
 */
@Component
@Transactional
@PropertySource("classpath:init.properties")
public class ExchangeTask{

    private final Logger LOG= LoggerFactory.getLogger(this.getClass());

    @Autowired
    private SubscribeDAO subscribeDAO;

    @Autowired
    private ResourceConfigDAO resourceConfigDAO;

    @Autowired
    private ResourceColumnDAO resourceColumnDAO;

    @Autowired
    private SubscribeDbioDAO subscribeDbioDAO;

    @Autowired
    private SubscribeService subscribeService;

    @Autowired
    private SubTaskDAO subTaskDAO;

    @Autowired(required=false)
    private MetadataToDataSwapService metacubeCatalogService;

    /*增量方式 ：  可为空 ，为空则表示该任务不增量获取，不为空：可选 date,sequence，表示增量类型 日期/序列(数字型)*/
    @Value("${incremental}")
    private String incremental;

    /*增量字段，时间戳字段或者序列字段*/
    @Value("${incremental.field}")
    private String incrementalField;

    /*增量初始值，默认为0，当序列从0开始时，需要传例如 -1*/
    @Value("${incremental.initValue}")
    private String incrementalInitValue;

    /*定时交换任务开始时间*/
    @Value("${scheduler.hour}")
    private Long schdeulerHour;


    public void startTask(){
//        LOG.info("^^^^^^^^^^Exchange Task^^^^^^^^^^");
        List<SubTaskPO> taskList = subTaskDAO.getByStatus(CommonConstants.WAIT_IMPORT);
        if(CollectionUtils.isNotEmpty(taskList)){
            for(SubTaskPO taskPO:taskList){
                String user = taskPO.getCreator();
                String subscribeId = taskPO.getEtlSubscribeId();

                if(StringUtils.isNotEmpty(subscribeId) &&
                        StringUtils.equals(taskPO.getStatus(), CommonConstants.WAIT_IMPORT)){
                    try{
                        startSubscribeTask(user, subscribeId);
                        taskPO.setStatus(CommonConstants.IMPORTING);
                        subTaskDAO.updateById(taskPO);
                    }catch (Exception e){
                        e.printStackTrace();
                        LOG.error(new Date().toString() + "执行任务失败-" + taskPO.getId()+"-"+taskPO.getSubTaskId() + " 异常："
                                + e.getMessage());
                    }
                }else if(StringUtils.equals(taskPO.getStatus(), CommonConstants.WAIT_IMPORT)){
                    try {
                        subscribeId = creatTask(taskPO.getSubTaskId(), taskPO.getSrcMetaId(), taskPO.getDestMetaId());
                        if (StringUtils.isNotEmpty(subscribeId)) {
                           // startSubscribeTask(user, subscribeId);
                            taskPO.setEtlSubscribeId(subscribeId);
                            taskPO.setStatus(CommonConstants.IMPORTING);
                            subTaskDAO.updateById(taskPO);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        LOG.error(DateTools.formatDate(new Date()) + ": " + taskPO.getId() + "-" + taskPO.getSubTaskId() + " \n异常："
                                + e.getMessage());
                    }
                }
            }
        }

    }

    private TimerDto getTimerConfig(ResourceConfigPO rcPO){
        int cycleValue = rcPO.getRefreshCycle();

        int startHour = 6;
        if(schdeulerHour!=null && schdeulerHour>=0 && schdeulerHour<24){
            startHour = schdeulerHour.intValue();
        }


        TimerDto timerDto = new TimerDto();
        timerDto.setRepeat(true);

        if(cycleValue==EVERY_DAY){
            timerDto.setSchedulerType(2);
            timerDto.setMinutes(0);
            timerDto.setHour(startHour);  //每天早上6点更新
        }else if(cycleValue==EVERY_WEEK){
            timerDto.setSchedulerType(3);
            timerDto.setWeekDay(6);
            timerDto.setMinutes(0);
            timerDto.setHour(startHour);  //每周一早上六点更新
        }else if(cycleValue==EVERY_MONTH){
            timerDto.setSchedulerType(4);
            timerDto.setDayOfMonth(1);
            timerDto.setMinutes(0);
            timerDto.setHour(startHour);  //每月1号早上6点更新
        }else if(cycleValue==EVERY_QUARTER){
            timerDto.setSchedulerType(5);
            timerDto.setDayOfMonth(1);
            timerDto.setMinutes(0);
            timerDto.setHour(startHour);  //每月1号早上6点更新
        }else if(cycleValue==EVERY_HALF_YEAR) {
            timerDto.setSchedulerType(7);
            timerDto.setIntervalDelayMinutes(0);
            timerDto.setMonthOfYear(6);
            timerDto.setDayOfMonth(1);
            timerDto.setMinutes(0);
            timerDto.setHour(startHour);  //每月1号早上6点更新
        }else if(cycleValue==EVERY_YEAR){
            timerDto.setSchedulerType(6);
            timerDto.setDayOfMonth(1);
            timerDto.setMinutes(0);
            timerDto.setHour(startHour);  //每月1号早上6点更新
        }else if(cycleValue==REAL_TIME){
            timerDto.setSchedulerType(1);
            timerDto.setSeconds(0);
            timerDto.setMinutes(5);
        }
        return timerDto;
    }

    /*
    *
    * @Title: 创建交换任务
    * @param: sourceMetaId 交换任务数据源-元数据ID
    * @param: destMetaId   交换任务目标-元数据ID
    *
    */
//    public String creatTask(Long subscribeId, Long sourceMetaId, Long destMetaId)throws Exception{
    public String creatTask(String subNo, Long sourceMetaId, Long destMetaId)throws Exception{

//        SubscribePO subscribePO = subscribeDAO.getById(subscribeId);
        SubscribePO subscribePO = subscribeDAO.getBySubNo(subNo);
        if(subscribePO==null){
            throw new Exception("subscribeId-"+subNo+" 在订阅数据中没有记录");
        }
        ResourceConfigPO rcPO = resourceConfigDAO.getConfigById(subscribePO.getResourceId());
        if(rcPO==null){
            throw new Exception("resourceId-"+subscribePO.getResourceId()+" 在资源数据中没有记录");
        }
        List<ResourceColumnPO> sourcePoList = resourceColumnDAO.getColumnByResourceId(rcPO.getId());

        List<SubscribeDbioPO> subOutputList = subscribeDbioDAO.getBySubscribeIdAndType(
                subscribePO.getId(), "input");
        List<ResourceColumnVO> destPoList = new ArrayList<ResourceColumnVO>();
        for(SubscribeDbioPO sdbPO:subOutputList){
            ResourceColumnPO resourceColumnPO = resourceColumnDAO.getColumnById(sdbPO.getColumnId());
            if(resourceColumnPO!=null){
                ResourceColumnVO rcVO = new ResourceColumnVO(resourceColumnPO);
                if(StringUtils.isNotEmpty(sdbPO.getDataMaskingType())){
                    rcVO.setDataMaskingType(sdbPO.getDataMaskingType());
                    rcVO.setDataStartIndex(sdbPO.getDataStartIndex());
                    rcVO.setDataLength(sdbPO.getDataLength());
                }
                destPoList.add(rcVO);
            }
        }

        //交换任务描述
        StringBuilder descpt = new StringBuilder(subscribePO.getDeptName()+"-");
        descpt.append(subscribePO.getSubscribeUserName()+"订阅 "+rcPO.getCode()+":"+rcPO.getName());

        //采用数据上报创建者作为ETL任务的userId, ETL的任务号作为ETL的名字
        String userName = subscribePO.getCreator();
        String jobName = subscribePO.getSubNo();
        String prefix = "SUBSCRIBE-EXCHANGE-";
        String group = subscribePO.getShareMethod()==2?prefix+"FILE":prefix+"DB";
        CreateJobDto createJobDto = new CreateJobDto(userName, jobName, group, descpt.toString());

        //修改交换定时周期： 分为实时1、每日2、每周3、每月4、每季度5、每半年6，每年7等。
        try {
            /************************dataInput FileInputDto参数拼装****************************/
            TableInputDto tableInputDto = assembleTableInputDtoParams(sourcePoList, sourceMetaId);
            createJobDto.setDataInput(tableInputDto);

            /************************dataOutput InsertUpdateDto参数拼装************************/
            InsertUpdateDto insertUpdateDto = assembleInsertUpdateDto(destPoList, destMetaId);
            createJobDto.setDataOutput(insertUpdateDto);
            createJobDto.setTimer(getTimerConfig(rcPO));

        } catch (NullPointerException e) {
            throw new Exception("当前资源" + rcPO.getId() + "|" + rcPO.getName() + " bind_table_id为空, 入库失败");
        }


        LOG.info("交换任务创建参数 :{}", createJobDto.toString());
        SubscribeResultDto subscribeResultDto = subscribeService.createSubscribeJob(createJobDto);
        LOG.info("交换任务结果内容 :{}", subscribeResultDto.toString());

        String etlSubscribeId = null;
        if (subscribeResultDto.getStatus() == 0) {
            etlSubscribeId = subscribeResultDto.getSubscribeId();
            LOG.info("数据交换记录" + subscribeResultDto.getName() +
                    "创建ETL任务-subscribeId" + etlSubscribeId );
        } else {
            throw new Exception("创建交换任务失败,失败原因："+subscribeResultDto.getErrorMessage());
        }
        return etlSubscribeId;
    }

    private TableInputDto assembleTableInputDtoParams(List<ResourceColumnPO> columnPOList, Long metaId) throws Exception {

        TableInputDto tableInputDto = new TableInputDto();

        ResultBean<MetadataDTO> respon = metacubeCatalogService.findTableInfoByID(metaId);
        if(!respon.isSuccess()){
            LOG.error("元数据表metaId "+metaId+",获取元数据信息失败："+respon.getMsg());
            throw new Exception("元数据表metaId "+metaId+",获取元数据信息失败："+respon.getMsg());
        }
        MetadataDTO metadataDTO = respon.getData();
        tableInputDto.setSchemaId(new Long(metadataDTO.getSchemaId()));
        tableInputDto.setTableId(new Long(metadataDTO.getMetaId()));
        tableInputDto.setTableType("table");
        tableInputDto.setTable(metadataDTO.getMetaName());

        SearchFieldsDto primaryKey = new SearchFieldsDto();
        List<OutputFieldsDto> outputFields = new ArrayList<OutputFieldsDto>();

        List<String> fields = new ArrayList<String>();
        //交换时候必须有字段
//        fields.add("ds_batch");
//        fields.add("ds_sync_time");
//        fields.add("ds_sync_flag");
        fields.addAll(getSpecilFiled(metaId));
        for (ResourceColumnPO model : columnPOList) {
            String field = model.getTableColCode();
            fields.add(field);
        }

        String incre = "date";
        String increField = "ds_sync_time";
        String increInitValue = "1970-01-01 00:00:00";

        if(StringUtils.isNotEmpty(incremental) &&
                StringUtils.isNotEmpty(incrementalField)){
            incre = incremental;
            increField = incrementalField;
            increInitValue = incrementalInitValue;
        }
        tableInputDto.setIncremental(incre);
        tableInputDto.setIncrementalField(increField);
        tableInputDto.setIncrementalInitValue(increInitValue);

        tableInputDto.setFields(fields);
        //LOG.info("配置任务Input信息： {}", tableInputDto.toString());
        return tableInputDto;
    }


    /*dataInput InsertUpdateDto参数拼装*/
    private InsertUpdateDto assembleInsertUpdateDto(List<ResourceColumnVO> columnPOList,Long metaId)throws Exception {


        InsertUpdateDto insertUpdateDto = new InsertUpdateDto();

        ResultBean<MetadataDTO> respon = metacubeCatalogService.findTableInfoByID(metaId);
        if(!respon.isSuccess()){
            LOG.error("元数据表metaId "+metaId+",获取元数据信息失败："+respon.getMsg());
            throw new Exception("元数据表metaId "+metaId+",获取元数据信息失败："+respon.getMsg());
        }
        MetadataDTO metadataDTO = respon.getData();
        insertUpdateDto.setSchemaId(new Long(metadataDTO.getSchemaId()));
        insertUpdateDto.setTableId(new Long(metadataDTO.getMetaId()));
        insertUpdateDto.setTable(metadataDTO.getMetaName());

        SearchFieldsDto primaryKey = new SearchFieldsDto();
        List<OutputFieldsDto> outputFields = new ArrayList<OutputFieldsDto>();

        for (ResourceColumnVO model : columnPOList) {
            OutputFieldsDto outputFieldsDto = new OutputFieldsDto(model.getTableColCode(), model.getTableColCode());
            outputFieldsDto.setUpdate(true);

            if (model.getUniqueFlag().equals(true)) {
                primaryKey = new SearchFieldsDto(model.getTableColCode(), "=", model.getTableColCode());
                outputFieldsDto.setUpdate(false);
            }
            if(StringUtils.equalsAnyIgnoreCase(model.getDataMaskingType(), "mask")){
                outputFieldsDto.addMaskRule(model.getDataStartIndex(), model.getDataLength());
            }else if(StringUtils.equalsAnyIgnoreCase(model.getDataMaskingType(), "truncate")){
                outputFieldsDto.addTruncationRule(model.getDataStartIndex(), model.getDataLength());
            }
            outputFields.add(outputFieldsDto);
        }

        //交换特殊字段必须
        //        fields.add("ds_batch"), fields.add("ds_sync_time"),fields.add("ds_sync_flag");
        //List<String> requiredFields = Arrays.asList("ds_batch", "ds_sync_time", "ds_sync_flag");
        List<String> requiredFields = new ArrayList<String>();
        requiredFields.addAll(getSpecilFiled(metaId));
        for(String field: requiredFields){
            OutputFieldsDto outputFieldsDto = new OutputFieldsDto(field, field);
            outputFieldsDto.setUpdate(true);
            outputFields.add(outputFieldsDto);
        }
        insertUpdateDto.setIncloudFlag(false);
        insertUpdateDto.setIncloudBatch(false);
        insertUpdateDto.setIncloudTime(false);

        List<SearchFieldsDto> searchField = new ArrayList<SearchFieldsDto>();
        searchField.add(primaryKey);

        insertUpdateDto.setSearchFields(searchField);
        insertUpdateDto.setUpdateFields(outputFields);
        //LOG.info("配置任务InsertUpdate信息： {}", insertUpdateDto.toString());
        return insertUpdateDto;
    }

    public Boolean startSubscribeTask(String userName, String subscribeId)throws Exception{

        SubscribeResultDto subscribeResultDto;

        try {
            QueryJobDto queryJobDto = new QueryJobDto(userName, subscribeId);
            subscribeResultDto = subscribeService.startSubscribeJob(queryJobDto);

            if (subscribeResultDto.getStatus() == 0) {

                LOG.info("启动数据交换-SubscribeId:" + subscribeResultDto.getSubscribeId() +
                        " ExcId:" + subscribeResultDto.getCurExecId() + "其中交换任务成功。");
                return true;
            } else {
                throw new Exception("启动交换任务失败,失败原因："+subscribeResultDto.getErrorMessage());
            }
        } catch (Exception e) {
            LOG.error(e.getMessage());
            throw new Exception(e.getMessage());
        }
    }


    public Boolean stopSubscribeTask(String userName, String subscribeId)throws Exception{

        SubscribeResultDto subscribeResultDto;
        try {
            QueryJobDto queryJobDto = new QueryJobDto(userName, subscribeId);
            subscribeResultDto = subscribeService.stopSubscribeJob(queryJobDto);

            if (subscribeResultDto.getStatus() == 0) {
                LOG.info("停止数据交换-SubscribeId:" + subscribeResultDto.getSubscribeId()
                        + " ExcId:" + subscribeResultDto.getCurExecId() + "其中交换任务成功。");
                return true;
            } else {
                throw new Exception("停止交换任务失败,失败原因："+subscribeResultDto.getErrorMessage());
            }
        } catch (Exception e) {
            LOG.error(e.getMessage());
            throw new Exception(e.getMessage());
        }
    }

    //达梦、oracle、mysql 各种不同数据库需要考虑大小写问题，传递给ETL参数根据大小写来固定配置
    private List<String> getSpecilFiled(Long metaId)throws Exception{

        List<String> fields = new ArrayList<String>();
        //交换时候必须有字段
        //配置好交换需要用到的字段 robin 2018/08/28
        List<MetadataField> metadataOriginFields = new ArrayList<MetadataField>();

        ResultBean<QueryMetadataFieldsResult> metaResult = metacubeCatalogService.getMetadataFieldsByMetaId(metaId.intValue());
        if(metaResult.isSuccess()){
            metadataOriginFields = metaResult.getData().getMetadataField();
        }else{
            LOG.error("获取资源绑定元数据结构异常：metaId {},错误原因：{}", metaId, metaResult.getMsg());
            throw new Exception("获取资源绑定元数据结构异常：metaId"+metaId);
        }
        List<MetadataField> metaFinalFields = new ArrayList<MetadataField>();
        for(MetadataField field:metadataOriginFields){

            if(StringUtils.equalsIgnoreCase("ds_batch", field.getColName())){
                fields.add(field.getColName());
            }else if(StringUtils.equalsIgnoreCase("ds_sync_time", field.getColName())){
                fields.add(field.getColName());
            }else if(StringUtils.equalsIgnoreCase("ds_sync_flag", field.getColName())){
                fields.add(field.getColName());
            }
        }
        return fields;
    }
}
