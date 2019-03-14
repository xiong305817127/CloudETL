/**
 * 元数据公共信息接口
 */
import qs from 'qs';
import { API_BASE_METADATA } from '../constants';
import request from '../utils/request';

// 查询数据来源选项表
export async function getSourceTable(formData) {
  const option = {
    method: 'POST',
    body: JSON.stringify({ ...formData, sourceId: 1 }),
  };
  const querystring = qs.stringify({
    page: 1,
    rows: 10000000,
  });
  if (!formData.dsId) { // 无dsId，查库
    return request(`${API_BASE_METADATA}/dataSource/search?${querystring}`, option);
  } else { // 有dsId，查表
    return request(`${API_BASE_METADATA}/metadataTable/search?${querystring}`, option);
  }
}

// 采集列表渲染...采集
export async function CJLBlist(query) {
   const option = {
    method: 'POST',
    body: JSON.stringify(query),
  };
   const querystring = qs.stringify({
    page: 1,
    rows: 10000000,
  });
  return request(`${API_BASE_METADATA}/dataSource/searchDs?${querystring}`, option);
}

// 查询数据来源选项表...采集
export async function getAcquisition(query) {
   const option = {
    method: 'POST',
    body: JSON.stringify(query),
  };
   const querystring = qs.stringify({
    page: 1,
    rows: 10000000,
  });
  return request(`${API_BASE_METADATA}/metadataTable/search?${querystring}`, option);
}

// 查询部门选项表
export async function getDepartments(query) {
  const querystring = qs.stringify(query);
  return request(`${API_BASE_METADATA}/userInfo/findDeptByUserId?${querystring}`);
}


// 查询存储的数据库
export async function getStoreDatabase(formData) {
  const option = {
    method: 'POST',
    body: JSON.stringify(formData),
  };
  const querystring = qs.stringify({
    page: 1,
    rows: 10000000,
  });
  return request(`${API_BASE_METADATA}/dataSource/search?${querystring}`, option);
}

export async function getStoreDatabaseAcquition(formData){
   const option = {
    method: 'POST',
    body: JSON.stringify(formData),
  };
  const querystring = qs.stringify({
    page: 1,
    rows: 10000000,
  });
  return request(`${API_BASE_METADATA}/dataSource/search?${querystring}`, option);
}

// 查询表拥有者
export async function getUserByRenterId(query) {
  const querystring = qs.stringify(query);
  return request(`${API_BASE_METADATA}/userInfo/findUserByRenterId?${querystring}`);
}

// 查询行业、标签
export async function getAllResource(query) {
  return request(`${API_BASE_METADATA}/dataResource/getAllResource`);
}

// 查询hdfs树形
export async function getHdfsTree(formData) {
  const option = {
    method: "POST",
    body: JSON.stringify(formData)
  }
  return request(`${API_BASE_METADATA}/hdfs/search`, option);
}

/**
 *  通过数据库信息，查询schema信息
 *  author pwj 
 */
export async function getDbSchemasByDsId(dsId) {
  return request(`${API_BASE_METADATA}/dataSwap/getDbSchemas/${dsId}`);
}
/*
   根据库去查询的schema
   2018 10 11   alisa
 */

export async function getSchemasByDsId(dsId) {
  return request(`${API_BASE_METADATA}/dataSwap/getSchemasByDsId/${dsId}`);
}

/*
   根据schema去查询的对应的表
   2018 10 11   alisa
 */

export async function getRentTablesBySchemaId(query) {
    const querystring = qs.stringify(query);
  return request(`${API_BASE_METADATA}/dataSwap/getRentTablesBySchemaId/?${querystring}`);
}