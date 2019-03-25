/**
 * 数据资源目录 - 我的资源接口
 */
import qs from 'qs';
import { API_BASE_RESOURCE } from '../constants';
import request from '../utils/request';

// 搜索数据表类
export async function searchTable(query) {
  const queryString = qs.stringify(Object.assign({}, query, { datatype: 1 }));
  return request(`${API_BASE_RESOURCE}/myResource/search?${queryString}`);
}

// 搜索文件目录类
export async function searchFile(query) {
  const queryString = qs.stringify(Object.assign({}, query, { datatype: 2 }));
  return request(`${API_BASE_RESOURCE}/myResource/search?${queryString}`);
}

// 获取数据表类
export async function getMeta(query) {
  const queryString = qs.stringify(query);
  return request(`${API_BASE_RESOURCE}/metadataPros/queryByMetaId?${queryString}`);
}

// 获取目录类
export async function getDir(query) {
  const queryString = qs.stringify(query);
  return request(`${API_BASE_RESOURCE}/metadataFile/queryByFileId?${queryString}`);
}
