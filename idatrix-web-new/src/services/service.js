// 1.主端口API_BASE
import qs from 'qs';
import { API_BASE_SERVICE, API_BASE_SECURITY } from '../constants';
import request from '../utils/request';
const requestUrl = {
  //服务开放&应用中心：
  serviceListRequest: API_BASE_SERVICE + '/app/list',
  serviceAddRequest: API_BASE_SERVICE + '/app/add',
  serviceEditRequest: API_BASE_SERVICE + '/app/update',
  serviceDeleteRequest: API_BASE_SERVICE + '/app/delete',
  //服务开放&服务中心:
  serviceSearchRequest: API_BASE_SERVICE + '/service/list',
  serviceDetailsRequest: API_BASE_SERVICE + '/service/detail',
  //安全管理&敏感规则：
  searchSensitiveInfoList:API_BASE_SECURITY + '/sensitiveInfo/index',
  addSensitiveInfoList:API_BASE_SECURITY + '/sensitiveInfo/add',
  updateSensitiveInfoList:API_BASE_SECURITY + '/sensitiveInfo/update',
  deleteSensitiveInfoList:API_BASE_SECURITY + '/sensitiveInfo/delete',
}
/*2.服务治理：GET方法*/
/* 2.1应用列表*/
export async function getTableList(obj) {

  // 原来为GET请求修改为POST
  // edited by steven Leo on 2018.11.10
  const option = {
    method: "POST",
    headers:{
      "Content-Type": "application/x-www-form-urlencoded"
    },
    body: qs.stringify(obj)
  };
  return request(requestUrl.serviceListRequest,option);
}
/*2.2查询请求：url与列表请求一致*/
// export async function searchTableList(obj) {

//   // 原来为GET请求修改为POST
//   // edited by steven Leo on 2018.11.10
//   const option = {
//     method: "POST",
//     body: JSON.stringify(obj)
//   };
//   return request(requestUrl.serviceListRequest,option);
// }
/*2.3新增请求*/
export async function addTableList(obj) {
  const option = {
    method: "POST",
    body: JSON.stringify(obj)
  };
  return request(requestUrl.serviceAddRequest, option);
}
/*2.4修改请求*/
export async function editTableList(obj) {
  const option = {
    method: "POST",
    body: JSON.stringify(obj)
  };
  return request(requestUrl.serviceEditRequest, option);
}
/*2.5删除请求*/
export async function deleteTableList(obj) {
  const option = {
    method: "POST",
    body: JSON.stringify(obj)
  };
  return request(requestUrl.serviceDeleteRequest, option);
}
/*3.1服务搜索：*/
export async function  getServiceList(obj) {
  // return request(requestUrl.serviceSearchRequest+"/"+obj.pageNum+"/"+obj.pageSize,option)
  return request(requestUrl.serviceSearchRequest);
}
/*3.2服务详情：*/
export async function  getServiceDetails(obj) {
  return request(requestUrl.serviceDetailsRequest+"/"+obj.serviceId);
}
/*3.3服务详细：请求参数：可删除*/
export async function  getServiceParam(obj) {
  return request(requestUrl.serviceDetailsRequest+"/"+obj.serviceId);
}
// 获取敏感规则:搜索
export async function getSensitiveInfoList(obj) {
  const option = {
    method: 'POST',
    headers: { 'Content-Type': 'application/x-www-form-urlencoded;charset=UTF-8' },
    body: qs.stringify(obj),
  };
    return request(requestUrl.searchSensitiveInfoList, option);
}
// 新增敏感规则
export async function addSensitiveInfo(obj) {
  const option = {
    method: 'POST',
    headers: { 'Content-Type': 'application/x-www-form-urlencoded;charset=UTF-8' },
    body: qs.stringify(obj),
  };
  return request(requestUrl.addSensitiveInfoList, option);
}
// 修改敏感规则
export async function updateSensitiveInfo(obj) {
  const option = {
    method: 'POST',
    headers: { 'Content-Type': 'application/x-www-form-urlencoded;charset=UTF-8' },
    body: qs.stringify(obj),
  };
  return request(requestUrl.updateSensitiveInfoList, option);
}
// 删除敏感规则：删除不成功，700
export async function deleteSensitiveInfo(obj) {
  const option = {
    method: 'POST',
    headers: { 'Content-Type': 'application/x-www-form-urlencoded;charset=UTF-8' },
    body: qs.stringify(obj),
  };
  return request(requestUrl.deleteSensitiveInfoList, option);
}
