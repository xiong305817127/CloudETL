import { API_BASE_METADATA } from '../constants';
import request from '../utils/request';

const qingqiuUrl={
 
   /*查询数据库信息 连接信息*/   
   SJCJgetdbinfo:API_BASE_METADATA + '/directDataCollect/getdbinfo',
   
   /*查询表信息*/
  //  SJCJgetTableInfo:API_BASE_METADATA + '/directDataCollect/getTableInfo',
   SJCJgetTableInfo:API_BASE_METADATA + '/directDataCollect/getDbTables',
   
   /*查询表字段信息*/
   SJCJgetDbFieldInfo:API_BASE_METADATA + '/directDataCollect/getDbFieldInfo',
 
  /*数据库表信息采集接口*/
   SJCJinsertTableFields:API_BASE_METADATA + '/directDataCollect/insertTableFields',

   /*校验数据库表名称*/
  /* SJCJisExists:API_BASE_METADATA +'metadataTable/isExists',*/

  //获取表详细信息
  GetTableDetails: API_BASE_METADATA + "/directDataCollect/getTableFields",
  XJCJModify:API_BASE_METADATA+'/metadataPros/Modify',
  /*删除表*/
  XJCJbatchToDelete:API_BASE_METADATA + '/metadataTable/batchToDelete',
  /*删除数据库*/
  XJCJdelete:API_BASE_METADATA + '/dataSource/delete',

  /*根据数据源信息查询schema列表*/
  GETSCHEMALIST:API_BASE_METADATA + "/directDataCollect/getDbSchemas",

  /**保存直采信息*/
  SAVETABLEANDFIELDS: API_BASE_METADATA + "/directDataCollect/saveTableAndFields"
}

export async function SaveTableAndFields(table){
  const option = {method: "POST", body:JSON.stringify(table)}
  return request(qingqiuUrl.SAVETABLEANDFIELDS, option);
}

// 获取表格信息
// edited by steven leo 2018/09/25
export async function GetTableDetailsByTableName(tableInfo){
  const option = {
    method : "POST",
    body: JSON.stringify(tableInfo)
  }
  return request(qingqiuUrl.GetTableDetails,option);
}
/*采集删除 数据库*/
export async function XJCJdelete(obj) { 
  const option = {
    method: "POST",
    body: JSON.stringify(obj)
  }      
  return request(qingqiuUrl.XJCJdelete,option);
}
/*采集删除 表*/
export async function XJCJbatchToDelete(obj) { 
  const option = {
    method: "PUT",
    body: JSON.stringify(obj)
  }      
  return request(qingqiuUrl.XJCJbatchToDelete,option);
}
/* 采集表字段修改*/
export async function XJCJModify(obj) { 
  const option = {
    method: "POST",
    body: JSON.stringify(obj)
  }      
  return request(qingqiuUrl.XJCJModify,option);
}
/*查询数据库信息 连接信息*/
export async function SJCJgetdbinfo(obj) { 
  const option = {
    method: "POST",
    body: JSON.stringify(obj)
  }      
  return request(qingqiuUrl.SJCJgetdbinfo,option);
}

/*根据数据源信息查询schema列表*/
export async function GETSCHEMALIST(obj){
  const option = {
    method: "POST",
    body: JSON.stringify(obj)
  }
  return request(qingqiuUrl.GETSCHEMALIST,option);
}

 /*查询表信息*/
export async function SJCJgetTableInfo(obj) { 
  const option = {
    method: "POST",
    body: JSON.stringify(obj)
  }      

  return request(qingqiuUrl.SJCJgetTableInfo,option);
}
/*查询表字段信息*/
export async function SJCJgetDbFieldInfo(obj) { 
  const option = {
    method: "POST",
    body: JSON.stringify(obj)
  }      
  return request(qingqiuUrl.SJCJgetDbFieldInfo+'?dsType='+obj.dsType,option);
}
  /*数据库表信息采集接口*/
export async function SJCJinsertTableFields(obj) { 
  const option = {
    method: "POST",
    body: JSON.stringify(obj)
  }   
  return request(qingqiuUrl.SJCJinsertTableFields,option);
}
export async function SJCJisExists(obj) {
  const option = {
    method: "POST",
    body: JSON.stringify(obj)
  }
  return request(API_BASE_METADATA + "/metadataTable/isExists",option)
}
