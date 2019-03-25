/**
 * 数据分析bi项目API
 */

import qs from 'qs';
import { API_BASE_BI,API_BASE_METADATA } from '../constants';
import request from '../utils/request';


//打开资源目录getDatabaseList
export async function getLoginPrmit(query) {
  const option = {
    method: 'POST',
    headers: { 'Content-Type': 'application/x-www-form-urlencoded;charset=UTF-8' },
    body: qs.stringify(query),
  };
  return request(`${API_BASE_BI}/rest/saiku/session`,option);
}


// 获取模型文件夹
export async function getModelList(query) {
  return request(`${API_BASE_BI}/rest/category/list/${query.renterId}`);
}

// 一键导入
export async function importToSaiku() {
  const option = {
    method: 'POST',
  };
  return request(`${API_BASE_BI}/rest/schema/importToSaiku`,option);
}

//批量删除模型
export async function deleteModel(query) {
  const option = {
    method: 'DELETE',
    body: JSON.stringify(query),
  };
  return request(`${API_BASE_BI}/rest/schema/batchDelete`,option);
}


// 新增目录
export async function addNewFolder(query) {
  const option = {
    method: 'POST',
    body: JSON.stringify(query),
  };
  return request(`${API_BASE_BI}/rest/category`, option);
}

//修改目录
export async function editFolder(query) {
  const option = {
    method: 'PUT',
    body: JSON.stringify(query),
  };
  return request(`${API_BASE_BI}/rest/category`, option);
}

//批量删除目录
export async function deleteFolder(query) {
  const option = {
    method: 'DELETE',
    body: JSON.stringify(query),
  };
  return request(`${API_BASE_BI}/rest/category`, option);
}

//判断当前用户是否存在改目录
export async function isExistFolder(query) {
  const {name,renterId} = query;
  return request(`${API_BASE_BI}/rest/category/isExist/${name}/${renterId}`);
}

//判断当前用户是否存在schema
export async function isExistName(query) {
  const {name,renterId} = query;
  return request(`${API_BASE_BI}/rest/schema/isExist/${name}/${renterId}`);
}

//批量删除目录
export async function batchDeleteFolder(query) {
  return request(`${API_BASE_BI}/rest/category`);
}

//获取scheama
export async function getDatabaseList(query) {
  return request(`${API_BASE_BI}/rest/meta/data/database/list/${query}`);
}

//获取table
export async function getTableList(query) {
  return request(`${API_BASE_BI}/rest/meta/data/table/list/${query}`);
}

//获取table fields字段
export async function getFieldsList(query) {
  return request(`${API_BASE_BI}/rest/meta/data/column/list/${query}`);
}


//批量删除目录
export async function insertSchema(query) {
  const option = {
    method: 'POST',
    body: JSON.stringify(query),
  };

  console.log(JSON.stringify(query),"格式化");

  return request(`${API_BASE_BI}/rest/schema/insertSchema`,option);
}

//打开资源目录
export async function openSchema(query) {
  return request(`${API_BASE_BI}/rest/schema/${query.id}`);
}

//编辑转换
export async function editSchema(query) {
  const option = {
    method: 'PUT',
    body: JSON.stringify(query),
  };
  return request(`${API_BASE_BI}/rest/schema`,option);
}

//获取scheama 内表的数量
export async function getRentTablesBySchemaId(query) {
	const querystring = qs.stringify(query);
  return request(`${API_BASE_METADATA}/dataSwap/getRentTablesBySchemaId?${querystring}`);
}

