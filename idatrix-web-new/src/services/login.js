/**
 * 用户登录相关
 * 此文件的接口都假定用户尚未登录
 * 已通过授权才能调用的接口不能在此文件定义
 */
import qs from 'qs';
import { API_BASE_SECURITY } from '../constants';
import request from '../utils/request';

// 获取验证码
export async function getCaptchaImg() {
  const timestamp = new Date().getTime();
  return request(`${API_BASE_SECURITY}/u/preLogin.shtml?timestamp=${timestamp}`);
}


// 登录表单提交
export async function submitLogin(obj) {
  const option = {
    method: 'POST',
    headers: { 'Content-Type': 'application/x-www-form-urlencoded;charset=UTF-8' },
    body: qs.stringify(obj),
  };
  // 返回虚拟登录数据
  // return {data:{
  //   status: 200, message: '', backUrl: '', vt: "5951e3ffe9314841a1d92c9ae922000e",
  //   loginUser: {age:25, cardId: "366534653979347682", createTime: 1466046933000, email: "admin", id: 1, phone: "13623215682", realName: "小芳", sex: 2, status: 1, username: "admin1"},
  //   sysList: [{id:'dataResDir'},{id:'metadata'},{id:'servicebase'},{id:'datalab'},{id:'cloudetl'},{id:'monitor'},{id:'ITIL'},{id:'security'}]
  // }};
  return request(`${API_BASE_SECURITY}/u/submitLogin.shtml`, option);
}

// 登录表单提交
export async function submitLoginForThirdParty(obj) {
  const option = {
    method: 'POST',
    headers: { 'Content-Type': 'application/x-www-form-urlencoded;charset=UTF-8' },
    body: qs.stringify(obj),
  };
  // 返回虚拟登录数据
  // return {data:{
  //   status: 200, message: '', backUrl: '', vt: "5951e3ffe9314841a1d92c9ae922000e",
  //   loginUser: {age:25, cardId: "366534653979347682", createTime: 1466046933000, email: "admin", id: 1, phone: "13623215682", realName: "小芳", sex: 2, status: 1, username: "admin1"},
  //   sysList: [{id:'dataResDir'},{id:'metadata'},{id:'servicebase'},{id:'datalab'},{id:'cloudetl'},{id:'monitor'},{id:'ITIL'},{id:'security'}]
  // }};
  return request(`${API_BASE_SECURITY}/u/submitLoginForThirdParty.shtml`, option);
}

// 获取用户子系统权限
export async function fetchPermission(formData) {
  const option = {
    method: 'POST',
    headers: { 'Content-Type': 'application/x-www-form-urlencoded;charset=UTF-8' },
    body: qs.stringify(formData),
  };
  // 返回虚拟权限数据
  // return { data: { status: 200, permits: [] } };
 /* return request(`${API_BASE_SECURITY}/permission/user-permits`, option);*/

  return request(`${API_BASE_SECURITY}/permission/user-permits.shtml`, option);
}

// 退出登录
export async function loginOut(formData) {
  const option = {
    method: 'POST',
    headers: { 'Content-Type': 'application/x-www-form-urlencoded;charset=UTF-8' },
    body: qs.stringify(formData),
  };
  return request(`${API_BASE_SECURITY}/u/logout.shtml`, option);
}
