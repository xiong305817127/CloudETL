/**
 * 用户个人账户相关接口
 */
import qs from 'qs';
import { API_BASE_SECURITY } from '../constants';
import request from '../utils/request';


// 修改用户个人信息
export async function updateUser(obj) {
 /* obj['_method'] = 'put';*/
  const option = {
    method: 'POST',
    headers: { 'Content-Type': 'application/x-www-form-urlencoded;charset=UTF-8' },
    body: qs.stringify(obj),
  };
  return request(`${API_BASE_SECURITY}/token/update.shtml`, option);
}
