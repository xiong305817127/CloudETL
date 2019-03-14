/**
 * 元数据公共信息接口
 */
import qs from 'qs';
import { API_BASE_METADATA, API_BASE_RESOURCE, DEFAULT_PAGE_SIZE } from '../constants';
import request from '../utils/request';

// 查询部门选项表
export async function getDepartments(query) {
  const querystring = qs.stringify(query);
  return request(`${API_BASE_METADATA}/userInfo/findDeptByUserId?${querystring}`);
}


// 查询行业、标签
export async function getAllResource(query) {
  return request(`${API_BASE_METADATA}/dataResource/getAllResource`);
}

// 查询hdfs树形
export async function getHdfsTree(formData) {
  const option = {
    method: "POST",
    body: JSON.stringify(formData)
  }
  return request(`${API_BASE_METADATA}/hdfs/search`, option);
}

// 查询权限列表
export async function getPermitsList(query) {
  const queryString = qs.stringify(query);
  return request(`${API_BASE_RESOURCE}/authority/getAllAuthority?${queryString}`);
}

//查询跨租户的组织机构
export async function findOrgnazation() {
  return request(`${API_BASE_METADATA}/userInfo/findOrganizations`);
}
