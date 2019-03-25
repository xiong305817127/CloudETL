/**
 * 数据分析子系统 - 全文检索
 */
import qs from 'qs';
import { API_BASE_ANALYSIS } from '../constants';
import request from '../utils/request';

// 获取选项列表
export async function getSolrOptions() {
  return request(`${API_BASE_ANALYSIS}/es/index/list`);
}

// 获取索引搜索结果
export async function getSolrFull(formData) {
  const option = {
    method: 'POST',
    headers: { 'Content-Type': 'application/x-www-form-urlencoded;charset=UTF-8' },
    body: qs.stringify(formData),
  };
  return request(`${API_BASE_ANALYSIS}/es/search/full`, option);
}

// 自定义搜索
export async function getSolrCustom(formData) {
  const option = {
    method: 'POST',
    headers: { 'Content-Type': 'application/x-www-form-urlencoded;charset=UTF-8' },
    body: qs.stringify(formData),
  };
  return request(`${API_BASE_ANALYSIS}/es/search/custom`, option);
}

// 索引信息
export async function getSolrMetadata(query) {
  const querystring = qs.stringify(query);
  return request(`${API_BASE_ANALYSIS}/es/search/metadata?${querystring}`);
}

// 获取搜索历史
export async function getSearchHistory(query) {
  const querystring = qs.stringify(query);
  return request(`${API_BASE_ANALYSIS}/es/search/history?${querystring}`);
}
