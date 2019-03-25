import qs from 'qs';
import { API_BASE_SECURITY } from '../constants';
import request from '../utils/request';

// 获取列表
export async function getLogoList(data) {
  const querystring = qs.stringify(data);
  return request(`${API_BASE_SECURITY}/auditLog/list.shtml?${querystring}`);
}
