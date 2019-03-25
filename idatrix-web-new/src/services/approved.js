/**
 * 数据资源目录 - 已审批
 */
import qs from 'qs';
import { API_BASE_RESOURCE } from '../constants';
import request from '../utils/request';
import { deepCopy } from '../utils/utils';

// 获取列表
export async function getList(query) {
  const querystring = qs.stringify(query);
  return request(`${API_BASE_RESOURCE}/myResource/getMyApproved?${querystring}`);
}

// 回收权限
export async function takeBackPermits(query) {
  const formData = query.map(id => ({ id }));
  const option = {
    method: 'POST',
    body: JSON.stringify(formData),
  };
  return request(`${API_BASE_RESOURCE}/myResource/batchToReCycle`, option);
}
