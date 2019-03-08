package com.idatrix.resource.webservice.webservice.Impl;

import com.alibaba.fastjson.JSON;
import com.idatrix.resource.webservice.common.CommonConstants;
import com.idatrix.resource.webservice.common.CommonServiceException;
import com.idatrix.resource.webservice.dao.ResourceColumnDAO;
import com.idatrix.resource.webservice.dao.ResourceConfigDAO;
import com.idatrix.resource.webservice.dao.SubscribeDAO;
import com.idatrix.resource.webservice.dao.SubscribeDbioDAO;
import com.idatrix.resource.webservice.dto.ParamDTO;
import com.idatrix.resource.webservice.dto.ResultDTO;
import com.idatrix.resource.webservice.po.ResourceConfigPO;
import com.idatrix.resource.webservice.po.SubscribeDbioPO;
import com.idatrix.resource.webservice.po.SubscribePO;
import com.idatrix.resource.webservice.service.IServiceLogService;
import com.idatrix.resource.webservice.vo.InputParamVO;
import com.idatrix.resource.webservice.vo.SQLInfo;
import com.idatrix.resource.webservice.webservice.ISubscribeSearchService;
import com.ys.idatrix.db.agent.api.db.SqlQueryResult;
import com.ys.idatrix.db.proxy.api.sql.SqlCommand;
import com.ys.idatrix.db.proxy.api.sql.SqlExecuteDao;
import com.ys.idatrix.metacube.api.bean.cloudetl.DataBaseInfo;
import com.ys.idatrix.metacube.api.service.cloudetl.CloudETLService;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


@Component("subscribeSearchWebservice")
public class SubscribeSearchServiceImpl implements ISubscribeSearchService {

    private static final Logger LOG = LoggerFactory.getLogger(SubscribeSearchServiceImpl.class);

    public SubscribeSearchServiceImpl(){
        super();
    }

    @Autowired
    private SubscribeDAO subscribeDAO;

    @Autowired
    private SubscribeDbioDAO subscribeDbioDAO;

    @Autowired
    private ResourceConfigDAO resourceConfigDAO;

    @Autowired
    private CloudETLService cloudETLService;

    @Autowired
    private SqlExecuteDao sqlExecuteDao;

    @Autowired
    private IServiceLogService serviceLogService;

    @Autowired
    private ResourceColumnDAO resourceColumnDAO;

    @Override
    public ResultDTO databaseSearchByCondition(List<ParamDTO> inputParams, String subscribeKey, Integer pageNum, Integer pageSize) {

        long startTime = System.currentTimeMillis();

        ResultDTO resultDTO = new ResultDTO();

        SubscribePO subscribePO = subscribeDAO.getBySubscribeKey(subscribeKey);

        String inputStr = JSON.toJSONString(new InputParamVO(inputParams, subscribeKey, pageNum, pageSize));
        String outputStr = "", errorMsg = "", errorStack = "";
        int isSuccess = 0;

        try {
            //查询前，对权限以及查询参数进行校验
            validateBeforeSearch(inputParams, subscribeKey, subscribePO);

            //获取总的count数目
            SQLInfo sqlCountCommand = assembleQueryCountSQL(subscribePO, subscribeKey, inputParams);
            SqlQueryResult sqlResult = executeQuery(subscribeKey, sqlCountCommand);
            Object totalSize=sqlResult.getData().get(0).get("total");
            resultDTO.setTotalSize( Long.valueOf(String.valueOf(totalSize)));

            //执行sql命令返回数据
            int page = pageNum==null?0:pageNum.intValue();
            int size = pageSize==null?20:pageSize.intValue();
            SQLInfo sqlInfo = assembleSQL(subscribePO, subscribeKey, inputParams, page, size);
            SqlQueryResult sqlCommandResult = executeQuery(subscribeKey, sqlInfo);

            resultDTO.setStatusCode(CommonConstants.SUCCESS_VALUE);
            resultDTO.setColumns(sqlCommandResult.getColumns());
            resultDTO.setData(JSON.toJSONString(sqlCommandResult.getData()));
            outputStr = JSON.toJSONString(resultDTO);
            isSuccess = 1;
        } catch (CommonServiceException e) {
            e.printStackTrace();
            if (e.getErrorCode() == CommonConstants.EC_SEARCH_PARAM_ERROR) {
                resultDTO.setStatusCode(e.getErrorCode());
                resultDTO.setErrorMsg("输入参数校验失败:"+e.getMessage());
                errorMsg = "输入参数校验失败";
                errorStack = e.getMessage();
            } else if (e.getErrorCode() == CommonConstants.EC_SEARCH_NO_AUTH_ERROR) {
                resultDTO.setStatusCode(e.getErrorCode());
                resultDTO.setErrorMsg("无调用权限："+e.getMessage());
                errorMsg = "无调用权限";
                errorStack = e.getMessage();
            } else {
                resultDTO.setStatusCode(CommonConstants.EC_SEARCH_CALL_FAILURE);
                resultDTO.setErrorMsg("调用失败："+e.getMessage());
                errorMsg = "调用失败";
                errorStack = e.getMessage();
            }
            LOG.error(new Date() + "订阅编码" + subscribeKey + "调用出现异常：" + e.getErrorCode() + " " + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            resultDTO.setStatusCode(CommonConstants.EC_UNEXPECTED);
            resultDTO.setErrorMsg("订阅编码" + subscribeKey + " 调用失败 " + e.getMessage());

            errorMsg = "调用失败: "+ e.getMessage();
            errorStack = ExceptionUtils.getFullStackTrace(e);
            LOG.error(new Date() + "订阅编码" + subscribeKey + " 调用失败 " + e.getMessage());
        }

        //之前时间为s,如果时间低于1s,用户看到运行时间为0
        int excTime = (int)(System.currentTimeMillis()-startTime);
        LOG.info("订阅编码" + subscribeKey + " 本次调用时间： "+excTime+" ms");

        try {
            serviceLogService.insertServiceLogRecord(subscribePO.getResourceId(), subscribePO.getDeptId(),
                    subscribePO.getDeptName(), isSuccess, excTime, inputStr, outputStr, errorMsg, errorStack,
                    subscribePO.getCreator());
        } catch (CommonServiceException e) {
            e.printStackTrace();
            LOG.error("保存数据库服务出错：" + e.getMessage());
        }

        return resultDTO;
    }



    private SQLInfo assembleSQL(SubscribePO subscribePO, String subscribeKey, List<ParamDTO> inputParams,
                                int pageNum, int pageSize) throws CommonServiceException  {

        SqlCommand sqlCommand = new SqlCommand();
        ResourceConfigPO resourceConfigPO = resourceConfigDAO.getConfigById(subscribePO.getResourceId());
        if (resourceConfigPO == null)
            throw new CommonServiceException(CommonConstants.EC_NOT_EXISTED_VALUE,
                    "根据当前订阅编码" + subscribeKey + "对应资源信息" + subscribePO.getResourceId() + "不存在");

        DataBaseInfo dataBaseInfo = cloudETLService.getDbInfoByMetaId(resourceConfigPO.getBindTableId());
        if (dataBaseInfo == null)
            throw new CommonServiceException(CommonConstants.EC_NOT_EXISTED_VALUE,
                    "根据当前订阅编码" + subscribeKey + "对应元数据信息" + resourceConfigPO.getBindTableId() + "不存在");

        String dsName = dataBaseInfo.getName();
        String table = dataBaseInfo.getTableName();

        //获取返回结果字段
        List<SubscribeDbioPO> resultColList
                = subscribeDbioDAO.getBySubscribeIdAndType(subscribePO.getId(), "output");

        StringBuffer resultColStrBuffer = new StringBuffer();

        for (SubscribeDbioPO model : resultColList) {
            resultColStrBuffer.append(resourceColumnDAO.getColumnById(model.getColumnId()).getTableColCode()).append(",");
        }

        String resultColStr
                = resultColStrBuffer.toString().substring(0, resultColStrBuffer.toString().length() - 1);

        StringBuffer sqlBuffer = new StringBuffer();

        sqlBuffer.append("SELECT ");
        sqlBuffer.append(resultColStr).append(" ");
        sqlBuffer.append(" FROM ").append(table).append(" ");

        if (inputParams != null && !inputParams.isEmpty()) {
            sqlBuffer.append(" WHERE 1=1 ");

            for (ParamDTO model : inputParams) {
                sqlBuffer.append(" AND ");
                sqlBuffer.append(model.getParamCode()).append(" = '").append(model.getParamValue()).append("' ");
            }
        }

        sqlCommand.setDbSource(dsName);
        String pageSqlCommad = getPageHelperSQLCommand(sqlBuffer.toString(), resourceConfigPO, pageNum, pageSize);
        if(StringUtils.isEmpty(pageSqlCommad)){
            sqlCommand.setCommand(sqlBuffer.toString());
        }else {
            sqlCommand.setCommand(pageSqlCommad);
        }

        SQLInfo sqlInfo = new SQLInfo();
        sqlInfo.setSqlCommand(sqlCommand);
        sqlInfo.setUserName(resourceConfigPO.getCreator());
        return sqlInfo;
    }

    //根据数据库类型返回 分页语句
    private String getPageHelperSQLCommand(String originCommand, ResourceConfigPO rcPO, int pageNum ,int pageSize){

        String formatInfo = rcPO.getFormatInfo();
        if(StringUtils.startsWithIgnoreCase(formatInfo, "mysql") ||
                StringUtils.startsWithIgnoreCase(formatInfo, "dm")){

            StringBuilder sqlBuilder = new StringBuilder(originCommand.length() + 14);
            sqlBuilder.append(originCommand);
            if (pageNum == 0) {
                sqlBuilder.append(" LIMIT "+pageSize);
            } else {
                sqlBuilder.append(" LIMIT "+pageNum*pageSize+" , " + pageSize);
            }
            return sqlBuilder.toString();
        }else if(StringUtils.startsWithIgnoreCase(formatInfo, "oracle")){
            StringBuilder sqlBuilder = new StringBuilder(originCommand.length() + 120);
            sqlBuilder.append("SELECT * FROM ( ");
            sqlBuilder.append(" SELECT TMP_PAGE.*, ROWNUM ROW_ID FROM ( ");
            sqlBuilder.append(originCommand);
            sqlBuilder.append(" ) TMP_PAGE)");
            sqlBuilder.append(" WHERE ROW_ID <= "+(pageNum+1)*pageSize + " AND ROW_ID > "+ pageNum*pageSize);
            return sqlBuilder.toString();
        }else if(StringUtils.startsWithIgnoreCase(formatInfo, "postgresql")){
            StringBuilder sqlBuilder = new StringBuilder(originCommand.length() + 14);
            sqlBuilder.append(originCommand);
            sqlBuilder.append(" LIMIT "+pageSize+" offset " + pageNum*pageSize);

            return sqlBuilder.toString();
        }
        return null;
    }


    private SQLInfo assembleQueryCountSQL(SubscribePO subscribePO, String subscribeKey, List<ParamDTO> inputParams)
            throws CommonServiceException  {

        SqlCommand sqlCommand = new SqlCommand();
        ResourceConfigPO resourceConfigPO = resourceConfigDAO.getConfigById(subscribePO.getResourceId());
        if (resourceConfigPO == null)
            throw new CommonServiceException(CommonConstants.EC_NOT_EXISTED_VALUE,
                    "根据当前订阅编码" + subscribeKey + "对应资源信息" + subscribePO.getResourceId() + "不存在");

        DataBaseInfo dataBaseInfo = cloudETLService.getDbInfoByMetaId(resourceConfigPO.getBindTableId());
        if (dataBaseInfo == null)
            throw new CommonServiceException(CommonConstants.EC_NOT_EXISTED_VALUE,
                    "根据当前订阅编码" + subscribeKey + "对应元数据信息" + resourceConfigPO.getBindTableId() + "不存在");

        String dsName = dataBaseInfo.getName();
        String table = dataBaseInfo.getTableName();

        //获取返回结果字段
        List<SubscribeDbioPO> resultColList
                = subscribeDbioDAO.getBySubscribeIdAndType(subscribePO.getId(), "output");

        StringBuffer resultColStrBuffer = new StringBuffer();

        for (SubscribeDbioPO model : resultColList) {
            resultColStrBuffer.append(model.getTableColCode()).append(",");
        }

        String resultColStr
                = resultColStrBuffer.toString().substring(0, resultColStrBuffer.toString().length() - 1);

        StringBuffer sqlBuffer = new StringBuffer();
        sqlBuffer.append("SELECT ");
        sqlBuffer.append("count(*) as total");
        sqlBuffer.append(" FROM ").append(table).append(" ");

        if (inputParams != null && !inputParams.isEmpty()) {
            sqlBuffer.append(" WHERE 1=1 ");
            for (ParamDTO model : inputParams) {
                sqlBuffer.append(" AND ");
                sqlBuffer.append(model.getParamCode()).append(" = '").append(model.getParamValue()).append("' ");
            }
        }

        sqlCommand.setDbSource(dsName);
        sqlCommand.setCommand(sqlBuffer.toString());
        //sqlcommand里面增加 schema
        sqlCommand.setDbSchema(dataBaseInfo.getSchema());

        SQLInfo sqlInfo = new SQLInfo();
        sqlInfo.setSqlCommand(sqlCommand);
        sqlInfo.setUserName(resourceConfigPO.getCreator());
        return sqlInfo;
    }

    private SqlQueryResult executeQuery(String subscribeKey, SQLInfo sqlInfo) throws CommonServiceException, RuntimeException  {

        LOG.info("查询执行sql信息为： " + sqlInfo.toString());
        SqlQueryResult sqlQueryResult = sqlExecuteDao.executeQuery(sqlInfo.getUserName(), sqlInfo.getSqlCommand());
        if(sqlQueryResult != null){
            if(sqlQueryResult.isSuccess()){
                return sqlQueryResult;
            }else{
                throw new CommonServiceException(CommonConstants.EC_NOT_EXISTED_VALUE,
                        "订阅编码" + subscribeKey + "执行SQL失败: "+ sqlQueryResult.getMessage());
            }
        }else{
            throw new CommonServiceException(CommonConstants.EC_NOT_EXISTED_VALUE,
                    "订阅编码" + subscribeKey + "获取SQL执行ID失败");
        }
    }

    private void validateBeforeSearch(List<ParamDTO> inputParams, String subscribeKey, SubscribePO subscribePO)
            throws CommonServiceException {
        if (subscribePO == null)
            throw new CommonServiceException(CommonConstants.EC_NOT_EXISTED_VALUE,
                    "根据当前订阅编码" + subscribeKey + "无法查到对应订阅信息");

        Date today = new Date();

        //进行订阅权限校验
        if (today.getTime() > subscribePO.getEndDate().getTime())
            throw new CommonServiceException(CommonConstants.EC_SEARCH_NO_AUTH_ERROR,
                    "当前订阅编码" + subscribeKey + "已过期，无订阅权限");

        //进行查询参数校验
        List<SubscribeDbioPO> searchConditionList
                = subscribeDbioDAO.getBySubscribeIdAndType(subscribePO.getId(), "input");

        //用来比对传入参数是否存在
        List<String> subscribeColumnList = new ArrayList<>();
        if (searchConditionList != null && !searchConditionList.isEmpty()) {

            for (SubscribeDbioPO model : searchConditionList) {
                subscribeColumnList.add(model.getTableColCode());
            }

            if (inputParams != null && !inputParams.isEmpty()) {
                for (ParamDTO model : inputParams) {
                    if(!subscribeColumnList.contains(model.getParamCode())){
                        throw new CommonServiceException(CommonConstants.EC_SEARCH_PARAM_ERROR,
                                "输入参数" + model.getParamName() + " " + model.getParamCode() + "不在订阅项" +
                                        subscribeKey + "查询参数之列");
                    }
                }
            }
        }
//        else
//            throw new CommonServiceException(CommonConstants.EC_NOT_EXISTED_VALUE,
//                    "根据当前订阅编码" + subscribeKey + "无法查到对应订阅信息的查询条件");
    }
}
