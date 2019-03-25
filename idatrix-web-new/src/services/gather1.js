/*job请求*/

import qs from "qs";
// 1.主端口API_BASE:/
import {
  API_BASE_GATHER,
  STANDALONE_ETL,
  API_BASE_QUALITY
} from "../constants";
import request from "../utils/gatherRequest";
import requests from "../utils/request";

const url = STANDALONE_ETL ? "/cloudetl" : API_BASE_GATHER;

/*Job请求Url*/
const runJob = {
  /*初始化*/
  job_list: "/cloud/getJobList.do", //调度列表
  getJob_group: "/job/getJobGroups.do", // 获取调度分组

  /*复制节点*/
  copyStep: "/entry/copyEntry.do",

  /*转换流程*/
  open_job: "/job/openJob.do",

  edit_job_attributes: "/job/editJobAttributes.do",
  save_job_attributes: "/job/saveJobAttributes.do",

  new_job: "/job/newJob.do",
  check_job_name: "/job/checkJobName.do",
  delete_job: "/job/deleteJob.do",
  job_exec_result: "/job/execJob.do", //执行 有问题
  job_exec_step_measure: "/job/getEntryMeasure.do",
  job_exec_id: "/job/getExecId.do",
  job_exec_step_status: "/job/getEntryStatus.do",

  getExecInfo: "/job/getExecInfo.do",

  job_status: "/job/getJobStatus.do", //步骤状态
  job_exec_log: "/job/getExecLog.do",
  job_exec_finished: "/job/getExecStatus.do",
  exec_stop: "/job/execStop.do",

  get_job_records: "/job/getJobRecords.do", //得到job执行历史
  get_job_log: "/job/getJobLogs.do", //得到job执行日志

  /*节点连接*/
  add_hop: "/hop/addHop.do",
  delete_hop: "/hop/deleteHop.do",

  /*步骤操作*/
  add_entry: "/entry/addEntry.do",
  check_entry_name: "/entry/checkEntryName.do",
  edit_entry: "/entry/editEntry.do",
  save_entry: "/entry/saveEntry.do",
  delete_entry: "/entry/deleteEntry.do",
  move_entry: "/entry/moveEntry.do",

  /*获取job详情*/
  getJobDetails: "/entry/getDetails.do",

  /*批量执行*/
  exec_BatchJob: "/job/execBatchJob.do",

  /*批量停止*/
  batchStop: "/job/execBatchStop",

  //获取数据字典查询   alisa修改
  dataDictList: "/analysis/dictList.do",
  //获取数据字典修改  alisa修改
  getdataDict: "/analysis/dataDict/",
  //获取数据字典修改   alisa修改
  postdataDict: "/analysis/dataDict/update.do",
  //获取数据字典导入   alisa修改
  dataDict: "/analysis/dataDict/update.do",
  //获取数据字典导出   alisa修改
  analysisCsvFile: "/analysis/analysisCsvFile.do",

  //新增字典接口 Alisa增加
  GetdictNew: "/analysis/dict.do",

  //新建页面新增 保存接口
  GetSibmitdictNew: "/analysis/dict.do",
  //编辑标准值页面查询
  GetdictAllList: "/analysis/dictAllList.do",

  //新建页面新增 保存接口
  GetdictData: "/analysis/dictData/update.do",

  //首页面状态执行
  GetdictDatastatus: "/analysis/dict/status.do",
  //保存字典名称
  Getupdate: "/analysis/dict/update.do"
};
//新建页面新增 保存接口
export async function Getupdate(obj) {
  const option = {
    method: "POST",
    body: JSON.stringify(obj)
  };
  return request(API_BASE_QUALITY + runJob.Getupdate, option);
}
//新建页面新增 保存接口
export async function GetdictDatastatus(obj) {
  const option = {
    method: "POST",
    body: JSON.stringify(obj)
  };
  return request(API_BASE_QUALITY + runJob.GetdictDatastatus, option);
}

//新建页面新增 保存接口
export async function GetdictData(obj) {
  const option = {
    method: "POST",
    body: JSON.stringify(obj)
  };
  return request(API_BASE_QUALITY + runJob.GetdictData, option);
}
//编辑标准值页面查询
export async function GetdictAllList(query, obj) {
  return requests(
    `${API_BASE_QUALITY}/analysis/dictDataList/${query.name}.do?` +
      "page=" +
      query.page,
    "size=" + query.size
  );
}

//新建页面新增 保存接口
export async function GetSibmitdictNew(obj) {
  const option = {
    method: "POST",
    body: JSON.stringify(obj)
  };
  return request(API_BASE_QUALITY + runJob.GetSibmitdictNew, option);
}

/*获取数据字典查询  alisa修改*/
export async function dataDictList(obj) {
  const option = {
    method: "GET",
    body: JSON.stringify(obj)
  };
  return request(API_BASE_QUALITY + runJob.dataDictList, option);
}

/*获取数据字典编辑  alisa修改*/
export async function getdataDict(query) {
  return requests(`${API_BASE_QUALITY}/analysis/dataDict/${query.name}.do`);
}

//新建页面新增 按id查询，获取复制字典名称的下拉列表
export async function getdictNewlist(query) {
  console.log(query, "query======");
  return requests(`${API_BASE_QUALITY}/analysis/dict/${query.id}.do`);
}
/*获取数据字典导出  alisa修改*/
/*export async function analysisCsvFile(query) {
    return requests(`${API_BASE_QUALITY}/analysis/analysisCsvFile/${query.id}.do`)
}
*/
/*获取数据字典保存   alisa修改*/
export async function postdataDict(obj) {
  const option = {
    method: "POST",
    body: JSON.stringify(obj)
  };
  return request(API_BASE_QUALITY + runJob.postdataDict, option);
}
/*获取数据字典导出   alisa修改*/
export async function analysisCsvFile(obj) {
  const option = {
    method: "POST",
    body: JSON.stringify(obj)
  };
  return request(API_BASE_QUALITY + runJob.analysisCsvFile, option);
}

/*获取数据字典导出   alisa修改*/
export async function GetdictNew(obj) {
  const option = {
    method: "POST",
    body: JSON.stringify(obj)
  };
  return request(API_BASE_QUALITY + runJob.GetdictNew, option);
}

/*批量停止*/
export async function batchJobsStop(obj) {
  const option = {
    method: "POST",
    headers: { "Content-Type": "application/json;charset=UTF-8" },
    credentials: "include",
    body: JSON.stringify(obj)
  };
  return request(url + runJob.batchStop, option);
}

/*请求信息合并*/
export async function getJobExecInfo(obj) {
  const option = {
    method: "POST",
    headers: { "Content-Type": "application/json;charset=UTF-8" },
    credentials: "include",
    body: JSON.stringify(obj)
  };
  return request(url + runJob.getExecInfo, option);
}

/*复制步骤*/
export async function copyStep(obj) {
  const option = {
    method: "POST",
    headers: { "Content-Type": "application/json;charset=UTF-8" },
    credentials: "include",
    body: JSON.stringify(obj)
  };
  return request(url + runJob.copyStep, option);
}

/*批量执行*/
export async function execBatchJob(obj) {
  const option = {
    method: "POST",
    headers: { "Content-Type": "application/json;charset=UTF-8" },
    credentials: "include",
    body: JSON.stringify(obj)
  };
  return request(url + runJob.exec_BatchJob, option);
}

/*得到job的执行记录*/
export async function get_JobRecords(name) {
  const option = {
    method: "POST",
    headers: { "Content-Type": "application/json;charset=UTF-8" },
    credentials: "include",
    body: JSON.stringify(
      typeof name == "object"
        ? name
        : {
            name: name
          }
    )
  };
  return request(url + runJob.get_job_records, option);
}

/*得到job的执行记录*/
export async function get_JobLog(obj) {
  const option = {
    method: "GET",
    body: JSON.stringify(obj)
  };
  return request(url + runJob.get_job_log, option);
}

/*获取job详情*/
export async function getJobDetailsList(obj) {
  const option = {
    method: "POST",
    headers: { "Content-Type": "application/json;charset=UTF-8" },
    credentials: "include",
    body: JSON.stringify(obj)
  };
  return request(url + runJob.getJobDetails, option);
}

/*任务列表*/
export async function getJob_list(obj) {
  const option = {
    method: "GET",
    body: JSON.stringify(obj)
  };
  return request(url + runJob.job_list, option);
}
/*任务列表*/
export async function getJob_group(obj) {
  const option = {
    method: "GET",
    body: JSON.stringify(obj)
  };
  return request(url + runJob.getJob_group, option);
}

/*打开转换*/
export async function getOpen_job(name) {
  const option = {
    method: "POST",
    headers: { "Content-Type": "application/json;charset=UTF-8" },
    credentials: "include",
    body: JSON.stringify(
      typeof name === "string"
        ? {
            name: name
          }
        : name
    )
  };

  console.log("打开转换");
  console.log(option.body);

  return request(url + runJob.open_job, option);
}

export async function getEdit_job_attributes(name) {
  const option = {
    method: "POST",
    headers: { "Content-Type": "application/json;charset=UTF-8" },
    credentials: "include",
    body: JSON.stringify(
      typeof name !== "object"
        ? {
            name: name
          }
        : name
    )
  };
  console.log("编辑转换");
  console.log(option.body);
  return request(url + runJob.edit_job_attributes, option);
}

export async function getSave_job_attributes(name) {
  const option = {
    method: "POST",
    headers: { "Content-Type": "application/json;charset=UTF-8" },
    credentials: "include",
    body: JSON.stringify({
      name: name.name,
      newName: name.newname,
      params: name.params,
      description: name.description,
      ...name
    })
  };
  console.log("保存转换");
  console.log(option.body);
  return request(url + runJob.save_job_attributes, option);
}

export async function getNew_job(name) {
  const option = {
    method: "POST",
    headers: { "Content-Type": "application/json;charset=UTF-8" },
    credentials: "include",
    body: JSON.stringify({
      name: name.info_name,
      newName: name.copy_name ? name.copy_name : "",
      description: name.description,
      ...name
    })
  };
  console.log("新建转换");
  console.log(option.body);
  return request(url + runJob.new_job, option);
}

export async function getCheck_job_name(name) {
  const option = {
    method: "POST",
    headers: { "Content-Type": "application/json;charset=UTF-8" },
    credentials: "include",
    body: JSON.stringify({
      name: name
    })
  };

  console.log("检测名字");
  console.log(option.body);
  return request(url + runJob.check_job_name, option);
}

export async function getDelete_job(query) {
  const option = {
    method: "POST",
    headers: { "Content-Type": "application/json;charset=UTF-8" },
    credentials: "include",
    body: JSON.stringify(query)
  };
  return request(url + runJob.delete_job, option);
}

export async function Job_exec_configuration(obj) {
  const option = {
    method: "POST",
    headers: { "Content-Type": "application/json;charset=UTF-8" },
    credentials: "include",
    body: JSON.stringify(obj)
  };

  return request(url + runJob.job_exec_result, option);
}
/*获取步骤统计*/
export async function getJob_exec_step_measure(id) {
  console.log(id);
  const option = {
    method: "POST",
    headers: { "Content-Type": "application/json;charset=UTF-8" },
    credentials: "include",
    body: JSON.stringify({
      executionId: id
    })
  };

  console.log("获取步骤统计");
  console.log(option.body);
  return request(url + runJob.job_exec_step_measure, option);
}
/*根据名字 获取id*/
export async function getJob_exec_id(name) {
  const option = {
    method: "POST",
    headers: { "Content-Type": "application/json;charset=UTF-8" },
    credentials: "include",
    body: JSON.stringify(typeof name !== "object" ?{
      name: name
    } : name
    )
  };
  console.log("获取id");
  console.log(option.body);

  return request(url + runJob.job_exec_id, option);
}

/*根据名字 获取执行状态*/
export async function getJob_status(name) {
  const option = {
    method: "POST",
    headers: { "Content-Type": "application/json;charset=UTF-8" },
    credentials: "include",
    body: JSON.stringify(typeof name === "object"? name :{
      name: name
    })
  };
  console.log(option.body);
  return request(url + runJob.job_status, option);
}

/*步骤状态*/
export async function getJob_exec_step_status(id) {
  const option = {
    method: "POST",
    headers: { "Content-Type": "application/json;charset=UTF-8" },
    credentials: "include",
    body: JSON.stringify({
      executionId: id
    })
  };

  console.log("步骤状态");
  console.log(option.body);
  return request(url + runJob.job_exec_step_status, option);
}

/*执行是否结束*/
export async function getJob_exec_finished(id) {
  const option = {
    method: "POST",
    headers: { "Content-Type": "application/json;charset=UTF-8" },
    credentials: "include",
    body: JSON.stringify({
      executionId: id
    })
  };

  console.log("执行是否结束");
  console.log(option.body);
  return request(url + runJob.job_exec_finished, option);
}

/*获取日志*/
export async function getJob_exec_log(id) {
  const option = {
    method: "POST",
    headers: { "Content-Type": "application/json;charset=UTF-8" },
    credentials: "include",
    body: JSON.stringify({
      executionId: id
    })
  };

  console.log("获取日志");
  console.log(option.body);

  return request(url + runJob.job_exec_log, option);
}

/*添加hop节点*/
export async function add_hop(obj) {
  const option = {
    method: "POST",
    headers: { "Content-Type": "application/json;charset=UTF-8" },
    credentials: "include",
    body: JSON.stringify(obj)
  };

  console.log("添加hop节点");
  console.log(option.body);
  return request(url + runJob.add_hop, option);
}

/*反转hop节点*/
export async function invert_hop(obj) {
  console.log(obj);
  const option = {
    method: "POST",
    headers: { "Content-Type": "application/json;charset=UTF-8" },
    credentials: "include",
    body: JSON.stringify({
      name: obj.transname,
      from: obj.start,
      to: obj.target,
      enabled: true
    })
  };

  console.log("反转hop节点");
  console.log(option.body);
  return request(url + runJob.invert_hop, option);
}

/*删除hop节点*/
export async function delete_hop(obj) {
  console.log(obj);
  const option = {
    method: "POST",
    headers: { "Content-Type": "application/json;charset=UTF-8" },
    credentials: "include",
    body: JSON.stringify({
      ...obj,
      enabled: true,
      isJob: true
    })
  };

  console.log("删除hop节点");
  console.log(option.body);
  return request(url + runJob.delete_hop, option);
}

/*添加step*/
export async function add_entry(obj) {
  const option = {
    method: "POST",
    headers: { "Content-Type": "application/json;charset=UTF-8" },
    credentials: "include",
    body: JSON.stringify(obj)
  };

  console.log("添加step");
  console.log(option.body);
  return request(url + runJob.add_entry, option);
}

/*编辑step*/
export async function edit_entry(obj) {
  const option = {
    method: "POST",
    headers: { "Content-Type": "application/json;charset=UTF-8" },
    credentials: "include",
    body: JSON.stringify(obj)
  };

  return request(url + runJob.edit_entry, option);
}

/*保存step*/
export async function save_entry(obj) {
  console.log(obj);
  const option = {
    method: "POST",
    headers: { "Content-Type": "application/json;charset=UTF-8" },
    credentials: "include",
    body: JSON.stringify(obj)
  };
  console.log("保存step");
  console.log(option.body);
  return request(url + runJob.save_entry, option);
}

/*检查step名*/
export async function check_entry_name(obj) {
  const option = {
    method: "POST",
    headers: { "Content-Type": "application/json;charset=UTF-8" },
    credentials: "include",
    body: JSON.stringify(obj)
  };

  console.log("检查job名");
  console.log(option.body);
  return request(url + runJob.check_entry_name, option);
}

/*删除step*/
export async function delete_entry(obj) {
  const option = {
    method: "POST",
    headers: { "Content-Type": "application/json;charset=UTF-8" },
    credentials: "include",
    body: JSON.stringify(obj)
  };

  return request(url + runJob.delete_entry, option);
}

/*移动step*/
export async function move_entry(obj) {
  console.log(obj);
  const option = {
    method: "POST",
    headers: { "Content-Type": "application/json;charset=UTF-8" },
    credentials: "include",
    body: JSON.stringify(obj)
  };

  console.log("移动step");
  console.log(option.body);

  return request(url + runJob.move_entry, option);
}

/*暂停执行*/

export async function get_exec_pause(executionId) {
  const option = {
    method: "POST",
    headers: { "Content-Type": "application/json;charset=UTF-8" },
    credentials: "include",
    body: JSON.stringify({
      executionId: executionId
    })
  };
  console.log("暂停执行");
  console.log(option.body);

  return request(url + runJob.exec_pause, option);
}
/*恢复执行*/
export async function get_exec_resume(executionId) {
  const option = {
    method: "POST",
    headers: { "Content-Type": "application/json;charset=UTF-8" },
    credentials: "include",
    body: JSON.stringify({
      executionId: executionId
    })
  };
  console.log("恢复执行");
  console.log(option.body);

  return request(url + runJob.exec_resume, option);
}
/*终止执行*/

export async function getJob_exec_stop(executionId) {
  const option = {
    method: "POST",
    headers: { "Content-Type": "application/json;charset=UTF-8" },
    credentials: "include",
    body: JSON.stringify(typeof executionId === "object" ? executionId : {
      executionId: executionId
    })
  };

  console.log("终止执行");
  console.log(option.body);
  return request(url + runJob.exec_stop, option);
}
