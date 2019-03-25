/**
 * 元数据定义相关接口
 * ! 由于历史原因，部分接口尚在metadata.js文件上，将逐步迁移到此文件
 */
import qs from 'qs';
import { API_BASE_METADATA } from '../constants';
import request from '../utils/request';

/* -======================== 以下是数据表类相关接口 ===========================- */

// 根据metaid获取历史信息中的字段信息
export async function getMetaTableBaseInfoByMetaId(id){
  return request(`${API_BASE_METADATA}/metadataTable/getTableBaseInfoByMetaId?metaid=${id}`);
}

// 获取某个版本的表字段
export async function getVersionDetails(id,version){
  return request(`${API_BASE_METADATA}/metadataPros/queryBakByMetaIdAndVersion?metaid=${id}&versionid=${version}`)
}
// 查询数据表类
export async function getMetaTableList(query, formData) {
  const querystring = qs.stringify(query);
  const option = {
    method: "POST",
    body: JSON.stringify({...formData}),
  };
  return request(`${API_BASE_METADATA}/metadataTable/search?${querystring}`, option);
}

// 查询数据表字段
export async function getFieldsById(query) {
  const querystring = qs.stringify(query);
  return request(`${API_BASE_METADATA}/metadataPros/queryByMetaId?${querystring}`);
}

// 创建/修改表基本信息
export async function createMetadata(formData) {
  const option = {
    method: "POST",
    body: JSON.stringify(formData),
  }
  return request(`${API_BASE_METADATA}/metadataTable/insert`, option);
}

// 检测表名是否已存在
export async function metaNameIsExists(formData) {
  const option = {
    method: "POST",
    body: JSON.stringify(formData),
  };
  return request(`${API_BASE_METADATA}/metadataTable/isExists`, option);
}

// 生成实体表
export async function createEntyTable(query) {
  const querystring = qs.stringify(query);
  return request(`${API_BASE_METADATA}/metadataTable/createEntityTable?${querystring}`);
}

// 修改数据表基本信息
export async function updateTableBase(formData) {
  const option = {
    method: "POST",
    body: JSON.stringify(formData),
  };
  return request(`${API_BASE_METADATA}/metadataTable/batchUpdate`, option);
}

// 新建数据表时 插入字段
export async function batchInsertFields(formData) {
  const option = {
    method: "POST",
    body: JSON.stringify(formData),
  };
  return request(`${API_BASE_METADATA}/metadataPros/batchInsert`, option);
}

// 修改数据表时 提交修改字段的字段
export async function batchUpdateFields(formData) {
  const option = {
    method: "POST",
    body: JSON.stringify(formData),
  }
  return request(`${API_BASE_METADATA}/metadataPros/Modify`, option);
}

// 修改数据表时 插入历史版本
export async function insertHisVersion(formData) {
  const option = {
    method: "POST",
    body: JSON.stringify(formData),
  }
  return request(`${API_BASE_METADATA}/metadataTable/InsertHisVersion`, option)
}

// 获取草稿箱列表
export async function getDraftsList(query, formData) {
  const querystring = qs.stringify(query);
  const option = {
    method: "POST",
    body: JSON.stringify(formData),
  }
  return request(`${API_BASE_METADATA}/metadataTable/getDraft?${querystring}`, option);
}

// 查询数据表类回收站
export async function getMetaTableRecycle(query, formData) {
  const querystring = qs.stringify(query);
  const option = {
    method: "POST",
    body: JSON.stringify({...formData, sourceId: 2}),
  };
  return request(`${API_BASE_METADATA}/metadataTable/getRecycle?${querystring}`, option);
}

// 数据表类移入回收站
export async function moveMetadataToRecycle(formData) {
  const option = {
    method: "PUT",
    body: JSON.stringify(formData),
  }
  return request(`${API_BASE_METADATA}/metadataTable/moveToRecycle`, option);
}

// 从回收站恢复数据表
export async function restoreTable(formData) {
  const option = {
    method: "PUT",
    body: JSON.stringify(formData),
  }
  return request(`${API_BASE_METADATA}/metadataTable/restore`, option);
}

// 永久删除数据表
export async function deleteTable(formData) {
  const option = {
    method: "PUT",
    body: JSON.stringify(formData),
  }
  return request(`${API_BASE_METADATA}/metadataTable/batchToDelete`, option);
}

// 获取历史版本列表
export async function getHistoryVersion(query) {
  const querystring = qs.stringify(query);
  return request(`${API_BASE_METADATA}/metadataHistory/getHistoryVersion?${querystring}`);
}

// 删除历史版本
export async function deleteHistoryVersion(query) {
  const querystring = qs.stringify(query);
  return request(`${API_BASE_METADATA}/metadataHistory/deleteHistoryVersion?${querystring}`);
}

/* -======================== 以下是文件目录相关接口 ===========================- */

// 文件目录列表
export async function getDirectoryList(query, formData) {
  const option = {
    method: "POST",
    body: JSON.stringify(formData)
  };
  const querystring = qs.stringify(query);
  return request(`${API_BASE_METADATA}/metadataFile/search?${querystring}`, option);
}

// 文件批量新建接口
export async function batchInsertFile(query, formData) {
  const querystring = qs.stringify(query);
  const option = {
    method: "POST",
    body: JSON.stringify(formData)
  };
  return request(`${API_BASE_METADATA}/metadataFile/batchInsert?${querystring}`, option);
}

// 文件批量修改接口
export async function batchUpdateFile(query, formData) {
  const querystring = qs.stringify(query);
  const option = {
    method: "POST",
    body: JSON.stringify(formData)
  };
  return request(`${API_BASE_METADATA}/metadataFile/batchUpdate?${querystring}`, option);
}

// 检测文件目录是否已被使用
export async function metaFileIsExists(formData) {
  const option = {
    method: "POST",
    body: JSON.stringify(formData),
  };
  return request(`${API_BASE_METADATA}/metadataFile/isExists`, option);
}

// 查询文件目录类回收站
export async function getMetaFileRecycle(query, formData) {
  const querystring = qs.stringify(query);
  const option = {
    method: "POST",
    body: JSON.stringify(formData),
  };
  return request(`${API_BASE_METADATA}/metadataFile/getRecycle?${querystring}`, option);
}

// 文件移入回收站T
export async function moveMetafileToRecycle(formData) {
  const option = {
    method: "PUT",
    body: JSON.stringify(formData)
  }
  return request(`${API_BASE_METADATA}/metadataFile/moveToRecycle`, option)
}

// 从回收站恢复文件
export async function restoreFile(formData) {
  const option = {
    method: "PUT",
    body: JSON.stringify(formData),
  }
  return request(`${API_BASE_METADATA}/metadataFile/batchToRecovery`, option);
}

// 从回收站删除文件
export async function deleteFile(formData) {
  const option = {
    method: "PUT",
    body: JSON.stringify(formData),
  }
  return request(`${API_BASE_METADATA}/metadataFile/batchToDelete`, option);
}

/* -======================== 以下是ES索引相关接口 ===========================- */

// ES列表
export async function getESList(query, formData) {
  const option = {
    method: "POST",
    body: JSON.stringify(formData)
  };
  const querystring = qs.stringify(query);
  // return {
  //   data: {
  //     rows: [],
  //     total: 0,
  //   }
  // }
  return request(`${API_BASE_METADATA}/esIndex/search?${querystring}`, option);
}

// ES索引详情
export async function getESDetail(query) {
  const querystring = qs.stringify(query);
  return request(`${API_BASE_METADATA}/esIndex/queryIndexInfoByIndexId?${querystring}`);
}

// 新建ES索引
export async function insertES(formData) {
  const option = {
    method: "POST",
    body: JSON.stringify(formData)
  };
  return request(`${API_BASE_METADATA}/esIndex/createIndex`, option);
}

// 编辑ES索引
export async function modifyES(formData) {
  const option = {
    method: "POST",
    body: JSON.stringify(formData)
  };
  return request(`${API_BASE_METADATA}/esIndex/upgrade`, option);
}

// 修改状态
export async function updateESStatus(formData) {
  const option = {
    method: 'POST',
    body: JSON.stringify(formData),
  };
  return request(`${API_BASE_METADATA}/esIndex/start-stop`, option);
}

// ES历史列表
export async function getESHistory(formData) {
  const option = {
    method: 'POST',
    body: JSON.stringify(formData),
  };
  // const querystring = qs.stringify(query);
  return request(`${API_BASE_METADATA}/esIndex/history`, option);
}

// 切换版本
export async function switchESVersion(formData) {
  const option = {
    method: 'POST',
    body: JSON.stringify(formData),
  };
  return request(`${API_BASE_METADATA}/esIndex/switchVersion`, option);
}

// 删除版本
export async function deleteESVersion(query) {
  const querystring = qs.stringify(query);
  return request(`${API_BASE_METADATA}/esIndex/delete?${querystring}`);
}

/* -======================== 以下是导入导出相关接口 ===========================- */

// 导出元数据事实表接口
export const exportMetadata = `${API_BASE_METADATA}/metadataTable/exportMetadata`;

// 导出元数据文件目录接口
export const exportMetafile = `${API_BASE_METADATA}/metadataFile/exportFileByIds`;

// 数据表导入接口
export const importMetadataTable = `${API_BASE_METADATA}/metadataTable/importMetadata`;

// 文件目录导入接口
export const importMetadatafile = `${API_BASE_METADATA}/metadataFile/importFile`;

// 上传样例接口
export const uploadExample = `${API_BASE_METADATA}/fileOperate/uploadExample`;

// ES导入接口
export const importES = `${API_BASE_METADATA}`;
// ES导出接口
export const exportES = `${API_BASE_METADATA}`;
