/**
 * 质量分析所有services接口
 * @author pwj 2018/9/27
 */

import request from 'utils/request';
import {
  API_BASE_GATHER,
  API_BASE_QUALITY,
  API_BASE_CATALOG,
  API_BASE_SECURITY,
  API_BASE_BI,
  API_BASE_DMP
} from "../constants";
import qs from "qs";

//调度任务成功失败次数
export async function getSchedulingQuality(obj) {
   return request(`${API_BASE_QUALITY}/statistics/getSummaryInfo`)
 }
//调度任务成功失败次数
export async function getScheduling(obj) {
  return request(`${API_BASE_GATHER}/statistics/getSummaryInfo` + "?type=job")
}
//调度任务成功列表
export async function getRenterSuccessTasks(obj) {
  return request(`${API_BASE_GATHER}/statistics/getRenterSuccessTasks` + "?page=1&pageSize=6")
}

//按月份统计过去一年的数量
export async function getTaskExecLinesByMonth(obj) {
  return request(`${API_BASE_GATHER}/statistics/getTaskExecLinesByMonth` + "?type=job")
}

//质量分析饼图图表 合格不合格
export async function getNodeTotal(obj) {
  return request(`${API_BASE_QUALITY}/statistics/getNodeTotal`)
}
//质量分析成功列表
export async function getQuailyRenterSuccessTasks(obj) {
  return request(`${API_BASE_QUALITY}/statistics/getRenterSuccessTasks` + "?page=1&pageSize=6")
}

//质量分析饼图图表 成功失败
export async function getTaskExecTimesByMonth(obj) {
  return request(`${API_BASE_QUALITY}/statistics/getTaskExecTimesByMonth`)
}

//质量分析成功列表 合格不合格
export async function getTaskByNodeType(obj) {
  return request(`${API_BASE_QUALITY}/statistics/getTaskByNodeType` + "?page=1&pageSize=6")
}
//统计资源注册量、订阅量、发布量
export async function getCount(obj) {
  const queryString = qs.stringify(obj);
  return request(`${API_BASE_CATALOG}/report/resources/count?${queryString}`)
}
//根据资源注册量、订阅量、发布量查询
export async function getresources(obj) {
  console.log(obj, "根据资源注册量、订阅量、发布量查询");
  const queryString = qs.stringify(obj);
  return request(`${API_BASE_CATALOG}/report/resources?${queryString}`)
}

//接口调用次数
export async function getCountByNumberOfCalls(obj) {
  const queryString = qs.stringify(obj);
  return request(`${API_BASE_CATALOG}/report/services/countByNumberOfCalls?${queryString}`)
}
//数据调用量
export async function getServerByTheAmountOfData(obj) {
  const queryString = qs.stringify(obj);
  return request(`${API_BASE_CATALOG}/report/services/countByTheAmountOfData?${queryString}`)
}
//上报任务数量
export async function getReptByNumberOfTasks(obj) {
  const queryString = qs.stringify(obj);
  return request(`${API_BASE_CATALOG}/report/data-report/countByNumberOfTasks?${queryString}`)
}
//上报数据量
export async function getCountByTheAmountOfData(obj) {
  const queryString = qs.stringify(obj);
  return request(`${API_BASE_CATALOG}/report/data-report/countByTheAmountOfData?${queryString}`)
}
//作业交换数据量
export async function getCountByNumberOfTasks(obj) {
  const queryString = qs.stringify(obj);
  return request(`${API_BASE_CATALOG}/report/exchange/countByTheAmountOfData?${queryString}`)
}
//作业交换作业量
export async function getNumberOfTasks(obj) {
  const queryString = qs.stringify(obj);
  return request(`${API_BASE_CATALOG}/report/exchange/countByNumberOfTasks?${queryString}`)
}

//查看服务调用列表 详情列表
export async function getServicesList(obj) {
  return request(`${API_BASE_CATALOG}/report/services` + "?serviceCode=" + obj.serviceCode)
}

//查看数据上报列表 详情列表
export async function getreportList(obj) {
  return request(`${API_BASE_CATALOG}/report/data-report` + "?deptCode=" + obj.deptCode)
}

//查看交换任务列表 详情列表
export async function getExchangeList(obj) {
  return request(`${API_BASE_CATALOG}/report/exchange` + "?deptId=" + obj.deptId)
}
//服务调用总次数、成功次数、失败次数
export async function getCountByNumberOfTimes(obj) {
   const queryString = qs.stringify(obj);
  return request(`${API_BASE_CATALOG}/report/services/countByNumberOfTimes?${queryString}`)
}
//登陆统计-获取当前租户下某年的登录用户数量月度统计
export async function getStatistics(obj) {
  return request(`${API_BASE_SECURITY}/login/count/show/monthly/statistics/` + obj.year + ".shtml")
}

//登陆统计-获取部门登录排行详情
export async function getStatisticsInfo(obj) {
  return request(`${API_BASE_SECURITY}/login/count/show/dept/login/info.shtml`)
}

//登陆统计-获取当前租户下用户的活跃信息
export async function getStatisticsUser(obj) {
  return request(`${API_BASE_SECURITY}/login/count/show/active/user.shtml`)
}
//展示用户列表，实现分页+模糊查询
export async function getSchemaInfo(obj) {
  return request(`${API_BASE_BI}/rest/statistics/schema/add/info`)
}
//新增一条记录
export async function getSchemaclick(obj) {
  const queryString = qs.stringify(obj);
  return request(`${API_BASE_BI}/rest/statistics/data/click?${queryString}`)
}
//神算子记录
export async function getDetail(obj) {
  return request(`${API_BASE_DMP}/statistics/detail`, {
    omit: true
  })
}

//接口服务详情
export async function getDetails(obj) {
   const queryString = qs.stringify(obj);
  return request(`${API_BASE_SECURITY}/login/count/show/login/details.shtml?${queryString}`)
}

