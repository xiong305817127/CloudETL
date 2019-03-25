/**
 * 安全子系统 - 组织机构管理
 */

import qs from 'qs';
import { API_BASE_SECURITY } from '../constants';
import request from '../utils/request';

// 导出api
export const exportApi = `${API_BASE_SECURITY}/organization/export.shtml`;
// 导入api
export const importApi = `${API_BASE_SECURITY}/organization/import.shtml`;

// 获取列表
export async function getList(data) {
  const querystring = qs.stringify(data);
  const query = querystring ? `?${querystring}` : '';
  // return request(`${API_BASE_SECURITY}/organization/rent-organization.shtml${query}`);
  return request(`${API_BASE_SECURITY}/organization/list.shtml${query}`);
}

// 新增组织
export async function addOrganization(payload) {
  const option = {
    method: 'POST',
    headers: { 'Content-Type': 'application/x-www-form-urlencoded;charset=UTF-8' },
    body: qs.stringify(payload),
  };
  return request(`${API_BASE_SECURITY}/organization/add.shtml`, option);
}

// 修改组织
export async function modifyOrganization(payload) {
  const option = {
    method: 'POST',
    headers: { 'Content-Type': 'application/x-www-form-urlencoded;charset=UTF-8' },
    body: qs.stringify(payload),
  };
  return request(`${API_BASE_SECURITY}/organization/update.shtml`, option);
}

// 删除组织
export async function deleteOrganization(payload) {
  const option = {
    method: 'POST',
    headers: { 'Content-Type': 'application/x-www-form-urlencoded;charset=UTF-8' },
    body: qs.stringify(payload),
  };
  return request(`${API_BASE_SECURITY}/organization/delete.shtml`, option);
}

// 根据组织id查询用户列表
export async function getUsersByOrgId(id) {
  const option = {
    method: 'POST',
    headers: { 'Content-Type': 'application/x-www-form-urlencoded;charset=UTF-8' },
    body: qs.stringify({
      deptId: id,
    }),
  }
  return request(`${API_BASE_SECURITY}/member/findUsersByOrganizatioId`, option);
}

// 添加用户到组织机构
export async function addUserToOrg(payload) {
  const option = {
    method: 'POST',
    headers: { 'Content-Type': 'application/x-www-form-urlencoded;charset=UTF-8' },
    body: qs.stringify(payload),
  };
  return request(`${API_BASE_SECURITY}/organization/addUserToOrg.shtml`, option);
}


// 批量删除组织
export async function batchDeleteOrganization(payload) {
  const option = {
    method: 'DELETE',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(payload),
  };
  return request(`${API_BASE_SECURITY}/organization/batchDelete`, option);
}

// 查询所属组织
export async function findAscriptionDeptList(payload) {
	const querystring = qs.stringify(payload);
  const option = {
    method: 'GET',
		headers: { 'Content-Type': 'application/x-www-form-urlencoded;charset=UTF-8' },
	};
  return request(`${API_BASE_SECURITY}/organization/findAscriptionDeptList.shtml?${querystring}`, option);
}