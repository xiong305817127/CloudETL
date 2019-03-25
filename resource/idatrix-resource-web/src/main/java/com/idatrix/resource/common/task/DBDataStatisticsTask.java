package com.idatrix.resource.common.task;

import com.idatrix.resource.catalog.dao.ResourceConfigDAO;
import com.idatrix.resource.catalog.po.ResourceConfigPO;
import com.idatrix.resource.catalog.service.IResourceStatiscsService;
import com.idatrix.resource.common.vo.SQLInfo;
import com.idatrix.resource.datareport.dao.ResourceImportStatisticsDAO;
import com.idatrix.resource.datareport.po.ResourceImportStatisticsPO;
import com.ys.idatrix.db.api.common.RespResult;
import com.ys.idatrix.db.api.sql.dto.SqlExecReqDto;
import com.ys.idatrix.db.api.sql.dto.SqlQueryRespDto;
import com.ys.idatrix.db.api.sql.service.SqlExecService;
import com.ys.idatrix.metacube.api.beans.MetadataDTO;
import com.ys.idatrix.metacube.api.beans.ResultBean;
import com.ys.idatrix.metacube.api.service.MetadataToDataSwapService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

/**
 * 统计通过其他方式导入到最终 资源目录绑定表中 数据量
 */

@Slf4j
@Component
public class DBDataStatisticsTask {

    @Autowired
    private ResourceConfigDAO resourceConfigDAO;

    @Autowired
    private ResourceImportStatisticsDAO resourceImportStatisticsDAO;

    @Autowired(required = false)
    private SqlExecService sqlExecuteDao;

    @Autowired(required = false)
    private MetadataToDataSwapService metacubeCatalogService;

    @Autowired
    private IResourceStatiscsService resourceStatiscsService;

    public void startTask() {

        List<ResourceImportStatisticsPO> poList = resourceImportStatisticsDAO.getAllImportStatitics();
        if(CollectionUtils.isEmpty(poList)){
            return;
        }

        for(ResourceImportStatisticsPO po: poList){
            try {
                ResourceConfigPO rcPO = resourceConfigDAO.getConfigById(po.getResourceId());
                if(rcPO==null){
                    throw new Exception("数据量统计，对应资源信息" + po.getResourceId() + "不存在");
                }

                SQLInfo sqlInfo = assembleQueryCountSQL(rcPO);
                //TODO:用于优化count查询利用时间区间查询能够大大提供count效率
                po.setLastCountTime(sqlInfo.getQueryTime());
                SqlQueryRespDto result = executeQuery(sqlInfo.getUserName(), sqlInfo.getSqlCommand());
                Object value = result.getData().get(0).get("total");
                Long dbValue = (Long)value;
                po.setCreator(rcPO.getCreator());
                po.setUpdater(rcPO.getCreator());
                po.setUpdateTime(new Date());
                po.setCreateTime(new Date());
                po.setMetaId(rcPO.getBindTableId());
                po.setTotalRecord(dbValue);
                resourceImportStatisticsDAO.updateByPrimaryKey(po);
                if(dbValue>0){
                    resourceStatiscsService.refreshDataCount(po.getResourceId(), dbValue);
                }
            }catch (Exception ex){
                ex.printStackTrace();
            }

        }

    }

    private SQLInfo assembleQueryCountSQL(ResourceConfigPO rcPO)
            throws Exception {

        Long bindTableId = rcPO.getBindTableId();
        MetadataDTO metadataDTO = new MetadataDTO();
        ResultBean<MetadataDTO> respon = metacubeCatalogService.findTableInfoByID(bindTableId);
        if(!respon.isSuccess()){
            log.error("元数据表metaId "+bindTableId+",获取元数据信息失败："+respon.getMsg());
            throw new Exception("元数据表metaId "+bindTableId+",获取元数据信息失败："+respon.getMsg());
        }
        metadataDTO = respon.getData();

        Date queryTime = new Date();
        StringBuffer sqlBuffer = new StringBuffer();
        sqlBuffer.append("SELECT ");
        sqlBuffer.append("count(*) as total");
        sqlBuffer.append(" FROM ").append(metadataDTO.getMetaName()).append(" ");

        SqlExecReqDto sqlExecuteDto = new SqlExecReqDto();
        sqlExecuteDto.setSchemaId(new Long(metadataDTO.getSchemaId()));
        //sqlExecuteDto.setSchemaName(metadataDTO.getSchemaName());
        sqlExecuteDto.setCommand(sqlBuffer.toString());

        return new SQLInfo(rcPO.getCreator(), sqlExecuteDto, queryTime);
    }

    private SqlQueryRespDto executeQuery(String user, SqlExecReqDto command) throws Exception  {

        log.info("数据量统计执行用户名 "+user + " 执行语句为 " + command);
        RespResult<SqlQueryRespDto> sqlQueryResult = sqlExecuteDao.executeQuery(user, command);
        if(sqlQueryResult==null){
            log.error("启动数据量统执行失败");
            throw new Exception("启动数据量统执行失败");
        }else if(sqlQueryResult.isSuccess()){
            return sqlQueryResult.getData();
        }else{
            log.error("数据量统执行SQL失败: "+ sqlQueryResult.getMsg());
            throw new Exception("数据量统执行SQL失败: "+ sqlQueryResult.getMsg());
        }
    }
}
