import qs from 'qs';
import { API_BASE_SECURITY } from '../constants';
import request from '../utils/request';

// 第一步：账户校正registerAccount
export async function registerAccount(obj) {
  const option = {
    method: 'POST',
    headers: { 'Content-Type': 'application/x-www-form-urlencoded;charset=UTF-8' },
    body: qs.stringify(obj),
  };
  return request(`${API_BASE_SECURITY}/u/find-pwsd.shtml`, option);
}
//第二步：从邮箱或手机中获取验证码registerVerify
export async function registerVerify(objCode) {
  const option = {
    method: 'POST',
    headers: { 'Content-Type': 'application/x-www-form-urlencoded;charset=UTF-8' },
    body: qs.stringify(objCode),
  };
  return request(`${API_BASE_SECURITY}/u/find-pwsd-two`, option);
}
// 第三步：重置密码registerPassword，
export async function registerPassword(objPwd) {
  const option = {
    method: 'POST',
    headers: { 'Content-Type': 'application/x-www-form-urlencoded;charset=UTF-8' },
    body: qs.stringify(objPwd),
  };
  return request(`${API_BASE_SECURITY}/u/find-pwsd-three`, option);
}
