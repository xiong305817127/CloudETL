/**
 * 沧州数据资源目录
 */
import qs from "qs";
import {
  API_BASE_METADATATOSWAP,
  API_BASE_CATALOG,
  API_BASE_METADATA,
  API_BASE_SECURITY,
  API_BASE_ES
} from "../constants";
import request from "../utils/request";

//公共请求
//获取所有目录
export async function getAllNode() {
  return request(`${API_BASE_CATALOG}/classify/getAll`);
}
export async function getAllOrgs(query) {
  const querystring = qs.stringify(query);
  return request(
    `${API_BASE_SECURITY}/organization/getAllOrgs.do?${querystring}`
  );
}
//调用安全角色接口
export async function getRoles(query) {
  const querystring = qs.stringify(query);
  return request(`${API_BASE_SECURITY}/role/list.shtml?${querystring}`);
}

// 基础数据
// 删除子节点 id = xxx
export async function deleteNode(query) {
  const querystring = qs.stringify(query);
  return request(`${API_BASE_CATALOG}/classify/delete?${querystring}`);
}
// 增加子节点
// {"id":123,“parentId”:12,“resourceName”:“市政府”，“resourceEncode”:“23”，“dept”:2,}
export async function addNode(query) {
  const options = {
    method: "POST",
    body: JSON.stringify(query)
  };
  return request(`${API_BASE_CATALOG}/classify/save`, options);
}
//获取单个节点信息
export async function getNodeInfo(query) {
  const querystring = qs.stringify(query);
  return request(`${API_BASE_CATALOG}/classify/getNode?${querystring}`);
}
//文件导入
export async function fileImport(query) {
  const options = {
    method: "POST",
    body: JSON.stringify(query)
  };
  return request(`${API_BASE_CATALOG}/classify/processExcel`, options);
}

//我的资源
//查询列表  待更新
export async function getResultList(query) {
  const querystring = qs.stringify(query);
  return request(`${API_BASE_CATALOG}/resource/getAll?${querystring}`);
}
//新增保存接口
export async function saveInfo(query) {
  const options = {
    method: "POST",
    body: JSON.stringify(query)
  };
  return request(`${API_BASE_CATALOG}/resource/add`, options);
}
//批量新增
export async function fileResourceImport(query) {
  const options = {
    method: "POST",
    body: JSON.stringify(query)
  };
  return request(`${API_BASE_CATALOG}/resource/processExcel`, options);
}
//删除多个
export async function deleteInfo(query) {
  const querystring = qs.stringify(query);
  return request(`${API_BASE_CATALOG}/resource/delete?${querystring}`);
}
//变更历史  待更新
export async function getHistoryList(query) {
  const querystring = qs.stringify(query);
  return request(`${API_BASE_CATALOG}/resource/getHistory?${querystring}`);
}
//审批历史 待更新
export async function getCheckList(query) {
  const querystring = qs.stringify(query);
  return request(`${API_BASE_CATALOG}/approve/getHistory?${querystring}`);
}
//根据id编辑
export async function getResource(query) {
  const querystring = qs.stringify(query);
  return request(`${API_BASE_CATALOG}/resource/getResource?${querystring}`);
}
//获得服务列表
export async function getAllServices(query) {
  const querystring = qs.stringify(query);
  return request(`${API_BASE_CATALOG}/service/getAllServices?${querystring}`);
}
//根据dsId查询数据库库表
export async function queryMetaDataByDsId(query) {
  const querystring = qs.stringify(query);
  return request(
    `${API_BASE_METADATA}/metadataTable/ds/queryByDsId?${querystring}`
  );
}

//调用元数据接口 userId / dsType
export async function getDatabaseList(query) {
  const querystring = qs.stringify(query);

  console.log(querystring);

  return request(
    `${API_BASE_METADATA}/dataSwap/getDatabasesByUserIdDsType?${querystring}`
  );
}
//获取物理表名接口 userId / dsType
export async function getDatabaseListByRentId(query) {
  const querystring = qs.stringify(query);

  console.log(querystring);

  return request(
    `${API_BASE_METADATATOSWAP}/getDatabasesByRentIdDsType?${querystring}`
  );
}
//获得表名
export async function getTableList(query) {
  const querystring = qs.stringify(query);
  return request(
    `${API_BASE_METADATA}/dataSwap/getTablesByRentIdDsName?${querystring}`
  );
}
//获得字段名
export async function getFieldList(query) {
  const querystring = qs.stringify(query);
  return request(
    `${API_BASE_METADATATOSWAP}/getFieldsByMetaId?${querystring}`
  );
}

//注册管理
//获取注册待审批资源
//批量注册
export async function getSubmitInfo(query) {
  const querystring = qs.stringify(query);
  return request(`${API_BASE_CATALOG}/approve/submit?${querystring}`);
}
export async function getWaitRegInfo(query) {
  const options = {
    method: "POST",
    body: JSON.stringify(query)
  };
  return request(`${API_BASE_CATALOG}/approve/getWaitReg`, options);
}
//审批
export async function getProcess(query) {
  const options = {
    method: "POST",
    body: JSON.stringify(query)
  };
  return request(`${API_BASE_CATALOG}/approve/process`, options);
}
//批量审批
export async function getBatchProcess(query) {
  const querystring = qs.stringify(query);
  return request(`${API_BASE_CATALOG}/approve/batchProcess?${querystring}`);
}

//我的审批
export async function getProcessedReg(query) {
  const options = {
    method: "POST",
    body: JSON.stringify(query)
  };
  return request(`${API_BASE_CATALOG}/approve/getProcessedReg`, options);
}
//获得审批历史
export async function getHistoryInfo(query) {
  const querystring = qs.stringify(query);
  return request(`${API_BASE_CATALOG}/approve/getHistory?${querystring}`);
}

//获取发布审批列表
export async function getWaitPub(query) {
  const options = {
    method: "POST",
    body: JSON.stringify(query)
  };
  return request(`${API_BASE_CATALOG}/approve/getWaitPub`, options);
}
//我的审批
export async function getProcessedPub(query) {
  const options = {
    method: "POST",
    body: JSON.stringify(query)
  };
  return request(`${API_BASE_CATALOG}/approve/getProcessedPub`, options);
}

//系统参数
export async function getConfig() {
  return request(`${API_BASE_CATALOG}/sysconfig/getConfig`);
}
export async function handleSubmit(query) {
  const options = {
    method: "POST",
    body: JSON.stringify(query)
  };
  return request(`${API_BASE_CATALOG}/sysconfig/save`, options);
}

//资源维护
//获取列表
export async function getMaintenList(query) {
  const options = {
    method: "POST",
    body: JSON.stringify(query)
  };
  return request(`${API_BASE_CATALOG}/approve/maintainQuery`, options);
}
//下架
export async function getRecall(query) {
  const querystring = qs.stringify(query);
  return request(`${API_BASE_CATALOG}/approve/recall?${querystring}`);
}
//退回
export async function getBack(query) {
  const querystring = qs.stringify(query);
  return request(`${API_BASE_CATALOG}/approve/back?${querystring}`);
}
//發佈
export async function getPub(query) {
  const querystring = qs.stringify(query);
  return request(`${API_BASE_CATALOG}/approve/pub?${querystring}`);
}
//獲取文件列表
export async function getResourceFile(query) {
  const querystring = qs.stringify(query);
  return request(
    `${API_BASE_CATALOG}/dataUpload/getResourceFile?${querystring}`
  );
}

//获取服务
export async function getServices(url, options) {
  return request(`${url}`, options);
}

//订阅管理
export async function getSubscriptionList(query) {
  const options = {
    method: "POST",
    body: JSON.stringify(query)
  };

  return request(`${API_BASE_CATALOG}/subscribe/getOverview`, options);
}
//订阅
export async function getConfigInit(query) {
  const querystring = qs.stringify(query);
  return request(`${API_BASE_CATALOG}/subscribe/getConfigInit?${querystring}`);
}
//订阅提交审批
export async function getSubscriptionAdd(query) {
  const options = {
    method: "POST",
    body: JSON.stringify(query)
  };

  return request(`${API_BASE_CATALOG}/subscribe/add`, options);
}
//订阅
export async function getSubDetail(query) {
  const querystring = qs.stringify(query);
  return request(`${API_BASE_CATALOG}/subscribe/getDetail?${querystring}`);
}
//逐级查询目录
export async function getSubtreeAndDepth(query) {
  const querystring = qs.stringify(query);
  return request(
    `${API_BASE_CATALOG}/classify/getSubtreeAndDepth?${querystring}`
  );
}

//待我审批

//获取详情
export async function getSubWaitApprove(query) {
  const options = {
    method: "POST",
    body: JSON.stringify(query)
  };
  return request(`${API_BASE_CATALOG}/subscribe/getWaitApprove`, options);
}

//审批操作
export async function getSubProcess(query) {
  const options = {
    method: "POST",
    body: JSON.stringify(query)
  };
  return request(`${API_BASE_CATALOG}/subscribe/process`, options);
}

//批量审批
//审批操作
export async function getSubBatchProcess(query) {
  const querystring = qs.stringify(query);
  return request(`${API_BASE_CATALOG}/subscribe/batchProcess?${querystring}`);
}

//我审批的
export async function getSubProcessedApprove(query) {
  const options = {
    method: "POST",
    body: JSON.stringify(query)
  };
  return request(`${API_BASE_CATALOG}/subscribe/getProcessedApprove`, options);
}
/*下载模板*/
export async function dataUpload(query) {
  const querystring = qs.stringify(query);
  return request(`${API_BASE_CATALOG}/dataUpload/download?${querystring}`);
}

export async function searchResourceData(query) {
  const options = {
    method: "POST",
    body: JSON.stringify(query)
  };
  return request(`${API_BASE_ES}/dataswap/searchResourceData`, options);
}
