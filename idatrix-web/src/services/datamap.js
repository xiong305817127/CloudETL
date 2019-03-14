/**
 * 用户个人账户相关接口
 */
import qs from 'qs';
import { API_BASE_GRAPH } from 'constants';
import request from 'utils/request';

// 获取数据地图
export async function queryRelationship(formData) {
  const option = {
    method: 'POST',
    body: JSON.stringify(formData),
  };
  return request(`${API_BASE_GRAPH}/queryRelationship`, option);
}

// 获取节点信息
export async function queryNode(formData) {
  // const querystring = qs.stringify(query);
  const option = {
    method: 'POST',
    body: JSON.stringify(formData),
  };
  return request(`${API_BASE_GRAPH}/queryNode`, option);
}
