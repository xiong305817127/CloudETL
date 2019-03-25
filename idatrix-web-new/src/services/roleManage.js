import qs from 'qs';
import { API_BASE_SECURITY } from '../constants';
import request from '../utils/request';

// 1.获取列表
export async function getList(data) {
  const querystring = qs.stringify(data);
  const query = querystring ? `?${querystring}` : '';
  return request(`${API_BASE_SECURITY}/role/list.shtml${query}`);
}
//2.新建角色
export async function newRole(obj) {
  const option = {
    method: 'POST',
    headers: { 'Content-Type': 'application/x-www-form-urlencoded;charset=UTF-8' },
    body: qs.stringify(obj),
  };
  return request(`${API_BASE_SECURITY}/role/add.shtml`, option);
}
// 3.上传/导入
export async function roleUploadApi(obj) {
  const option = {
    method: "POST",
    body: qs.stringify(obj)
  };
  return request(`${API_BASE_SECURITY}/member/import.shtml`, option);
}
// 4.下载/导出:直接在页面输出
export const roleDownloadApi = `${API_BASE_SECURITY}/member/export.shtml`;

// 5.修改角色
export async function roleUpdateApi(obj) {
  obj['_method'] = 'put';
  const option = {
    method: "POST",
    headers: { 'Content-Type': 'application/x-www-form-urlencoded;charset=UTF-8' },
    body: qs.stringify(obj)
  };
  return request(`${API_BASE_SECURITY}/role/update.shtml`, option);
}
// 6.删除角色
export async function roleDeleteApi(obj) {
  const option = {
    method: "POST",
    headers: { 'Content-Type': 'application/x-www-form-urlencoded;charset=UTF-8' },
    body: qs.stringify(obj)
  };
  return request(`${API_BASE_SECURITY}/role/deleteRoleById.shtml`, option);
}
// 7.分配角色成员
export async function roleMemberApi(obj) {
  const option = {
    method: "POST",
    body: qs.stringify(obj)
  };
  return request(`${API_BASE_SECURITY}/role/addRole2User.shtml`, option);
}
// 8.查询id角色信息
export async function roleIdApi(obj) {
  const option = {
    method: "POST",
    body: qs.stringify(obj)
  };
  return request(`${API_BASE_SECURITY}/role/selectRoleByUserId.shtml`, option);
}
//9.获取资源管理的权限：'资源管理'授权
export async function roleJurisdictionApi(obj) {
  const option = {
    method: "POST",
    headers: { 'Content-Type': 'application/x-www-form-urlencoded;charset=UTF-8' },
    body: qs.stringify(obj)
  };
  return request(`${API_BASE_SECURITY}/permission/addPermission2Role.shtml`, option);
}
// 根据角色id查权限
export async function getPermissionById(id) {
  return request(`${API_BASE_SECURITY}/permission/selectPermissionById.shtml?id=${id}`);
}

// 根据角色id查询用户列表
export async function getUsersByRoleId(id) {
  const option = {
    method: "POST",
    headers: { 'Content-Type': 'application/x-www-form-urlencoded;charset=UTF-8' },
    body: qs.stringify({
      roleId: id,
    })
  };
  return request(`${API_BASE_SECURITY}/member/findUsersByRoleId`, option);
}

// 添加多个用户到角色
export async function addUsersToRole(formData) {
  const option = {
    method: "POST",
    headers: { 'Content-Type': 'application/x-www-form-urlencoded;charset=UTF-8' },
    body: qs.stringify(formData)
  };
  return request(`${API_BASE_SECURITY}/role/addUserToRole`, option);
}
