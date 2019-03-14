/**
 * 数据资源目录 - 数据资源目录接口
 */
import qs from 'qs';
import { API_BASE_RESOURCE } from '../constants';
import request from '../utils/request';

// 获取
export async function getAllResource() {
  // return request(`${API_BASE_RESOURCE}/DataResourceController/getAllTagAndTheme`);
  return request(`${API_BASE_RESOURCE}/dataResource/getAllResource`);
}

// 搜索数据表类
export async function searchTable(formData, query) {
  const option = {
    method: 'POST',
    body: JSON.stringify(formData),
  };
  const querystring = qs.stringify(query);
  return request(`${API_BASE_RESOURCE}/dataResource/searchTables?${querystring}`, option);
}

// 搜索文件目录类
export async function searchFile(formData, query) {
  const option = {
    method: 'POST',
    body: JSON.stringify(formData),
  };
  const queryString = qs.stringify(query);
  return request(`${API_BASE_RESOURCE}/dataResource/searchFiles?${queryString}`, option);
}

export async function getMeta(query) {
  const queryString = qs.stringify(query);
  return request(`${API_BASE_RESOURCE}/metadataPros/queryByMetaId?${queryString}`);
}

// 查询部门列表
export async function getDepartment(query) {
  const queryString = qs.stringify(query);
  return request(`${API_BASE_RESOURCE}/userInfo/queryDeptTreeByUserId?${queryString}`);
}

// 查询权限列表
export async function getPermitsList(query) {
  const queryString = qs.stringify(query);
  return request(`${API_BASE_RESOURCE}/authority/getAllAuthority?${queryString}`);
}

// 查询权限结果
export async function getPermitsResults(query) {
  const queryString = qs.stringify(query);
  return request(`${API_BASE_RESOURCE}/myResource/queryAuthorityByResourceId?${queryString}`);
}

// 申请权限
export async function applyPermits(formData) {
  const option = {
    method: 'POST',
    body: JSON.stringify(formData),
  };

  return request(`${API_BASE_RESOURCE}/myResource/batchInsert`, option);
}

// 数据地图接口
export async function querySanKey(query) {
  const queryString = qs.stringify(query);
  return request(`${API_BASE_RESOURCE}/dataRelation/querySanKey?${queryString}`);
}
// 数据地图 - 查询字段关系
export async function queryFieldRelation(query) {
  const queryString = qs.stringify(query);
  return request(`${API_BASE_RESOURCE}/dataRelation/queryTableAndFiledById?${queryString}`);
}
