/**
 * 数据分析子系统 - 任务管理
 */
import qs from 'qs';
import { API_BASE_ANALYSIS } from '../constants';
import request from '../utils/request';

// 上传jar文件接口
export const jarUploadApi = `${API_BASE_ANALYSIS}/task/definition/upload`;

// 获取任务列表
export async function getTaskList(data) {
  const querystring = qs.stringify(data);
  const query = querystring ? `?${querystring}` : '';
  return request(`${API_BASE_ANALYSIS}/task/definition/getTasks${query}`);
}

// 查看任务详情
export async function viewTask(id) {
  return request(`${API_BASE_ANALYSIS}/task/definition/view/${id}`);
}

// 查看任务执行状态
export async function getTaskNodeInfo(query) {
  const querystring = qs.stringify(query);
  return request(`${API_BASE_ANALYSIS}/executor/getTaskNodeInfo?${querystring}`);
}

// 新增任务
export async function addTask(obj) {
  const option = {
    method: 'POST',
    body: JSON.stringify(obj),
  };
  return request(`${API_BASE_ANALYSIS}/task/definition/createTask`, option);
}

// 修改任务
export async function modifyTask(obj) {
  const option = {
    method: 'POST',
    body: JSON.stringify(obj),
  };
  return request(`${API_BASE_ANALYSIS}/task/definition/updateTaskAndFlow`, option);
}

// 删除任务
export async function deleteTask(obj) {
  const querystring = qs.stringify(obj);
  return request(`${API_BASE_ANALYSIS}/task/definition/deleteTask?${querystring}`);
}

// 执行任务
export async function startTask(id) {
  return request(`${API_BASE_ANALYSIS}/executor/startTask?taskid=${id}`);
}

// 暂停任务
export async function pauseTask(id) {
  return request(`${API_BASE_ANALYSIS}/executor/pauseTask?taskid=${id}`);
}

// 恢复任务
export async function resumeTask(id) {
  return request(`${API_BASE_ANALYSIS}/executor/resumeTask?taskid=${id}`);
}

// 取消任务
export async function cancelTask(id) {
  return request(`${API_BASE_ANALYSIS}/executor/cancelTask?taskid=${id}`);
}

// 创建调度
export async function createTaskSchedule(obj) {
  const option = {
    method: 'POST',
    body: JSON.stringify(obj),
  };
  return request(`${API_BASE_ANALYSIS}/executor/createTaskSchedule`, option);
}

// 删除调度
export async function deleteScheduleTask(query) {
  const querystring = qs.stringify(query);
  return request(`${API_BASE_ANALYSIS}/executor/deleteTaskSchedule?${querystring}`);
}

// 获取任务统计报表
export async function getReport(id) {
  return request(`${API_BASE_ANALYSIS}/executor/statTask?taskid=${id}`);
}

// 查看任务执行log
export async function getTaskExecLog(query) {
  const querystring = qs.stringify(query);
  return request(`${API_BASE_ANALYSIS}/executor/getTaskExecLog?${querystring}`);
}

// 查看任务节点执行log
export async function getNodeExecLog(query) {
  const querystring = qs.stringify(query);
  return request(`${API_BASE_ANALYSIS}/executor/getNodeExecLog?${querystring}`);
}
