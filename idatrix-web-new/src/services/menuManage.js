import qs from 'qs';
import { API_BASE_SECURITY } from '../constants';
import request from '../utils/request';

// 获取列表
export async function getRoleList(data) {
  const querystring = qs.stringify(data);
  const query = querystring ? `?${querystring}` : '';
  return request(`${API_BASE_SECURITY}/role/list.shtml${query}`);
}
