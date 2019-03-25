package com.idatrix.resource.exchange.po;

import com.idatrix.resource.common.utils.DateTools;
import com.idatrix.resource.exchange.vo.request.ExchangeSubscribeVO;
import lombok.Data;

import java.util.Date;

/**
 * Created by Administrator on 2018/11/8.
 */

@Data
public class ExchangeSubscribeTaskPO {

    //主键ID
    private Long id;

    /*和rc_subscribe 里面获取的redis一致*/
    private Long seq;

    /*和rc_subscribe 里面生成规则一致*/
    private String subNo;

    /*rc_subscribe里面的id*/
    private Long subscribeId;

    /*资源信息编码*/
    private String resourceCode;

    /*源数据表的元数据ID*/
    private Long srcMetaId;

    /*目标数据表的元数据ID*/
    private Long destMetaId;

    /*部门信息 订阅部门ID*/
    private Long subscribeDeptId;

    /*部门信息 订阅部门名称*/
    private String subscribeDeptName;

    /*订阅截止日志 一锤子买卖的订阅，暂时无用*/
    private Date endTime;

    private String creator;

    private Date createTime;

    private String modifier;

    private Date modifyTime;



    public ExchangeSubscribeTaskPO(ExchangeSubscribeVO esVO, String user, Long detpId, String deptName){

        this.resourceCode = esVO.getResourceCode();
        this.endTime = DateTools.parseDate(esVO.getEndTime());

        this.subscribeDeptId = detpId;
        this.subscribeDeptName = deptName;

        this.creator = user;
        this.createTime = new Date();
        this.modifier = user;
        this.modifyTime = new Date();
    }

    public ExchangeSubscribeTaskPO(ExchangeSubscribeInfoPO taskVO, Long detpId, String deptName){

        this.resourceCode = taskVO.getResourceCode();
        this.endTime = taskVO.getEndTime();

        this.subscribeDeptId = detpId;
        this.subscribeDeptName = deptName;

        this.creator = taskVO.getCreator();
        this.createTime = new Date();
        this.modifier = taskVO.getCreator();
        this.modifyTime = new Date();
    }

}
