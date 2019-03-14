/**
 * 数据资源目录 - 我的申请
 */
import qs from 'qs';
import { API_BASE_RESOURCE } from '../constants';
import request from '../utils/request';
import { deepCopy } from '../utils/utils';

// 获取列表
export async function getList(query) {
  const querystring = qs.stringify(query);
  return request(`${API_BASE_RESOURCE}/myResource/getMyApprove?${querystring}`);
}

// 重新申请
export async function doReapply(query) {
  const formData = query.map(id => ({ id }));
  const option = {
    method: 'POST',
    body: JSON.stringify(formData),
  };
  return request(`${API_BASE_RESOURCE}/myResource/batchToReApprove`, option);
}

// 撤回申请
export async function undoApply(query) {
  const formData = query.map(id => ({ id }));
  const option = {
    method: 'POST',
    body: JSON.stringify(formData),
  };
  return request(`${API_BASE_RESOURCE}/myResource/batchToRevoke`, option);
}
