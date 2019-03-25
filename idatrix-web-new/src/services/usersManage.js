import qs from 'qs';
import { API_BASE_SECURITY } from '../constants';
import request from '../utils/request';
//1.用户数据：搜索
export async function getList(data) {
  const querystring = qs.stringify(data);
  const query = querystring ? `?${querystring}` : '';
  return request(`${API_BASE_SECURITY}/member/users.shtml${query}`);
}
//2.新建用户
export async function newUser(obj) {
  const option = {
    method: 'POST',
    headers: { 'Content-Type': 'application/x-www-form-urlencoded;charset=UTF-8' },
    body: qs.stringify(obj),
  };
  return request(`${API_BASE_SECURITY}/member/add.shtml`, option);
}
//3.修改用户信息
export async function updateUser(obj) {
  const option = {
    method: 'POST',
    headers: { 'Content-Type': 'application/x-www-form-urlencoded;charset=UTF-8' },
    body: qs.stringify(obj),
  };
  return request(`${API_BASE_SECURITY}/member/update.shtml`, option);
}
//4.删除用户信息
export async function deleteUser(obj) {
  const option = {
    method: 'POST',
    headers: { 'Content-Type': 'application/x-www-form-urlencoded;charset=UTF-8' },
    body: qs.stringify(obj),
  };
  return request(`${API_BASE_SECURITY}/member/delete.shtml`, option);
}

//5.其他：导入导出路径直接在页面设置
    // 5.1下载/导出:
// export const userDownloadApi = `${API_BASE_SECURITY}/member/export.shtml`;
    // 5.2批量导入：上传
// export async function userUploadApi(obj) {
//   const option = {
//     method: "POST",
//     headers: { 'Content-Type': 'application/x-www-form-urlencoded;charset=UTF-8' },
//     body: qs.stringify(obj)
//   };
//   return request(`${API_BASE_SECURITY}/member/import.shtml`, option);
// }

// 获取所有用户
export async function getAllUserList(data) {
  return request(`${API_BASE_SECURITY}/member/users.shtml?pageSize=1000000`);
}

// 通过用户id获取用户所属角色
export async function getRolesByUserId(userId) {
  const option = {
    method: 'POST',
    headers: { 'Content-Type': 'application/x-www-form-urlencoded;charset=UTF-8' },
    body: qs.stringify({ userId }),
  };
  return request(`${API_BASE_SECURITY}/role/findRolesByUserId`, option);
}

// 添加多用户至组织机构
export async function addUserToOrg(obj) {
  const option = {
    method: 'POST',
    headers: { 'Content-Type': 'application/x-www-form-urlencoded;charset=UTF-8' },
    body: qs.stringify(obj),
  };
  return request(`${API_BASE_SECURITY}/organization/addUserToOrg.shtml`, option);
}

// 添加组织到用户
export async function addOrgToUser(obj) {
  const option = {
    method: 'POST',
    headers: { 'Content-Type': 'application/x-www-form-urlencoded;charset=UTF-8' },
    body: qs.stringify(obj),
  };
  return request(`${API_BASE_SECURITY}/organization/addOrgToUser`, option);
}

// 添加多用户至角色
export async function addUsersToRoles(obj) {
  const option = {
    method: 'POST',
    headers: { 'Content-Type': 'application/x-www-form-urlencoded;charset=UTF-8' },
    body: qs.stringify(obj),
  };
  return request(`${API_BASE_SECURITY}/role/addUsersToRoles.shtml`, option);
}

