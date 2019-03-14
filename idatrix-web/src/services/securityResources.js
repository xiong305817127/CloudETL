/**
 * 安全子系统 - 资源管理
 */
import qs from 'qs';
import { API_BASE_SECURITY } from '../constants';
import request from '../utils/request';

// 获取列表
export async function getResourcesList() {
  return request(`${API_BASE_SECURITY}/permission/list.shtml?pageSize=10000000`);
}

// 新增资源
export async function addResource(payload) {
  const option = {
    method: 'POST',
    headers: { 'Content-Type': 'application/x-www-form-urlencoded;charset=UTF-8' },
    body: qs.stringify(payload),
  };
  return request(`${API_BASE_SECURITY}/permission/add.shtml`, option);
}

// 修改资源
export async function modifyResource(payload) {
  const option = {
    method: 'POST',
    headers: { 'Content-Type': 'application/x-www-form-urlencoded;charset=UTF-8' },
    body: qs.stringify(payload),
  };
  return request(`${API_BASE_SECURITY}/permission/update.shtml`, option);
}

// 删除资源
export async function deleteResource(payload) {
  const option = {
    method: 'POST',
    headers: { 'Content-Type': 'application/x-www-form-urlencoded;charset=UTF-8' },
    body: qs.stringify(payload),
  };
  return request(`${API_BASE_SECURITY}/permission/deletePermissionById`, option);
}
