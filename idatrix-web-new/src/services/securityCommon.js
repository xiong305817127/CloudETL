/**
 * 安全子系统 - 组织机构管理
 */

import qs from 'qs';
import { API_BASE_SECURITY } from '../constants';
import request from '../utils/request';

// 获取组织列表
export async function getOrganizationList(query) {
  const querystring = qs.stringify({ ...query, pageSize: 10000000 });
  return request(`${API_BASE_SECURITY}/organization/list.shtml?${querystring}`);
}

//无需登录，通过cookie鉴权
export async function unNeedLoginPass(){
  return request(`${API_BASE_SECURITY}/u/login.shtml?notLogin=false`)
}

//检查密码是否重复
//@edit by pwj 
export async function checkPswd(query){
  return request(`${API_BASE_SECURITY}/token/comparePassword/${query}.shtml`)
}