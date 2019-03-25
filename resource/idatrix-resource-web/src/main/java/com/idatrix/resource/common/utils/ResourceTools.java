package com.idatrix.resource.common.utils;

import org.apache.commons.lang3.StringUtils;

/**
 * Created by Robin Wing on 2018-5-24.
 */
public class ResourceTools {

   public enum FormatType {
       /*不确定*/
       NOT_SURE(0,"不确定"),
       /*电子文件*/
        FILE(1,"电子文件"),
       /*电子表格*/
        FORM(2,"电子表格"),
       /*数据库*/
        DB(3,"数据库"),
       /*图形图像*/
        IMAGE(4,"图形图像"),
       /*流媒体*/
        STEAM_MEDIA(5,"流媒体"),
       /*自定义格式*/
        SELF_FORMAT(6,"自定义格式"),
       /*网络接口*/
        SERVICE_INTERFACE(7,"网络接口");

        int typeValue;
        String formatInfoZH;

       public int getTypeValue() {
           return typeValue;
       }

       public void setTypeValue(int typeValue) {
           this.typeValue = typeValue;
       }

       public String getFormatInfoZH() {
           return formatInfoZH;
       }

       public void setFormatInfoZH(String formatInfoZH) {
           this.formatInfoZH = formatInfoZH;
       }

       FormatType(int typeValue){this.typeValue=typeValue;};

       FormatType(int typeValue,String formatInfoZH){
           this.typeValue=typeValue;
           this.formatInfoZH = formatInfoZH;
       };

       public static FormatType getFormatType(int value){
           for(FormatType formatType: values()){
               if(formatType.getTypeValue()==value){
                   return formatType;
               }
           }
           return NOT_SURE;
       }

       public static String getFormatInfoZH(int value){
           for(FormatType formatType: values()){
               if(formatType.getTypeValue()==value){
                   return formatType.getFormatInfoZH();
               }
           }
           return NOT_SURE.getFormatInfoZH();
       }
    }

    public enum RefreshCycle {
        /*不确定*/
        NOT_SURE(0),
        /*实时*/
        REAL_TIME(1),
        /*每日*/
        BY_DAY(2),
        /*每周*/
        BY_WEEK(3),
        /*每月*/
        BY_MONTH(4),
        /*每季度*/
        BY_QUARTER(5),
        /*每半年*/
        BY_HALF_YEAR(6),
        /*每年*/
        BY_YEAR(7);

        int refreshValue;

        public int getRefreshValue() {
            return refreshValue;
        }

        public void setRefreshValue(int refreshValue) {
            this.refreshValue = refreshValue;
        }

        RefreshCycle(int refreshValue){this.refreshValue=refreshValue;};

        public static RefreshCycle getRefreshCycle(int value){
            for(RefreshCycle refreshCycle: values()){
                if(refreshCycle.getRefreshValue()==value){
                    return refreshCycle;
                }
            }
            return NOT_SURE;
        }
    }

    public enum ResourceAction{
        /*增加*/
        CREATE(1, "增加", "create"),
        /*修改*/
        UPDATE(2, "修改", "update"),
        /*删除*/
        DELETE(3, "删除", "delete"),
        /*上架*/
        MAINTAIN_PUB(4, "资源维护-上架", "maintain_pub"),
        /*下架*/
        MAINTAIN_RECALL(5, "资源维护-下架", "maintain_recall"),
        /*退回修改*/
        MAINTAIN_BACK(6, "资源维护-退回修改", "maintain_back");

        int actionValue;
        String action;
        String actionCode;

        public int getActionValue() {
            return actionValue;
        }

        public void setActionValue(int actionValue) {
            this.actionValue = actionValue;
        }

        public String getAction() {
            return action;
        }

        public void setAction(String action) {
            this.action = action;
        }

        public String getActionCode() {
            return actionCode;
        }

        public void setActionCode(String actionCode) {
            this.actionCode = actionCode;
        }

        ResourceAction(int actionValue, String action, String actionCode){
            this.action = action;
            this.actionCode = actionCode;
            this.actionValue = actionValue;
        }

        public static String getActionCode(String action){
            for(ResourceAction resourceAction: values()){
                if(StringUtils.equals(action, resourceAction.getAction())){
                    return resourceAction.getActionCode();
                }
            }
            return null;
        }

        public static String getAction(String actionCode){
            for(ResourceAction resourceAction: values()){
                if(StringUtils.equals(actionCode, resourceAction.getActionCode())){
                    return resourceAction.getAction();
                }
            }
            return null;
        }
    }

    /*资源管理角色： 提交者， 部门管理员， 中心管理
    * 流程介绍：
    *  1. 提交者， 配置好资源保存以后 就是 草稿状态（DRAFT), 点击注册以后，资源状态变成 注册带审核 (WAIT_REG_APPROVE)
    *  2. 部门管理员 处理-点击审批同意，资源变成 注册成功(REG_SUCCESS), 部门管理员申请上架 ，资源编程，待发布审核(WAIT_PUB_APPROVE)
    *               处理-点击审批不同意，变成退回修改(WAIT_UPDATE)
    *  3. 中心管理员 处理-点击审批同意，资源状态编程 已发布(PUB_SUCCESS),中心管理员可以下架，点击 下架，资源编程下架状态，
    *                    后面管理员继续处理 退回修改(WAIT_UPDATE)
    *               处理-点击审批不同意，变成退回修改(WAIT_UPDATE)
    * */
    public enum ResourceStatus{
        /*不确定*/
        NOT_SURE(0, "不确定", "not_sure"),
        /*草稿*/
        DRAFT(1, "草稿", "draft"),
        /*已删除*/
        DELETE(2, "已删除", "delete"),
        /*退回修改*/
        WAIT_UPDATE(3, "退回修改", "wait_update"),
        /*待注册审批*/
        WAIT_REG_APPROVE(4, "待注册审批", "wait_reg_approve"),
        /*已注册*/
        REG_SUCCESS(5, "已注册", "reg_success"),
        /*待发布审批*/
        WAIT_PUB_APPROVE(6, "待发布审批", "wait_pub_approve"),
        /*已发布*/
        PUB_SUCCESS(7, "已发布", "pub_success"),
        /*下架*/
        RECALL(8, "下架", "recall");

        int statusValue;
        String status;
        String statusCode;

        public String getStatusCode() {
            return statusCode;
        }

        public void setStatusCode(String statusCode) {
            this.statusCode = statusCode;
        }

        public int getStatusValue() {
            return statusValue;
        }

        public void setStatusValue(int statusValue) {
            this.statusValue = statusValue;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        ResourceStatus(int statusValue, String status){
            this.statusValue=statusValue;
            this.status = status;
        }

        ResourceStatus(int statusValue, String status, String statusCode){
            this.statusValue=statusValue;
            this.status = status;
            this.statusCode = statusCode;
        }

        public static String getStatus(int value){
            for(ResourceStatus resourceStatus: values()){
                if(resourceStatus.getStatusValue()==value){
                    return resourceStatus.getStatus();
                }
            }
            return null;
        }

        public static String getStatusCode(int value){
            for(ResourceStatus resourceStatus: values()){
                if(resourceStatus.getStatusValue()==value){
                    return resourceStatus.getStatusCode();
                }
            }
            return null;
        }

        public static ResourceStatus getResourceStatus(int value){
            for(ResourceStatus resourceStatus: values()){
                if(resourceStatus.getStatusValue()==value){
                    return resourceStatus;
                }
            }
            return NOT_SURE;
        }

        public static ResourceStatus getResourceStatus(String valueCode){
            for(ResourceStatus resourceStatus: values()){
                if(resourceStatus.getStatusCode().equals(valueCode)){
                    return resourceStatus;
                }
            }
            return NOT_SURE;
        }

        public static String getStatusByCode(String valueCode){
            for(ResourceStatus resourceStatus: values()){
                if(resourceStatus.getStatusCode().equals(valueCode)){
                    return resourceStatus.getStatus();
                }
            }
            return NOT_SURE.getStatus();
        }

        public static int getRefreshCycleValue(ResourceStatus rs){
            return rs.getStatusValue();
        }
    }

}
